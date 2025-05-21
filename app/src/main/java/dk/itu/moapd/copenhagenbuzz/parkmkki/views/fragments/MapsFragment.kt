package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FragmentMapsBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel

class MapsFragment : Fragment() {


    private var _binding: FragmentMapsBinding? = null
    private val viewModel: DataViewModel by activityViewModels()

    private val binding
        get() = requireNotNull(_binding) {
            "Binding error"
        }

    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        val itu = LatLng(55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions().position(itu).title("IT University of Copenhagen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itu, 15f)) // <- Added zoom
        googleMap.setPadding(0, 100, 0, 0)

        if (checkPermission()) {
            try {
                googleMap.isMyLocationEnabled = true // ✅ Enable current location marker

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val currentLatLng = LatLng(it.latitude, it.longitude)
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itu, 5f))
                        }
                    }
                }
                viewModel.events.observe(viewLifecycleOwner) { events ->
                    events.forEach { event ->
                        val location = event.eventLocation
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(event.eventName)
                        )
                    }
                }
            } catch (e: SecurityException) {
                // Should never happen with permission check, but safe to log
                e.printStackTrace()
            }
        } else {
            requestUserPermissions()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }

}