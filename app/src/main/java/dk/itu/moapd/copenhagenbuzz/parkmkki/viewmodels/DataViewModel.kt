package dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.utils.AlarmScheduler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import io.github.cdimascio.dotenv.dotenv

class DataViewModel : ViewModel() {

    private val DATABASE_URL: String = dotenv {
        directory = "/assets"
        filename = "env"
    }["DATABASE_URL"]


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance(DATABASE_URL).reference
    private val storage = FirebaseStorage.getInstance().reference.child("event_images")

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _favoritedEventKeys = MutableLiveData<Set<String>>(setOf())
    val favoritedEventKeys: LiveData<Set<String>> = _favoritedEventKeys

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun getCurrentUser() = auth.currentUser



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

    suspend fun refreshFavoritedEvents() {
        val userId = getCurrentUser()?.uid ?: return
        val snapshot = database.child("favorites").child(userId).get().await()
        val keys = snapshot.children.mapNotNull { it.key }.toSet()
        _favoritedEventKeys.postValue(keys)
    }

    fun isEventFavoritedLocally(eventKey: String): Boolean {
        return favoritedEventKeys.value?.contains(eventKey) ?: false
    }

    override fun onCleared() {
        super.onCleared()
        database.child("events").removeEventListener(eventListener)
    }

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

    private suspend fun uploadImage(key: String, data: ByteArray): String {
        val imageName = "${System.currentTimeMillis()}.jpg"
        val ref = storage.child(key).child(imageName)
        ref.putBytes(data).await()
        return ref.downloadUrl.await().toString()
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

    fun scheduleEventAlarm(context: Context, eventKey: String, event: Event) {
        AlarmScheduler.scheduleAlarm(context, eventKey, event)
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("alarm_set_$eventKey", true).apply()
    }

    fun cancelEventAlarm(context: Context, eventKey: String) {
        AlarmScheduler.cancelAlarm(context, eventKey)
        val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("alarm_set_$eventKey", false).apply()
    }
}
