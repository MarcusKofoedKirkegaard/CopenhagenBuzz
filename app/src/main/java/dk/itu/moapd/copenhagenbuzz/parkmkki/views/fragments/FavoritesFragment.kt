package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseListOptions
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.database.values
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.adapters.EventAdapter
import dk.itu.moapd.copenhagenbuzz.parkmkki.adapters.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.tasks.await

/**
 * A fragment that displays a timeline of events using a RecyclerView.
 *
 * This fragment observes the shared [DataViewModel] and updates the RecyclerView
 * whenever the event data changes.
 */
class FavoritesFragment : Fragment() {

    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.favorites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        FirebaseAuth.getInstance().currentUser?.let { user ->
            val favoritesQuery = Firebase.database("https://moapd-2025-793fd-default-rtdb.europe-west1.firebasedatabase.app/")
                .reference.child("favorites").child(user.uid)
                .orderByChild("eventDate")

            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(favoritesQuery, Event::class.java)
                .setLifecycleOwner(viewLifecycleOwner)
                .build()

            adapter = FavoriteAdapter(viewModel, options)
            recyclerView.adapter = adapter
            adapter.startListening()
        }
    }
}
