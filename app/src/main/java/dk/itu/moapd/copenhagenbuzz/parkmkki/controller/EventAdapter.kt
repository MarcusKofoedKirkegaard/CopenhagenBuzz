/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event

/**
 * Adapter for displaying a list of events in a RecyclerView.
 *
 * @property eventList The list of [Event] objects to be displayed.
 */
class EventAdapter(private val eventList: MutableList<Event>, private val viewModel: DataViewModel) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    /**
     * ViewHolder for an individual event item in the RecyclerView.
     *
     * @param view The root view of the event item layout.
     */
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventName: TextView = view.findViewById(R.id.text_field_event_name)
        val eventType: TextView = view.findViewById(R.id.text_field_event_type)
        val eventLocation: TextView = view.findViewById(R.id.text_field_event_location)
        val eventDate: TextView = view.findViewById(R.id.text_field_event_date)
        val eventDescription: TextView = view.findViewById(R.id.text_field_event_description)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val likeButton: ImageButton = view.findViewById(R.id.like_button)
        val unlikeButton: ImageButton = view.findViewById(R.id.unlike_button)
    }

    /**
     * Creates a new [EventViewHolder] when needed.
     *
     * @param parent The parent ViewGroup that holds the item views.
     * @param viewType The view type of the new View.
     * @return A new instance of [EventViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_row_item, parent, false)
        return EventViewHolder(view)
    }

    /**
     * Binds event data to the ViewHolder.
     *
     * @param holder The ViewHolder that should be updated.
     * @param position The position of the item within the dataset.
     */
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.eventName.text = event.eventName
        holder.eventType.text = event.eventType
        holder.eventLocation.text = event.eventLocation
        holder.eventDate.text = event.eventDate.toString()
        holder.eventDescription.text = event.eventDescription
        holder.eventImage.id = event.eventImageId

        // Set initial visibility based on favorite state
        holder.likeButton.visibility = if (event.isFavorite) View.GONE else View.VISIBLE
        holder.unlikeButton.visibility = if (event.isFavorite) View.VISIBLE else View.GONE

        holder.likeButton.setOnClickListener {
            event.isFavorite = true
            this.viewModel.updateEvent(event)
            notifyItemChanged(holder.adapterPosition)
        }

        holder.unlikeButton.setOnClickListener {
            event.isFavorite = false
            this.viewModel.updateEvent(event)
            notifyItemChanged(holder.adapterPosition) // Refresh the item
        }
    }

    /**
     * Returns the total number of items in the dataset.
     *
     * @return The size of [eventList].
     */
    override fun getItemCount(): Int {
        return eventList.size
    }
}
