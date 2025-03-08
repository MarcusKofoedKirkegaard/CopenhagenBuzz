package dk.itu.moapd.copenhagenbuzz.parkmkki.model
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

class DataViewModel : ViewModel() {
    private lateinit var eventData: MutableLiveData<List<Event>>

}