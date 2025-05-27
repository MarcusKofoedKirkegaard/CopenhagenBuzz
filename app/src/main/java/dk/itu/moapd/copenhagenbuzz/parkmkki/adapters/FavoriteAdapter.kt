package dk.itu.moapd.copenhagenbuzz.parkmkki.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FavoriteRowItemBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel

/**
 * Adapter for the favorites list using FirebaseRecyclerAdapter and RecyclerView.
 *
 * @constructor Accepts FirebaseRecyclerOptions to provide real-time updates.
 */
class FavoriteAdapter(private val dataViewModel: DataViewModel, options: FirebaseRecyclerOptions<Event>) :
    FirebaseRecyclerAdapter<Event, FavoriteAdapter.ViewHolder>(options) {

    class ViewHolder(private val binding: FavoriteRowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds an [Event] to the view using ViewBinding.
         */
        fun bind(event: Event, eventKey: String?, dataViewModel: DataViewModel ) {
            with(binding) {
                textFieldFavoriteName.text = event.eventName
                textFieldFavoriteType.text = event.eventType

                Glide.with(favoriteEventImage.context)
                    .load(event.eventImagePath)
                    .placeholder(R.drawable.test_image)
                    .error(R.drawable.test_image)
                    .into(favoriteEventImage)

                if (eventKey == null) {
                    unfavoriteButton.visibility = View.GONE
                    return
                }

                unfavoriteButton.setOnClickListener {
                    dataViewModel.unFavoriteEvent(eventKey)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoriteRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Event) {
        val eventKey = getRef(position).key
        holder.bind(model, eventKey, dataViewModel)
    }
}
