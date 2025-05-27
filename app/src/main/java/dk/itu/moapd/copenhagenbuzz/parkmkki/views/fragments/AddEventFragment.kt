package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.Toast
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddEventFragment : Fragment() {

    private var _binding: FragmentAddEventBinding? = null
    private val binding get() = requireNotNull(_binding)
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
        binding.editTextEventDate.isEnabled = false
        binding.editTextEventDate.setOnClickListener {
            showDateTimePicker()
        }
    }
    private fun showDateTimePicker() {
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        val timePicker = {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    if (calendar.timeInMillis < now.timeInMillis) {
                        Toast.makeText(requireContext(), "Please select a future time", Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }

                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val formatted = sdf.format(calendar.time)
                    binding.editTextEventDate.setText(formatted)
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                timePicker()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = now.timeInMillis
        datePickerDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.fabAddEvent.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (validateInputs()) {
                    onAddEvent()
                }
            }
        }

        binding.buttonTakePicture.setOnClickListener {
            startTakeImageIntent()
        }

        binding.buttonAddImage.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private suspend fun onAddEvent() {
        val event = createEvent() ?: run {
            showToast("Failed to create event.")
            return
        }

        viewModel.addEvent(event, photoByteArray)
        showToast("Event '${event.eventName}' added.")
        clearTextFields()
    }

    private fun validateInputs(): Boolean {
        val name = binding.editTextEventName.text.toString().trim()
        val type = binding.editTextEventType.text.toString().trim()
        val dateTimeStr = binding.editTextEventDate.text.toString().trim()
        val location = binding.editTextEventLocation.text.toString().trim()

        if (name.isEmpty() || type.isEmpty() || dateTimeStr.isEmpty() || location.isEmpty()) {
            showToast("All fields are required.")
            return false
        }

        if (dateTimeStr.isEmpty()) {
            showToast("Date and time are required.")
            return false
        }

        if (!isValidDateTime(dateTimeStr)) {
            showToast("Invalid format. Use dd/MM/yyyy HH:mm.")
            return false
        }

        if (!isValidAddress(location)) {
            showToast("Invalid address: '$location'. Please enter a valid location.")
            return false
        }

        return true
    }

    private fun isValidDateTime(dateTimeStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(dateTimeStr)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun isValidAddress(address: String): Boolean {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val results = geocoder.getFromLocationName(address, 1)
            !results.isNullOrEmpty()
        } catch (e: Exception) {
            Log.e("AddEventFragment", "Geocoding failed: ${e.message}", e)
            false
        }
    }

    private fun showToast(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    private suspend fun createEvent(): Event? {
        val user = viewModel.getCurrentUser() ?: return null

        val name = binding.editTextEventName.text.toString().trim()
        val type = binding.editTextEventType.text.toString().trim()
        val dateTimeStr = binding.editTextEventDate.text.toString().trim()
        val description = binding.editTextEventDescription.text.toString().trim()
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .parse(dateTimeStr)?.time ?: return null

        val location = getEventLocation()

        return Event(user.uid, name, location, timestamp, type, description, imagePath)
    }

    @SuppressLint("MissingPermission")
    private suspend fun getEventLocation(): EventLocation {
        val addressInput = binding.editTextEventLocation.text.toString().trim()
        val geo = Geocoder(requireContext(), Locale.getDefault())

        if (addressInput.isNotEmpty()) {
            val results = geo.getFromLocationName(addressInput, 1)
            if (!results.isNullOrEmpty()) {
                val loc = results[0]
                return EventLocation(loc.latitude, loc.longitude, addressInput)
            }
        }

        val fallback = fusedLocationClient.lastLocation.await()
        return fallback?.let {
            val addr = geo.getFromLocation(it.latitude, it.longitude, 1)
                ?.firstOrNull()?.getAddressLine(0) ?: "Unknown location"
            EventLocation(it.latitude, it.longitude, addr)
        } ?: EventLocation(55.40, 72.9097, "Default address")
    }

    private fun clearTextFields() {
        binding.editTextEventName.text?.clear()
        binding.editTextEventType.text?.clear()
        binding.editTextEventLocation.text?.clear()
        binding.editTextEventDate.text?.clear()
        binding.editTextEventDescription.text?.clear()
        binding.imagePreview.setImageBitmap(null)
        binding.imagePreview.visibility = View.GONE
        photoByteArray = ByteArray(0)
    }

    private fun startTakeImageIntent() {
        if (isCameraPermissionEnabled()) {
            val takeImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                showToast("Unable to open camera.")
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

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    @Deprecated("Deprecated in Java")
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

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}
