package dk.itu.moapd.copenhagenbuzz.parkmkki.adapters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val eventDate: TextView = view.findViewById(R.id.text_field_event_date)
        val eventDescription: TextView = view.findViewById(R.id.text_field_event_description)
        val favoriteButton: View = view.findViewById(R.id.favorite_button)
        val unFavoriteButton: View = view.findViewById(R.id.unfavorite_button)
        val image: ImageView = view.findViewById(R.id.event_image)
        val alarmButton: View = view.findViewById(R.id.alarm_button)
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
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        eventDate.text = sdf.format(Date(event.eventDate))
        eventDescription.text = event.eventDescription

        Glide.with(image.context)
            .load(event.eventImagePath)
            .placeholder(R.drawable.test_image)
            .error(R.drawable.test_image)
            .into(image)

        if (eventKey == null) {
            favoriteButton.visibility = View.GONE
            unFavoriteButton.visibility = View.GONE
            return
        }

        val prefs = alarmButton.context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val isAlarmSet = prefs.getBoolean("alarm_set_$eventKey", false)

        val millisUntilEvent = event.eventDate - System.currentTimeMillis()
        if (millisUntilEvent > 0) {
            if (isAlarmSet) {

                val seconds = (millisUntilEvent / 1000) % 60
                val minutes = (millisUntilEvent / (1000 * 60)) % 60
                val hours = (millisUntilEvent / (1000 * 60 * 60))

                (alarmButton as? TextView)?.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                alarmButton.isEnabled = true
            } else {
                (alarmButton as? TextView)?.text = "Set Alarm"
                alarmButton.isEnabled = true
            }
        } else {
            (alarmButton as? TextView)?.text = "Event Started"
            alarmButton.isEnabled = false
        }



        alarmButton.setOnClickListener {
            if (isAlarmSet) {
                dataViewModel.cancelEventAlarm(it.context, eventKey)
            } else {
                dataViewModel.scheduleEventAlarm(it.context, eventKey, event)
            }

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
