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
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event

/**
 * Adapter for displaying a list of events in a RecyclerView.
 *
 * @property eventList The list of [Event] objects to be displayed.
 */
class EventAdapter(
    private val context: Context,
    private val eventList: MutableList<Event>,
    private val viewModel: DataViewModel) :
    BaseAdapter() {

    override fun getCount(): Int = eventList.size

    override fun getItem(position: Int): Any = eventList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.event_row_item, parent, false)

        val eventName: TextView = view.findViewById(R.id.text_field_event_name)
        val eventType: TextView = view.findViewById(R.id.text_field_event_type)
        val eventLocation: TextView = view.findViewById(R.id.text_field_event_location)
        val eventDate: TextView = view.findViewById(R.id.text_field_event_date)
        val eventDescription: TextView = view.findViewById(R.id.text_field_event_description)
        val eventImage: ImageView = view.findViewById(R.id.event_image)
        val likeButton: ImageButton = view.findViewById(R.id.like_button)
        val unlikeButton: ImageButton = view.findViewById(R.id.unlike_button)

        val event = eventList[position]
        eventName.text = event.eventName
        eventType.text = event.eventType
        eventLocation.text = event.eventLocation
        eventDate.text = event.eventDate.toString()
        eventDescription.text = event.eventDescription
        eventImage.setImageResource(event.eventImageId)

        // Set initial visibility based on favorite state
        likeButton.visibility = if (event.isFavorite) View.GONE else View.VISIBLE
        unlikeButton.visibility = if (event.isFavorite) View.VISIBLE else View.GONE

        likeButton.setOnClickListener {
            event.isFavorite = true
            viewModel.updateEvent(event)
            notifyDataSetChanged()
        }

        unlikeButton.setOnClickListener {
            event.isFavorite = false
            viewModel.updateEvent(event)
            notifyDataSetChanged()
        }

        return view
    }
}
