package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.IOException

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 4,
    var currentDelegate: Int = 0,
    var currentModel: Int = 0,
    val context: Context,
    val objectDetectorListener: DetectorListener?
) {

    private val TAG = "ObjectDetectorHelper"

    // For this example this needs to be a var so it can be reset on changes
    private var objectDetector: ObjectDetector? = null
    private var isInitialized = false

    init {
        setupObjectDetector()
    }

    fun clearObjectDetector() {
        Log.d(TAG, "Clearing object detector")
        try {
            objectDetector?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing object detector", e)
        }
        objectDetector = null
        isInitialized = false
    }

    // Initialize the object detector using current settings
    fun setupObjectDetector() {
        Log.d(TAG, "Setting up object detector with threshold: $threshold, threads: $numThreads, delegate: $currentDelegate, model: $currentModel")

        try {
            // Clear existing detector first
            clearObjectDetector()

            // Create the base options for the detector
            val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

            // Set general detection options
            val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

            // Configure hardware delegate
            when (currentDelegate) {
                DELEGATE_CPU -> {
                    Log.d(TAG, "Using CPU delegate")
                    // Default - no additional configuration needed
                }
                DELEGATE_GPU -> {
                    if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                        Log.d(TAG, "Using GPU delegate")
                        baseOptionsBuilder.useGpu()
                    } else {
                        Log.w(TAG, "GPU delegate not supported, falling back to CPU")
                        objectDetectorListener?.onError("GPU is not supported on this device, using CPU instead")
                    }
                }
                DELEGATE_NNAPI -> {
                    Log.d(TAG, "Using NNAPI delegate")
                    baseOptionsBuilder.useNnapi()
                }
            }

            optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

            // Select model file
            val modelName = when (currentModel) {
                MODEL_MOBILENETV1 -> "last_model.tflite"
                MODEL_EFFICIENTDETV0 -> "efficientdet-lite0.tflite"
                MODEL_EFFICIENTDETV1 -> "efficientdet-lite1.tflite"
                MODEL_EFFICIENTDETV2 -> "efficientdet-lite2.tflite"
                else -> {
                    Log.w(TAG, "Unknown model type: $currentModel, using default")
                    "mobilenetv1.tflite"
                }
            }

            Log.d(TAG, "Loading model: $modelName")

            // Create the object detector
            objectDetector = ObjectDetector.createFromFileAndOptions(
                context,
                modelName,
                optionsBuilder.build()
            )

            isInitialized = true
            Log.d(TAG, "Object detector initialized successfully")

        } catch (e: IllegalStateException) {
            val errorMsg = "Object detector failed to initialize: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
            isInitialized = false
        } catch (e: IOException) {
            val errorMsg = "Failed to load model file: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
            isInitialized = false
        } catch (e: Exception) {
            val errorMsg = "Unexpected error during detector setup: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
            isInitialized = false
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        // Validate inputs
        if (image.isRecycled) {
            Log.e(TAG, "Cannot detect on recycled bitmap")
            return
        }

        if (image.width <= 0 || image.height <= 0) {
            Log.e(TAG, "Invalid bitmap dimensions: ${image.width}x${image.height}")
            return
        }

        // Ensure detector is initialized
        if (!isInitialized || objectDetector == null) {
            Log.d(TAG, "Detector not initialized, setting up...")
            setupObjectDetector()
            if (!isInitialized) {
                Log.e(TAG, "Failed to initialize detector")
                return
            }
        }

        val detector = objectDetector ?: run {
            Log.e(TAG, "Object detector is null")
            return
        }

        try {
            // Start timing
            val startTime = SystemClock.uptimeMillis()

            // Create image processor for rotation
            val imageProcessor = ImageProcessor.Builder().apply {
                // Only add rotation if needed
                val rotationSteps = -imageRotation / 90
                if (rotationSteps != 0) {
                    add(Rot90Op(rotationSteps))
                }
            }.build()

            // Process the image
            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))

            // Validate processed image
            if (tensorImage.width <= 0 || tensorImage.height <= 0) {
                Log.e(TAG, "Invalid processed image dimensions: ${tensorImage.width}x${tensorImage.height}")
                return
            }

            // Run detection
            val results = detector.detect(tensorImage)

            // Calculate inference time
            val inferenceTime = SystemClock.uptimeMillis() - startTime

            Log.d(TAG, "Detection completed in ${inferenceTime}ms, found ${results?.size ?: 0} objects")

            // Callback with results
            objectDetectorListener?.onResults(
                results,
                inferenceTime,
                tensorImage.height,
                tensorImage.width
            )

        } catch (e: IllegalArgumentException) {
            val errorMsg = "Invalid input for detection: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
        } catch (e: RuntimeException) {
            val errorMsg = "Runtime error during detection: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
        } catch (e: Exception) {
            val errorMsg = "Unexpected error during detection: ${e.message}"
            Log.e(TAG, errorMsg, e)
            objectDetectorListener?.onError(errorMsg)
        }
    }

    // Clean up resources
    fun cleanup() {
        Log.d(TAG, "Cleaning up ObjectDetectorHelper")
        clearObjectDetector()
    }

    interface DetectorListener {
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_MOBILENETV1 = 0
        const val MODEL_EFFICIENTDETV0 = 1
        const val MODEL_EFFICIENTDETV1 = 2
        const val MODEL_EFFICIENTDETV2 = 3

        private const val TAG = "ObjectDetectorHelper"
    }
}