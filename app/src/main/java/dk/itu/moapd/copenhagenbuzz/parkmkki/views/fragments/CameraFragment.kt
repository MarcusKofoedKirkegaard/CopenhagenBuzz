package dk.itu.moapd.copenhagenbuzz.parkmkki.views.fragments

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.copenhagenbuzz.parkmkki.databinding.FragmentCameraBinding
import dk.itu.moapd.copenhagenbuzz.parkmkki.viewmodels.CameraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fragment that handles the camera interface, capturing photos, and switching between front and back cameras.
 * It requests permissions to access the camera, displays the camera preview, and allows capturing images.
 */
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null // Binding reference for the fragment view.

    // Property to get the binding, throws exception if it's null.
    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    // ViewModel to handle camera selector state.
    private val viewModel: CameraViewModel by activityViewModels()

    // Variable to hold the camera selector (default to back camera).
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Variable to handle image capture functionality.
    private var imageCapture: ImageCapture? = null

    // Variable to store the URI of the captured image.
    private var imageUri: Uri? = null

    // Format for naming the captured photo files.
    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

    // Launcher to request camera permission at runtime.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        cameraPermissionResult(result) // Handle the result of the camera permission request.
    }

    /**
     * Called when the fragment's view is created.
     * Inflates the layout and sets up the necessary interactions.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCameraBinding.inflate(inflater, container, false).also {
        _binding = it // Assign the binding for accessing UI elements.
    }.root

    /**
     * Called when the fragment's view is fully created.
     * Sets up listeners for button interactions (photo capture, camera switch, etc.).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Request camera permissions on fragment startup.
        if (checkPermission()) {
            startCamera() // Start camera if permission granted.
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA) // Request permission.
        }

        // Observe camera selector changes from the ViewModel.
        viewModel.selector.observe(viewLifecycleOwner) {
            cameraSelector = it ?: CameraSelector.DEFAULT_BACK_CAMERA // Update camera selector.
        }

        // Set up UI listeners for the camera controls.
        binding.apply {

            // Listener for the photo capture button.
            buttonImageCapture.setOnClickListener {
                takePhoto() // Capture photo when clicked.
            }

            // Listener for the camera switch button.
            buttonCameraSwitch.apply {
                isEnabled = false // Disable until camera setup is complete.

                setOnClickListener {
                    // Switch between front and back cameras.
                    viewModel.onCameraSelectorChanged(
                        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                    )

                    // Restart the camera setup to apply the camera switch.
                    startCamera()
                }
            }

            // Listener for viewing the captured image.
            buttonImageViewer.setOnClickListener {
                imageUri?.let { uri ->
                    val bundle =
                        bundleOf("ARG_IMAGE" to uri.toString()) // Pass URI in a bundle for viewing.
                }
            }
        }
    }

    /**
     * Called when the fragment's view is destroyed.
     * Cleans up the binding to avoid memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Set the binding to null to release resources.
    }

    /**
     * Handles the result of the camera permission request.
     * If permission is granted, starts the camera.
     * If not, closes the activity.
     *
     * @param isGranted Whether the permission was granted or denied.
     */
    private fun cameraPermissionResult(isGranted: Boolean) {
        // Start the camera if permission is granted, otherwise close the activity.
        isGranted.takeIf { it }?.run {
            startCamera()
        } ?: requireActivity().finish()
    }

    /**
     * Checks if the app has the required camera permission.
     *
     * @return True if the camera permission is granted, otherwise false.
     */
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Updates the state of the camera switch button based on the available cameras.
     *
     * @param provider The camera provider to check for available cameras.
     */
    private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
        binding.buttonCameraSwitch.isEnabled = try {
            // Enable the camera switch button if both front and back cameras are available.
            hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            false // Disable button if camera info is unavailable.
        }
    }

    /**
     * Checks if the back camera is available.
     *
     * @param provider The camera provider to check.
     * @return True if the back camera is available, false otherwise.
     */
    private fun hasBackCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

    /**
     * Checks if the front camera is available.
     *
     * @param provider The camera provider to check.
     * @return True if the front camera is available, false otherwise.
     */
    private fun hasFrontCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    /**
     * Starts the camera and binds the use cases (preview and image capture) to the lifecycle.
     */
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            // Build the preview use case.
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider) // Set surface provider for the preview.
            }

            // Build the image capture use case.
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll() // Unbind any existing use cases.

                // Bind the preview and image capture use cases to the lifecycle.
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                // Update the camera switch button state.
                updateCameraSwitchButton(cameraProvider)

            } catch(ex: Exception) {
                showSnackBar("Use case binding failed: $ex") // Show an error if binding fails.
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Captures a photo and saves it to the device's storage.
     */
    private fun takePhoto() {

        val imageCapture = imageCapture ?: return // Return if image capture is not initialized.

        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date()) // Generate timestamp.
        val filename = "IMG_$timestamp.jpg" // Create a filename for the photo.

        // Prepare content values for storing the image in the MediaStore.
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        // Create output file options for the captured image.
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
            .build()

        // Capture the image and save it to the device.
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                // Called when the photo is successfully saved.
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri = output.savedUri // Store the URI of the saved image.
                    showSnackBar("Photo capture succeeded: $filename.jpg") // Notify the user.
                }

                // Called when an error occurs during image capture.
                override fun onError(exception: ImageCaptureException) {
                    showSnackBar("Photo capture failed: ${exception.message}") // Notify the user of the error.
                }
            }
        )
    }

    /**
     * Displays a Snackbar with the specified message.
     *
     * @param message The message to display in the Snackbar.
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show() // Show the Snackbar with the message.
    }
}