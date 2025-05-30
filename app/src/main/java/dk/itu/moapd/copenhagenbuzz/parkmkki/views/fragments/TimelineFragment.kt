package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.firebase.ui.database.FirebaseListOptions
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.adapters.EventAdapter
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event


/**
 * A fragment that displays a timeline of events using a RecyclerView.
 *
 * This fragment observes the shared [DataViewModel] and updates the RecyclerView
 * whenever the event data changes.
 */
class TimelineFragment : Fragment() {

    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var adapter: EventAdapter
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.timeline_list_view)

        FirebaseAuth.getInstance().currentUser?.let { user ->
            val query = viewModel.database.child("events").orderByChild("eventDate")

            val options = FirebaseListOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLayout(R.layout.event_row_item)
                .setLifecycleOwner(viewLifecycleOwner)
                .build()

            adapter = EventAdapter(context, childFragmentManager, viewModel, options)
            listView.adapter = adapter
        }

        viewModel.favoritedEventKeys.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }
}
