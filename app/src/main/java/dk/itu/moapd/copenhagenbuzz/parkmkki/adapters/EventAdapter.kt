package dk.itu.moapd.copenhagenbuzz.parkmkki.adapters

import android.view.View
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel

/**
 * Adapter for the Timeline ListView using FirebaseListAdapter.
 *
 * @property dataViewModel ViewModel for shared data access.
 * @constructor Accepts FirebaseListOptions for managing event data.
 */
class EventAdapter(
    private val dataViewModel: DataViewModel,
    options: FirebaseListOptions<Event>
) : FirebaseListAdapter<Event>(options) {

    private class ViewHolder(view: View) {
        val eventName: TextView = view.findViewById(R.id.text_field_event_name)
        val eventType: TextView = view.findViewById(R.id.text_field_event_type)
        val eventLocation: TextView = view.findViewById(R.id.text_field_event_location)
    }

    override fun populateView(v: View, model: Event, position: Int) {
        val viewHolder = ViewHolder(v)
        viewHolder.bind(model)
    }

    private fun ViewHolder.bind(event: Event) {
        eventName.text = event.eventName
        eventType.text = event.eventType
        eventLocation.text = event.eventLocation.toString()
    }
}
