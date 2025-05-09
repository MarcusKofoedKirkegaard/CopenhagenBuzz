/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.controller.CustomAdapter
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.OnItemClickListener
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.DataViewModel
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event

/**
 * A fragment that displays a timeline of events using a RecyclerView.
 *
 * This fragment observes the shared [DataViewModel] and updates the RecyclerView
 * whenever the event data changes.
 */
class TimelineFragment : Fragment(), OnItemClickListener {

    /**
     * ViewModel instance shared across the activity to observe event data.
     */
    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var adapter: CustomAdapter

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    /**
     * Called after the view has been created.
     *
     * Sets up the RecyclerView with a [LinearLayoutManager] and observes LiveData
     * from [DataViewModel] to update the adapter with event data.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState Saved instance state bundle.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Coonects to the database and rerads events
        FirebaseAuth.getInstance().currentUser?.let {
            user ->
            val query = Firebase.database("https://moapd-2025-793fd-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("events").child(user.uid).orderByChild("createdAt")

            val options = FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(query, Event::class.java)
                .setLifecycleOwner(viewLifecycleOwner)
                .build()

            adapter = CustomAdapter(this, options)

            val recyclerView = view.findViewById<RecyclerView>(R.id.timeline_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
        }
    }

    override fun onItemClickListener(dummy: Event, position: Int) {
        TODO("Not yet implemented")
    }
}