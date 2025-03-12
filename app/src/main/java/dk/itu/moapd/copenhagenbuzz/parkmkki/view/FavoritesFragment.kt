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
import dk.itu.moapd.copenhagenbuzz.parkmkki.R
import dk.itu.moapd.copenhagenbuzz.parkmkki.controller.FavoriteAdapter
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.DataViewModel

/**
 * A fragment that displays a timeline of events using a RecyclerView.
 *
 * This fragment observes the shared [DataViewModel] and updates the RecyclerView
 * whenever the event data changes.
 */
class FavoritesFragment : Fragment() {

    /**
     * ViewModel instance shared across the activity to observe event data.
     */
    private val viewModel: DataViewModel by activityViewModels()

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
        return inflater.inflate(R.layout.fragment_favorites, container, false)
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

        val recyclerView: RecyclerView = view.findViewById(R.id.favorites_recycler_view)

        // Set LayoutManager for RecyclerView (Linear layout for a list view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData from ViewModel and update RecyclerView adapter when data changes
        viewModel.favoritesData.observe(viewLifecycleOwner) { favorites ->
            val adapter = FavoriteAdapter(favorites)
            recyclerView.adapter = adapter
        }
    }
}