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
    var options: FirebaseListOptions<Event>
) : FirebaseListAdapter<Event>(options) {

    private class ViewHolder(view: View) {
        val eventName: TextView = view.findViewById(R.id.text_field_event_name)
        val eventType: TextView = view.findViewById(R.id.text_field_event_type)
        val eventLocation: TextView = view.findViewById(R.id.text_field_event_location)
        val eventDate: TextView = view.findViewById(R.id.text_field_event_date)
        val eventDescription: TextView = view.findViewById(R.id.text_field_event_description)
        val favoriteButton: View = view.findViewById(R.id.favorite_button)
        val unFavoriteButton: View = view.findViewById(R.id.unfavorite_button)
    }

    override fun populateView(v: View, model: Event, position: Int) {
        val viewHolder = ViewHolder(v)
        val eventKey = getRef(position).key

        viewHolder.bind(model, eventKey)
    }

    private fun ViewHolder.bind(event: Event, eventKey: String?) {
        eventName.text = event.eventName
        eventType.text = event.eventType
        eventLocation.text = event.eventLocation.address
        eventDate.text = event.eventDate.toString() // Måske brug string format i stedet
        eventDescription.text = event.eventDescription

        if (eventKey == null) {
            favoriteButton.visibility = View.GONE
            unFavoriteButton.visibility = View.GONE
            return
        }

        // Observe favorite status live
        val isFavorited = dataViewModel.isEventFavoritedLocally(eventKey)

        favoriteButton.visibility = if (isFavorited) View.GONE else View.VISIBLE
        unFavoriteButton.visibility = if (isFavorited) View.VISIBLE else View.GONE



        favoriteButton.setOnClickListener {
            eventKey.let {
                dataViewModel.favoriteEvent(it)
            }
        }

        unFavoriteButton.setOnClickListener {
            eventKey.let {
                dataViewModel.unFavoriteEvent(it)
            }
        }
    }
}
