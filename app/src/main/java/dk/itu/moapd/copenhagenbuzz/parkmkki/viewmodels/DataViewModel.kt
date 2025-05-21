package dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class DataViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        "https://moapd-2025-793fd-default-rtdb.europe-west1.firebasedatabase.app/"
    ).reference

    private val storage = FirebaseStorage.getInstance().reference.child("event_images")

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events


    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _favoritedEventKeys = MutableLiveData<Set<String>>(setOf())
    val favoritedEventKeys: LiveData<Set<String>> = _favoritedEventKeys

    suspend fun refreshFavoritedEvents() {
        val userId = getCurrentUser()?.uid ?: return
        val snapshot = database.child("favorites").child(userId).get().await()
        val keys = snapshot.children.mapNotNull { it.key }.toSet()
        _favoritedEventKeys.postValue(keys)
    }

    fun isEventFavoritedLocally(eventKey: String): Boolean {
        return favoritedEventKeys.value?.contains(eventKey) ?: false
    }

    private val eventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loadedEvents = mutableListOf<Event>()
            snapshot.children.forEach { childSnapshot ->
                val event = childSnapshot.getValue(Event::class.java)
                event?.let {
                    loadedEvents.add(it)
                }
            }
            _events.postValue(loadedEvents)
        }

        override fun onCancelled(error: DatabaseError) {
            _errorMessage.postValue("Failed to load events: ${error.message}")
        }
    }

    init {
        database.child("events").addValueEventListener(eventListener)
    }

    override fun onCleared() {
        super.onCleared()
        database.child("events").removeEventListener(eventListener)
    }

    fun getCurrentUser() = auth.currentUser

    fun addEvent(event: Event, imageByteArray: ByteArray) {
        viewModelScope.launch {
            try {
                val key = database.child("events").push().key ?: UUID.randomUUID().toString()
                val imageUrl = uploadImage(key, imageByteArray)
                event.eventImagePath = imageUrl
                database.child("events").child(key).setValue(event).await()
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to add event: ${e.message}")
            }
        }
    }

    fun favoriteEvent(eventKey: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUser()?.uid
                    ?: throw IllegalStateException("User must be logged in to favorite events.")

                val eventSnapshot = database.child("events").child(eventKey).get().await()
                val event = eventSnapshot.getValue(Event::class.java)
                    ?: throw IllegalStateException("Event not found")

                database.child("favorites").child(userId).child(eventKey).setValue(event).await()

                refreshFavoritedEvents()
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to favorite event: ${e.message}")
            }
        }
    }

    fun unFavoriteEvent(eventKey: String) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUser()?.uid
                    ?: throw IllegalStateException("User must be logged in to favorite events.")

                database.child("favorites").child(userId).child(eventKey).removeValue().await()
                refreshFavoritedEvents()
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to un-favorite event: ${e.message}")
            }
        }
    }

    fun isEventFavorited(eventKey: String, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return callback(false)

        database.child("favorites").child(userId).child(eventKey).get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.exists())
            }
            .addOnFailureListener {
                _errorMessage.postValue("Error checking favorite: ${it.message}")
                callback(false)
            }
    }


    private suspend fun uploadImage(key: String, data: ByteArray): String {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val ref = storage.child(key).child(imageName)
        ref.putBytes(data).await()
        return ref.downloadUrl.await().toString()
    }

}
