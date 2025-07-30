package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.location.LocationServices
import org.tensorflow.lite.task.vision.detector.Detection
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.databinding.FragmentCameraBinding
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.ObjectDetectorHelper
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.CaptureImageNewActivity
import java.io.ByteArrayOutputStream
import com.google.gson.Gson
import java.util.concurrent.TimeUnit
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R
import androidx.core.graphics.createBitmap

class CameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private val TAG = "CameraFragment"

    private var _fragmentCameraBinding: FragmentCameraBinding? = null
    private val fragmentCameraBinding get() = _fragmentCameraBinding!!

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private var bitmapBuffer: Bitmap? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    // Camera state management
    private var isCameraInitialized = false
    private var isDetectionActive = false

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")

        // Check permissions
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.camera_fragment)
                .navigate(R.id.action_camera_to_permissions)
        } else if (isCameraInitialized) {
            // Resume detection if camera was already initialized
            isDetectionActive = true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
        // Pause detection to save resources
        isDetectionActive = false
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView() called")
        _fragmentCameraBinding = null

        // Clean up resources
        cleanupCamera()

        // Shutdown executor
        if (::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
            try {
                if (!cameraExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                    cameraExecutor.shutdownNow()
                }
            } catch (e: InterruptedException) {
                cameraExecutor.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }

        super.onDestroyView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)

        // Set up capture button
        fragmentCameraBinding.newCapture.setOnClickListener {
            if (isCameraInitialized && bitmapBuffer != null) {
                captureNewImage()
            } else {
                Toast.makeText(requireContext(), "Camera not ready", Toast.LENGTH_SHORT).show()
            }
        }

        return fragmentCameraBinding.root
    }

    @SuppressLint("MissingPermission")
    private fun captureNewImage() {
        val currentBitmap = bitmapBuffer
        if (currentBitmap == null) {
            Toast.makeText(requireContext(), "No image available", Toast.LENGTH_SHORT).show()
            return
        }

        val rotation = getRotationDegrees()

        Log.d(TAG, "Capturing image with dimensions: ${currentBitmap.width}x${currentBitmap.height}")

        // Get detected objects
        val detectedObjects = fragmentCameraBinding.overlay.getDetectedObjects()
        Log.d(TAG, "Detected objects: ${detectedObjects.size}")

        // Get location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                val intent = Intent(requireContext(), CaptureImageNewActivity::class.java)

                try {
                    // Convert bitmap to byte array
                    val bitmapByteArray = ByteArrayOutputStream().use { stream ->
                        currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.toByteArray()
                    }

                    // Convert detections to JSON
                    val gson = Gson()
                    val jsonDetection = gson.toJson(detectedObjects)

                    // Set intent extras
                    intent.putExtra("captured_bitmap", bitmapByteArray)
                    intent.putExtra("detectionJson", jsonDetection)

                    intent.putExtra("rotation", rotation)
                    if (location != null) {
                        intent.putExtra("location_latitude", location.latitude)
                        intent.putExtra("location_longitude", location.longitude)
                        Log.d(TAG, "Location: ${location.latitude}, ${location.longitude}")
                    } else {
                        intent.putExtra("location_latitude", 0.0)
                        intent.putExtra("location_longitude", 0.0)
                        Log.w(TAG, "Location not available")
                    }

                    startActivity(intent)

                } catch (e: Exception) {
                    Log.e(TAG, "Error preparing capture data", e)
                    Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get location", exception)
                Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")

        try {
            // Initialize object detector
            objectDetectorHelper = ObjectDetectorHelper(
                context = requireContext(),
                objectDetectorListener = this
            )

            // Initialize camera executor
            cameraExecutor = Executors.newSingleThreadExecutor()

            // Wait for views to be properly laid out
            fragmentCameraBinding.viewFinder.post {
                setUpCamera()
            }

            // Initialize UI controls
            initBottomSheetControls()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Error initializing camera", Toast.LENGTH_LONG).show()
        }
    }

    private fun initBottomSheetControls() {
        // Threshold controls
        fragmentCameraBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
            if (objectDetectorHelper.threshold >= 0.1) {
                objectDetectorHelper.threshold -= 0.1f
                updateControlsUi()
            }
        }

        fragmentCameraBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
            if (objectDetectorHelper.threshold <= 0.8) {
                objectDetectorHelper.threshold += 0.1f
                updateControlsUi()
            }
        }

        // Max results controls
        fragmentCameraBinding.bottomSheetLayout.maxResultsMinus.setOnClickListener {
            if (objectDetectorHelper.maxResults > 1) {
                objectDetectorHelper.maxResults--
                updateControlsUi()
            }
        }

        fragmentCameraBinding.bottomSheetLayout.maxResultsPlus.setOnClickListener {
            if (objectDetectorHelper.maxResults < 5) {
                objectDetectorHelper.maxResults++
                updateControlsUi()
            }
        }

        // Thread controls
        fragmentCameraBinding.bottomSheetLayout.threadsMinus.setOnClickListener {
            if (objectDetectorHelper.numThreads > 1) {
                objectDetectorHelper.numThreads--
                updateControlsUi()
            }
        }

        fragmentCameraBinding.bottomSheetLayout.threadsPlus.setOnClickListener {
            if (objectDetectorHelper.numThreads < 4) {
                objectDetectorHelper.numThreads++
                updateControlsUi()
            }
        }

        // Delegate spinner
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(0, false)
        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    objectDetectorHelper.currentDelegate = position
                    updateControlsUi()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No operation
                }
            }

        // Model spinner
        fragmentCameraBinding.bottomSheetLayout.spinnerModel.setSelection(0, false)
        fragmentCameraBinding.bottomSheetLayout.spinnerModel.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    objectDetectorHelper.currentModel = position
                    updateControlsUi()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No operation
                }
            }
    }

    private fun updateControlsUi() {
        fragmentCameraBinding.bottomSheetLayout.maxResultsValue.text =
            objectDetectorHelper.maxResults.toString()
        fragmentCameraBinding.bottomSheetLayout.thresholdValue.text =
            String.format("%.2f", objectDetectorHelper.threshold)
        fragmentCameraBinding.bottomSheetLayout.threadsValue.text =
            objectDetectorHelper.numThreads.toString()

        // Clear and reinitialize detector
        objectDetectorHelper.clearObjectDetector()
        fragmentCameraBinding.overlay.clear()
    }

    private fun setUpCamera() {
        Log.d(TAG, "Setting up camera")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            } catch (e: Exception) {
                Log.e(TAG, "Camera provider initialization failed", e)
                Toast.makeText(requireContext(), "Camera initialization failed", Toast.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: run {
            Log.e(TAG, "Camera provider is null")
            return
        }

        try {
            // Camera selector
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Preview use case
            preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .build()

            // Image analysis use case
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                        if (isDetectionActive) {
                            try {
                                processImage(imageProxy)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error processing image", e)
                            }
                        }
                        imageProxy.close()
                    }
                }

            // Unbind all use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

            // Attach preview to surface provider
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)

            isCameraInitialized = true
            isDetectionActive = true

            Log.d(TAG, "Camera use cases bound successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)
            Toast.makeText(requireContext(), "Camera binding failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun processImage(imageProxy: ImageProxy) {
        // Validate image dimensions
        if (imageProxy.width <= 0 || imageProxy.height <= 0) {
            Log.e(TAG, "Invalid image dimensions: ${imageProxy.width}x${imageProxy.height}")
            return
        }

        // Initialize or update bitmap buffer
        if (bitmapBuffer == null ||
            bitmapBuffer?.width != imageProxy.width ||
            bitmapBuffer?.height != imageProxy.height) {

            bitmapBuffer = createBitmap(imageProxy.width, imageProxy.height)
            Log.d(TAG, "Created bitmap buffer: ${imageProxy.width}x${imageProxy.height}")
        }

        // Copy image data to bitmap
        bitmapBuffer?.let { bitmap ->
            try {
                bitmap.copyPixelsFromBuffer(imageProxy.planes[0].buffer)

                // Perform object detection
                val imageRotation = imageProxy.imageInfo.rotationDegrees
                objectDetectorHelper.detect(bitmap, imageRotation)

            } catch (e: Exception) {
                Log.e(TAG, "Error copying pixels or detecting objects", e)
            }
        }
    }

    private fun cleanupCamera() {
        Log.d(TAG, "Cleaning up camera resources")

        isDetectionActive = false
        isCameraInitialized = false

        try {
            cameraProvider?.unbindAll()
            camera = null
            preview = null
            imageAnalyzer = null
            bitmapBuffer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error during camera cleanup", e)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentCameraBinding.viewFinder.display.rotation
    }

    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        activity?.runOnUiThread {
            try {
                Log.d(TAG, "onResults: ${results?.size ?: 0} detections, inference: ${inferenceTime}ms, image: ${imageWidth}x${imageHeight}")

                // Update inference time display
                fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
                    String.format("%d ms", inferenceTime)

                // Update overlay with detection results
                val detectionList = results ?: LinkedList<Detection>()
                fragmentCameraBinding.overlay.setResults(
                    detectionList,
                    imageHeight,
                    imageWidth
                )

                // Log overlay view dimensions and state for debugging
                val overlay = fragmentCameraBinding.overlay
                Log.d(TAG, "Overlay dimensions: ${overlay.width}x${overlay.height}")
                Log.d(TAG, "ViewFinder dimensions: ${fragmentCameraBinding.viewFinder.width}x${fragmentCameraBinding.viewFinder.height}")
                Log.d(TAG, overlay.checkDrawableState())

                // Ensure overlay is visible and drawable
                if (overlay.visibility != View.VISIBLE) {
                    Log.d(TAG, "Making overlay visible")
                    overlay.visibility = View.VISIBLE
                }

                // Force the view hierarchy to refresh
                if (!overlay.isShown) {
                    Log.d(TAG, "Overlay not shown, trying to fix...")

                    // Try to make parent visible if needed
                    var parent = overlay.parent
                    while (parent is View && !parent.isShown) {
                        Log.d(TAG, "Making parent visible: ${parent.javaClass.simpleName}")
                        parent.visibility = View.VISIBLE
                        parent = parent.parent
                    }

                    // Request layout to ensure proper positioning
                    overlay.requestLayout()

                    // Try bringing to front
                    overlay.bringToFront()
                }

                // Force redraw with multiple methods
                overlay.forceRedraw()

                // Also try direct invalidation
                overlay.invalidate()

                // If still no drawing, try parent invalidation
                (overlay.parent as? View)?.invalidate()

                // Try invalidating the entire view hierarchy
                fragmentCameraBinding.root.invalidate()

            } catch (e: Exception) {
                Log.e(TAG, "Error updating UI with results", e)
            }
        }
    }

    override fun onError(error: String) {
        Log.e(TAG, "Detection error: $error")
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }
    private fun getRotationDegrees(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 (API 30) and above
            val windowMetrics = requireActivity().windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            val rotation = requireActivity().display?.rotation ?: 0
            when (rotation) {
                Surface.ROTATION_270 -> 270
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_90 -> 90
                else -> 0
            }
        } else {
            // For older versions
            @Suppress("DEPRECATION")
            when (requireActivity().windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_270 -> 270
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_90 -> 90
                else -> 0
            }
        }
    }
}