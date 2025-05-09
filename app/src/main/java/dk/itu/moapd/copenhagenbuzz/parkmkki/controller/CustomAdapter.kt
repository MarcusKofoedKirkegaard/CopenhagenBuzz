package dk.itu.moapd.copenhagenbuzz.parkmkki.controller

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.EventRowItemBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Image
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.OnItemClickListener

/**
 * Custom adapter that binds data from Firebase Realtime Database to a RecyclerView.
 * This adapter listens for long clicks on each item and triggers a custom item click listener.
 *
 * @property itemClickListener A custom listener to handle item click events.
 * @property options FirebaseRecyclerOptions containing the list of Event objects.
 */
class CustomAdapter(
    private val itemClickListener: OnItemClickListener, // Listener to handle item clicks.
    options: FirebaseRecyclerOptions<Event> // Options for FirebaseRecyclerAdapter to load Event data.
) : FirebaseRecyclerAdapter<Event, CustomAdapter.ViewHolder>(options) {

    /**
     * ViewHolder for binding an Event item to a RecyclerView item view.
     * This holds the binding reference to avoid finding the views repeatedly.
     */
    class ViewHolder(private val binding: EventRowItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an Event object to the corresponding view.
         *
         * @param event The Event object to bind.
         */
        fun bind(event: Event) {
            binding.textFieldEventName.text = event.eventName // Bind the event name to the text view.
        }

        /**
         * Binds an Image object to the corresponding view.
         * (Currently not implemented, but this can be extended for handling image data.)
         *
         * @param image The Image object to bind.
         */
        fun bind(image: Image) {
            // TODO: Implement binding logic for Image object when needed.
        }
    }

    /**
     * Creates and returns a ViewHolder that will hold the views of each item.
     * Inflates the event row layout and initializes the ViewHolder with the binding.
     *
     * @param parent The parent ViewGroup to which the new view will be attached.
     * @param viewType The view type of the item.
     * @return A new ViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        EventRowItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false) // Inflate the event row layout.
            .let(::ViewHolder) // Return a new ViewHolder instance with the inflated layout.

    /**
     * Binds the data for an Event to the specified ViewHolder.
     * Sets up the click listener for long click events on the item.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     * @param event The Event object to bind to the item at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, event: Event) {
        Log.d(TAG, "Populate an item at position: $position") // Log for debugging the position.

        // Bind the event data to the view holder.
        event.let(holder::bind)

        // Set a long click listener for the item view to trigger item click events.
        holder.itemView.setOnLongClickListener {
            itemClickListener.onItemClickListener(event, holder.absoluteAdapterPosition) // Trigger item click listener.
            true // Return true to indicate the event was handled.
        }
    }
}
