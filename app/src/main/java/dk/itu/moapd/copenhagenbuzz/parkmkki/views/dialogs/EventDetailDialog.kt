package dk.itu.moapd.copenhagenbuzz.parkmkki.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventDetailDialog : DialogFragment() {

    private lateinit var event: Event

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        event = requireArguments().getParcelable("event")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.event_row_item, container, false)

        val name = view.findViewById<TextView>(R.id.text_field_event_name)
        val type = view.findViewById<TextView>(R.id.text_field_event_type)
        val location = view.findViewById<TextView>(R.id.text_field_event_location)
        val date = view.findViewById<TextView>(R.id.text_field_event_date)
        val description = view.findViewById<TextView>(R.id.text_field_event_description)
        val image = view.findViewById<ImageView>(R.id.event_image)
        val alarm = view.findViewById<Button>(R.id.alarm_button)
        val info = view.findViewById<Button>(R.id.info_button)
        val favorite = view.findViewById<ImageButton>(R.id.favorite_button)
        val unfavorite = view.findViewById<ImageButton>(R.id.unfavorite_button)
        alarm.visibility = View.GONE
        info.visibility = View.GONE
        favorite.visibility = View.GONE
        unfavorite.visibility = View.GONE

        name.text = event.eventName
        type.text = event.eventType
        location.text = event.eventLocation.address
        date.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(event.eventDate))
        description.text = event.eventDescription

        Glide.with(requireContext())
            .load(event.eventImagePath)
            .placeholder(R.drawable.test_image)
            .error(R.drawable.test_image)
            .into(image)

        return view
    }

    companion object {
        fun newInstance(event: Event): EventDetailDialog {
            val args = Bundle().apply {
                putParcelable("event", event)
            }
            return EventDetailDialog().apply { arguments = args }
        }
    }
}
