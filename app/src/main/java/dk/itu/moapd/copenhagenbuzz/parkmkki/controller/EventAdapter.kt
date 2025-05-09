/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.EventLocation
import java.util.Locale

/**
 * Adapter used to display a list of [Event] objects in a list view.
 * This adapter is responsible for inflating views, binding data, and managing interactions
 * such as liking/unliking events.
 *
 * @property context The [Context] used to access system resources and services.
 * @property eventList A mutable list of [Event] objects to be displayed in the list view.
 * @property viewModel The [DataViewModel] that manages data updates, including favorite status changes for events.
 */
class EventAdapter(
    private val context: Context,
    private val eventList: MutableList<Event>,
    private val viewModel: DataViewModel
) : BaseAdapter() {

    /**
     * Returns the number of events in the [eventList].
     *
     * @return The size of the [eventList].
     */
    override fun getCount(): Int = eventList.size

    /**
     * Returns the event at the specified position in the list.
     *
     * @param position The position of the item within the dataset.
     * @return The [Event] at the given position.
     */
    override fun getItem(position: Int): Any = eventList[position]

    /**
     * Returns the ID of the event at the specified position.
     * In this case, the position is used as the unique ID for each item.
     *
     * @param position The position of the item within the dataset.
     * @return The ID of the event at the given position.
     */
    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Creates and returns a view for a specific item in the list.
     * This method is called for each item in the list to bind data and set up any interactions,
     * such as updating the event's favorite status.
     *
     * @param position The position of the item in the list.
     * @param convertView A recycled view that can be reused, or null if a new view should be created.
     * @param parent The parent view that this view will be attached to.
     * @return The view for the item at the specified position.
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the view if it hasn't been recycled
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.event_row_item, parent, false)

        // Find the views in the layout
        val eventName: TextView = view.findViewById(R.id.text_field_event_name)
        val eventType: TextView = view.findViewById(R.id.text_field_event_type)
        val eventLocation: TextView = view.findViewById(R.id.text_field_event_location)
        val eventDate: TextView = view.findViewById(R.id.text_field_event_date)
        val eventDescription: TextView = view.findViewById(R.id.text_field_event_description)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val likeButton: ImageButton = view.findViewById(R.id.like_button)
        val unlikeButton: ImageButton = view.findViewById(R.id.unlike_button)
        val thumbsupButton: ImageButton = view.findViewById(R.id.thumbsup_button)
        val thumbsupCounter: TextView = view.findViewById(R.id.thumbsup_counter)
        val editButton: MaterialButton = view.findViewById(R.id.button_event_edit)

        // Get the event at the current position
        val event = eventList[position]

        // Bind the event data to the views
        eventName.text = event.eventName
        eventType.text = event.eventType
        eventLocation.text = event.eventLocation.address
        eventDate.text = event.eventDate.toString()
        eventDescription.text = event.eventDescription
        event.eventImagePath?.let { imageUrl ->
            FirebaseStorage.getInstance().reference
                .child(imageUrl).downloadUrl.addOnSuccessListener { url ->
                    Picasso.get().load(url).into(eventImage)
                }
        }

        // Set visibility of like/unlike buttons based on the event's favorite status
        likeButton.visibility = if (event.isFavorite) View.GONE else View.VISIBLE
        unlikeButton.visibility = if (event.isFavorite) View.VISIBLE else View.GONE
        editButton.visibility = if (event.eventCreator == FirebaseAuth.getInstance().currentUser?.uid.toString()) View.VISIBLE else View.GONE

        // Set onClick listeners to toggle favorite status
        likeButton.setOnClickListener {
            event.isFavorite = true
            viewModel.updateEvent(event)  // Update the event's favorite status in the ViewModel
            notifyDataSetChanged()  // Notify the adapter that the data has changed
        }

        unlikeButton.setOnClickListener {
            event.isFavorite = false
            viewModel.updateEvent(event)  // Update the event's favorite status in the ViewModel
            notifyDataSetChanged()  // Notify the adapter that the data has changed
        }

        thumbsupButton.setOnClickListener {
            event.eventThumbsUp += 1
            thumbsupCounter.text = String.format(Locale.getDefault(), "%d", event.eventThumbsUp)
            viewModel.updateEvent(event)
            notifyDataSetChanged()
        }

        return view
    }
}
