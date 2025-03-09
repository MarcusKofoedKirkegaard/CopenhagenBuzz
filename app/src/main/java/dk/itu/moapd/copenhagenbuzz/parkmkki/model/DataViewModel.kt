package dk.itu.moapd.copenhagenbuzz.parkmkki.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import kotlinx.coroutines.*
import java.time.LocalDate

class DataViewModel : ViewModel() {
    private val _eventData = MutableLiveData<List<Event>>().apply {
        value = listOf(
            Event("Concert Night", "copenhagen", LocalDate.now(), "Concert", "Enjoy live music at the city square", R.drawable.test_image),
            Event("Food Festival", "copenhagen", LocalDate.now(), "Concert", "Taste delicious street food from local vendors", R.drawable.test_image),
            Event("Art Exhibition", "copenhagen", LocalDate.now(), "Concert", "Explore the latest contemporary art collections", R.drawable.test_image)
        )
    }

    val eventData: LiveData<List<Event>> get() = _eventData
}