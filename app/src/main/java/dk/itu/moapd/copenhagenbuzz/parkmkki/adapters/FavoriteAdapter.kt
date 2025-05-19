package dk.itu.moapd.copenhagenbuzz.parkmkki.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FavoriteRowItemBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event

/**
 * Adapter for the favorites list using FirebaseRecyclerAdapter and RecyclerView.
 *
 * @constructor Accepts FirebaseRecyclerOptions to provide real-time updates.
 */
class FavoriteAdapter(options: FirebaseRecyclerOptions<Event>) :
    FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    class ViewHolder(private val binding: FavoriteRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an [Event] to the view using ViewBinding.
         */
        fun bind(event: Event) {
            with(binding) {
                textFieldFavoriteName.text = event.eventName
                textFieldFavoriteType.text = event.eventType
                // Uncomment if you integrate an image loader like Glide or Picasso:
                // Glide.with(root).load(event.imageUrl).into(favoriteEventImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoriteRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        holder.bind(model)
    }
}
