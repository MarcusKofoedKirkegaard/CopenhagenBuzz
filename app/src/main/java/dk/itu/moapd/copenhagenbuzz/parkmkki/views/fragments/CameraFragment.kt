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

    private var _binding: FragmentCameraBinding? = null

    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: CameraViewModel by activityViewModels()
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private var imageUri: Uri? = null

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        cameraPermissionResult(result)
    }

    /**
     * Called when the fragment's view is created.
     * Inflates the layout and sets up the necessary interactions.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentCameraBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    /**
     * Called when the fragment's view is fully created.
     * Sets up listeners for button interactions (photo capture, camera switch, etc.).
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkPermission()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        viewModel.selector.observe(viewLifecycleOwner) {
            cameraSelector = it ?: CameraSelector.DEFAULT_BACK_CAMERA
        }

        binding.apply {
            buttonImageCapture.setOnClickListener {
                takePhoto()
            }

            buttonCameraSwitch.apply {
                isEnabled = false
                setOnClickListener {
                    viewModel.onCameraSelectorChanged(
                        if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                            CameraSelector.DEFAULT_BACK_CAMERA
                        else
                            CameraSelector.DEFAULT_FRONT_CAMERA
                    )
                    startCamera()
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
        _binding = null
    }

    /**
     * Handles the result of the camera permission request.
     * If permission is granted, starts the camera.
     * If not, closes the activity.
     *
     * @param isGranted Whether the permission was granted or denied.
     */
    private fun cameraPermissionResult(isGranted: Boolean) {
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
            hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            false
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

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider) // Set surface provider for the preview.
            }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

                updateCameraSwitchButton(cameraProvider)

            } catch(ex: Exception) {
                showSnackBar("Use case binding failed: $ex")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    /**
     * Captures a photo and saves it to the device's storage.
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
        val filename = "IMG_$timestamp.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    imageUri = output.savedUri
                    showSnackBar("Photo capture succeeded: $filename.jpg")
                }

                override fun onError(exception: ImageCaptureException) {
                    showSnackBar("Photo capture failed: ${exception.message}")
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
        ).show()
    }
}