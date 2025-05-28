package dk.itu.moapd.copenhagenbuzz.parkmkki.adapters

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.views.dialogs.EventDetailDialog
import dk.itu.moapd.copenhagenbuzz.parkmkki.views.dialogs.EventEditDialog
import dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments.AddEventFragment
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
    private val fragmentManager: FragmentManager,
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
        val infoButton: Button = view.findViewById(R.id.info_button)
        val editButton: Button = view.findViewById(R.id.edit_button)
        var countdownTimer: CountDownTimer? = null

        fun clearTimer() {
            countdownTimer?.cancel()
        }
    }

    override fun populateView(v: View, model: Event, position: Int) {

        val viewHolder: ViewHolder = v.tag as? ViewHolder ?: ViewHolder(v).also { v.tag = it }
        val eventKey = getRef(position).key

        viewHolder.clearTimer()
        viewHolder.bind(model, eventKey)
    }

    private fun ViewHolder.bind(event: Event, eventKey: String?) {
        if(eventKey == null) return
        val isFavorited = dataViewModel.isEventFavoritedLocally(eventKey)
        eventName.text = event.eventName
        eventType.text = event.eventType
        eventLocation.text = event.eventLocation.address
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        eventDate.text = sdf.format(Date(event.eventDate))
        eventDescription.text = event.eventDescription

        if (image.tag != event.eventImagePath) {
            image.tag = event.eventImagePath
            Glide.with(image.context)
                .load(event.eventImagePath)
                .placeholder(R.drawable.test_image)
                .error(R.drawable.test_image)
                .into(image)
        }

        val prefs = alarmButton.context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)
        val isAlarmSet = prefs.getBoolean("alarm_set_$eventKey", false)

        val millisUntilEvent = event.eventDate - System.currentTimeMillis()
        if (millisUntilEvent > 0) {
            if (isAlarmSet) {
                countdownTimer = object : CountDownTimer(millisUntilEvent, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = (millisUntilFinished / 1000) % 60
                        val minutes = (millisUntilFinished / (1000 * 60)) % 60
                        val hours = (millisUntilFinished / (1000 * 60 * 60))

                        (alarmButton as? TextView)?.text =
                            String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        alarmButton.isEnabled = true
                    }

                    override fun onFinish() {
                        (alarmButton as? TextView)?.text = "Event Started"
                        alarmButton.isEnabled = false
                    }
                }.start()
            } else {
                (alarmButton as? TextView)?.text = "Set Alarm"
                alarmButton.isEnabled = true
            }
        } else {
            (alarmButton as? TextView)?.text = "Event Started"
            alarmButton.isEnabled = false
        }

        alarmButton.setOnClickListener {
            val context = it.context
            val prefs = context.getSharedPreferences("alarm_prefs", Context.MODE_PRIVATE)

            if (prefs.getBoolean("alarm_set_$eventKey", false)) {
                clearTimer()
                dataViewModel.cancelEventAlarm(context, eventKey)
            } else {
                dataViewModel.scheduleEventAlarm(context, eventKey, event)
            }

            bind(event, eventKey)
        }

        if(event.eventCreator != FirebaseAuth.getInstance().currentUser?.uid.toString()) editButton.visibility = View.GONE

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

        editButton.setOnClickListener {
            EventEditDialog.newInstance(eventKey, event)
                .show(fragmentManager, "event_edit")
        }

        infoButton.setOnClickListener {
            EventDetailDialog.newInstance(event)
                .show(fragmentManager, "event_detail")
        }
    }

    override fun getItem(position: Int): Event {
        return super.getItem(count - position - 1)
    }

    override fun getRef(position: Int): DatabaseReference {
        return super.getRef(count - position - 1)
    }
}
