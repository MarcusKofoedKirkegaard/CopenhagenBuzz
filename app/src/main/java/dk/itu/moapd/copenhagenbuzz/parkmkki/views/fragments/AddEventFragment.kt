package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FragmentAddEventBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.models.EventLocation
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.DataViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.Locale

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = requireNotNull(_binding) { "Cannot access binding because it is null. Is the view visible?" }

    private val viewModel: DataViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var photoByteArray: ByteArray = ByteArray(0)
    private var imagePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAddEventBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.fabAddEvent.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                onAddEvent()
            }
        }

        binding.buttonTakePicture.setOnClickListener {
            startTakeImageIntent()
        }

        binding.buttonAddImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun startTakeImageIntent() {
        if (checkPermissionsCamera()) {
            if (isCameraPermissionEnabled()) {
                val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: ActivityNotFoundException) {
                    showToast("Error: could not get image")
                }
            }
        } else {
            requestCameraPermission()
        }
    }

    private fun isCameraPermissionEnabled(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        Log.i("Pick", intent.toString())
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                val imageBitmap = data.extras?.get("data") as? Bitmap
                imageBitmap?.let {
                    val baos = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    photoByteArray = baos.toByteArray()
                    binding.imagePreview.setImageBitmap(it)
                    binding.imagePreview.visibility = View.VISIBLE
                }
            }

            REQUEST_IMAGE_PICK -> {
                val imageUri: Uri? = data.data
                if (imageUri != null) {
                    // Check for sdk version, as getBitMap is deprecated
                    val bitmap = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                    } else {
                        val source = ImageDecoder.createSource(requireContext().contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    photoByteArray = baos.toByteArray()
                    binding.imagePreview.setImageBitmap(bitmap)
                    binding.imagePreview.visibility = View.VISIBLE
                } else {
                    showToast("Failed to pick image")
                }
            }
        }
    }

    private suspend fun onAddEvent() {
        if (!validateInputs()) {
            showToast("Please fill all fields and add an image")
            return
        }

        val event = createEvent() ?: run {
            showToast("Cannot create event; User must log in")
            return
        }

        viewModel.addEvent(event, photoByteArray)
        showToast("Successfully added event ${event.eventName}!")
        clearTextFields()
    }

    private fun validateInputs(): Boolean {
        return !binding.editTextEventName.text.isNullOrEmpty() &&
                !binding.editTextEventType.text.isNullOrEmpty()// &&
                //photoByteArray.isNotEmpty()
    }

    private fun showToast(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    private suspend fun createEvent(): Event? {
        val user = viewModel.getCurrentUser() ?: return null
        val name = binding.editTextEventName.text.toString()
        val type = binding.editTextEventType.text.toString()
        val date = binding.editTextEventDate.text.toString()
        val description = binding.editTextEventDescription.text.toString()
        val location = getEventLocation()
        return Event(user.uid, name, location, date.toLong(), type, description, imagePath, )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getEventLocation(): EventLocation {
        val geo = Geocoder(requireContext(), Locale.getDefault())
        val address = binding.editTextEventLocation.text.toString()

        if (address.isNotBlank()) {
            geo.getFromLocationName(address, 1)?.firstOrNull()?.let { placemark ->
                return EventLocation(
                    placemark.latitude,
                    placemark.longitude,
                    address
                )
            }
        }

        val location = fusedLocationClient.lastLocation.await()
        location?.let {
            val line = geo.getFromLocation(it.latitude, it.longitude, 1)
                ?.firstOrNull()
                ?.getAddressLine(0)
                .toString()
                .takeUnless { it.isNullOrBlank() }
                ?: "Unknown location"

            return EventLocation(
                it.latitude,
                it.longitude,
                line
            )
        }

        return EventLocation(55.40, 72.9097, "Default address")
    }

    private fun clearTextFields() {
        binding.editTextEventName.text?.clear()
        binding.editTextEventType.text?.clear()
        binding.editTextEventLocation.text?.clear()
        binding.editTextEventDate.text?.clear()
        binding.imagePreview.setImageBitmap(null)
        binding.imagePreview.visibility = View.GONE
        binding.editTextEventDescription.text?.clear()
        photoByteArray = ByteArray(0)
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}
