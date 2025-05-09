/*
 * (License Notice)
 * MIT License
 * Copyright (c) [2025] [Emil Parkel & Marcus Kofoed Kirkegaard]
 * See README for more
 */
package dk.itu.moapd.copenhagenbuzz.parkmkki.view

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Address
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import dk.itu.moapd.copenhagenbuzz.parkmkki.controller.EventController
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FragmentAddEventBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.Event
import dk.itu.moapd.copenhagenbuzz.parkmkki.model.EventLocation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

/**
 * Fragment for adding a new event.
 *
 * This fragment allows users to input event details and save them using the `EventController`.
 */
class AddEventFragment : Fragment() {

    /**
     * View binding for accessing UI elements in `fragment_add_event.xml`.
     */
    private var _binding: FragmentAddEventBinding? = null

    /**
     * Non-nullable binding property to ensure safe access.
     */
    private val binding get() = requireNotNull(_binding) {
        "Cannot access binding because it is null. Is the view visible?"
    }

    /**
     * Controller instance for managing event data.
     */
    private val eventController = EventController()

    /**
     * Date formatter for parsing user input dates.
     */
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private var selectedImageUri: Uri? = null


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            showImagePreview(it)
        }
    }

    // Capture image using camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri?.let {
                showImagePreview(it)
            }
        }
    }

    // Create temporary URI to store the captured image
    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/EventImages")
        }
        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] == true
        val storageGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true

        if (cameraGranted && storageGranted) {
            // Permissions granted, proceed to take photo or pick image
        } else {
            // Permissions not granted, inform user they need to grant permissions
            Toast.makeText(context, "Permissions are required to use the camera.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.CAMERA
        )

        val permissionsNeeded = permissions.filter { ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED }

        if (permissionsNeeded.isEmpty()) {
            val uri = createImageUri()
            selectedImageUri = uri
            takePictureLauncher.launch(uri)
        } else {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }


    /**
     * Inflates the fragment's layout and initializes view binding.
     *
     * @param inflater LayoutInflater for inflating views.
     * @param container Parent container, or `null` if none.
     * @param savedInstanceState Previously saved instance state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEventBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Called after the view has been created.
     *
     * Sets up the UI, including handling button clicks for adding an event.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState Saved instance state, if any.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database("https://moapd-2025-793fd-default-rtdb.europe-west1.firebasedatabase.app/").reference

        // Set up the floating action button click listener for adding an event
        binding.fabAddEvent.setOnClickListener {
            if (validateInputs()) {
                try {
                    // Update event using the EventController
                    auth.currentUser?.let { user ->
                        val event = Event(
                            user.uid,
                            binding.editTextEventName.text.toString().trim(),
                            EventLocation(0.0, 0.0, binding.editTextEventLocation.text.toString().trim()),
                            binding.editTextEventDate.text.toString().trim().toLong(),
                            binding.editTextEventType.text.toString().trim(),
                            binding.editTextEventLocation.text.toString().trim(),
                            selectedImageUri.toString(),
                            false,
                            0
                        )

                        database.child("events")
                            .child(user.uid)
                            .push()
                            .key?.let { uid ->
                                database.child("events")
                                    .child(user.uid)
                                    .child(uid)
                                    .setValue(event)
                            }

                        val filename = UUID.randomUUID().toString()
                        val imageRef = Firebase.storage("gs://moapd-2025-793fd.firebasestorage.app").reference
                            .child("images/$user.uid/$filename")
                        selectedImageUri?.let { img -> imageRef.putFile(img) }
                    }

                    // Show confirmation message
                } catch (e: Exception) {
                    Log.e("AddEventFragment", "Could not parse the date! Error: ${e.message}")
                }
            }
        }

        binding.buttonAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.buttonTakePicture.setOnClickListener {
            checkAndRequestPermissions()
        }
    }

    /**
     * Validates user input fields to ensure they are not empty.
     *
     * @return `true` if all input fields are valid, otherwise `false`.
     */
    private fun validateInputs(): Boolean {
        return !binding.editTextEventName.text.isNullOrEmpty() &&
                !binding.editTextEventLocation.text.isNullOrEmpty() &&
                !binding.editTextEventDate.text.isNullOrEmpty() &&
                !binding.editTextEventType.text.isNullOrEmpty()
    }

    /**
     * Displays a confirmation message when an event is successfully added.
     */
    private fun showMessage() {
        Snackbar.make(binding.root, "Event added", Snackbar.LENGTH_LONG).show()
    }

    /**
     * Cleans up the binding when the view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Address.toAddressString(): String =
        with(StringBuilder()) {
            append(getAddressLine(0)).append("\n")
            append(countryName)
            toString()
        }

    private fun setAddress(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = if (Build.VERSION.SDK_INT >= 33)
            geocoder.getFromLocation(latitude, longitude, 1)
        else
            geocoder.getFromLocation(latitude, longitude, 1)
        addresses?.firstOrNull()?.toAddressString()?.let { address ->
            binding.editTextEventLocation.setText(address)
        }
    }

    private fun showImagePreview(uri: Uri) {
        binding.imagePreview.setImageURI(uri)
        binding.imagePreview.visibility = View.VISIBLE
    }
}
