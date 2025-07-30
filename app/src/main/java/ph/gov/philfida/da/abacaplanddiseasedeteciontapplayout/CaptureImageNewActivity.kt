package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.util.Log
import androidx.exifinterface.media.ExifInterface as ExifInterfaceCompat
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CaptureImageNewActivity : AppCompatActivity() {

    lateinit var capturedNewImage: ByteArray
    lateinit var location: DoubleArray
    lateinit var timestamp: String
    lateinit var capturedNewImageView: ImageView
    lateinit var detectionInfo: String
    lateinit var bitmap: Bitmap
    lateinit var boxedBitmap: Bitmap
    lateinit var symptomsDetected: ArrayList<String>
    var lat: Double? = null
    var longt: Double? = null

    // UI components
    private lateinit var saveImageButton: Button
    private lateinit var viewGalleryButton: Button
    private lateinit var viewOnMapButton: Button
    private lateinit var detectionOverlay: MaterialCardView
    private lateinit var detectionResultText: TextView
    private lateinit var confidenceText: TextView
    private lateinit var locationText: TextView
    private lateinit var coordinatesText: TextView
    private var rotation: Int = 0

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val locationPermissionCode = 1001

    // Formatters
    private val decimalFormat = DecimalFormat("#.##")

    companion object {
        const val CUSTOM_DIRECTORY_NAME = "AbaddAppImages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_capture_image_new)
        super.onCreate(savedInstanceState)

        // Initialize UI components
        initializeViews()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get current location if permissions are granted
        if (checkLocationPermission()) {
            getCurrentLocation()
        } else {
            requestLocationPermission()
        }

        // Set up click listeners
        setupClickListeners()

        // Process the image and show detections
        processImageAndShowDetections()
    }

    private fun initializeViews() {
        saveImageButton = findViewById(R.id.saveImageButton)
        viewGalleryButton = findViewById(R.id.viewGalleryButton)
        viewOnMapButton = findViewById(R.id.viewOnMapButton)
        detectionOverlay = findViewById(R.id.detectionOverlay)
        detectionResultText = findViewById(R.id.detectionResult)
        confidenceText = findViewById(R.id.confidenceText)
        locationText = findViewById(R.id.locationText)
        capturedNewImageView = findViewById(R.id.capturedNewImage)
        coordinatesText = findViewById(R.id.coordinatesText)

        // Set current timestamp
        timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Import extras from intent
        importExtras()
    }

    private fun setupClickListeners() {
        viewOnMapButton.setOnClickListener {
            currentLocation?.let { location ->
                val gmmIntentUri = Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(Detection+Location)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
                }
            }
        }
        saveImageButton.setOnClickListener {
            saveImageToCustomDirectory()
        }
        viewGalleryButton.setOnClickListener {
            openGalleryActivity()
        }
    }

    private fun processImageAndShowDetections() {
        // Process the image and show detections
        if (::bitmap.isInitialized) {
            // TODO: Process the image with your ML model here
            // For now, we'll simulate a detection
            simulateDetection()
        }
    }

    private fun simulateDetection() {
        // This is a placeholder for your actual detection logic
        // Replace with your actual ML model processing
        val diseaseName = "Bunchy Top Virus"
        val confidence = 0.87f // 87% confidence

        // Update UI with detection results
        runOnUiThread {
            detectionOverlay.visibility = View.VISIBLE
            detectionResultText.text = "Detected: $diseaseName"
            confidenceText.text = "Confidence: ${(confidence * 100).roundToInt()}%"

            // Update location info if available
            currentLocation?.let { location ->
                updateLocationUI(location)
                viewOnMapButton.visibility = View.VISIBLE
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationPermissionCode
        )
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLocation = it
                        updateLocationUI(it)
                    } ?: run {
                        locationText.text = "Unable to get location"
                    }
                }
                .addOnFailureListener {
                    locationText.text = "Error getting location"
                }
        }
    }

    private fun updateLocationUI(location: Location) {
        // Update coordinates
        val lat = location.latitude
        val lng = location.longitude
        coordinatesText.text = "Coordinates: ${decimalFormat.format(lat)}, ${decimalFormat.format(lng)}"

        // Here you could also use Geocoder to get address from coordinates
        locationText.text = "Location: Acquired"

        // Store the location in your location array if needed
        this@CaptureImageNewActivity.location = doubleArrayOf(lat, lng)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                locationText.text = "Location permission denied"
            }
        }
    }

    private fun getRotatedBitmap(bitmap: Bitmap, imagePath: String? = null): Bitmap {
        try {
            val exif = if (imagePath != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ExifInterfaceCompat(imagePath)
                } else {
                    @Suppress("DEPRECATION")
                    android.media.ExifInterface(imagePath)
                }
            } else {
                return bitmap // Can't get EXIF data without a file path
            }

            val matrix = Matrix()

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e("ImageRotation", "Error rotating image", e)
            return bitmap
        }
    }

    private fun importExtras() {
        capturedNewImage = intent.getByteArrayExtra("captured_bitmap")!!
        location = intent.getDoubleArrayExtra("location") ?: doubleArrayOf(0.0, 0.0)
        detectionInfo = intent.getStringExtra("detectionInfo") ?: "No detection info"

        // If location was passed in the intent, use it
        if (location.isNotEmpty() && location[0] != 0.0 && location[1] != 0.0) {
            lat = location[0]
            longt = location[1]
            currentLocation = Location("provided")
            currentLocation?.latitude = lat ?: 0.0
            currentLocation?.longitude = longt ?: 0.0
        }

        // Convert byte array to bitmap and handle rotation
        bitmap = BitmapFactory.decodeByteArray(capturedNewImage, 0, capturedNewImage.size)
        // For in-memory bitmaps, we'll assume a default rotation if needed
        bitmap = getRotatedBitmap(bitmap)
        capturedNewImageView.setImageBitmap(bitmap)
        rotation = intent.getIntExtra("rotation", 0)
        bitmap = rotateBitmap(bitmap, rotation)
        // Process the detection info if available
        if (detectionInfo.isNotEmpty() && detectionInfo != "No detection info") {
            try {
                val gson = Gson()
                val type = object : TypeToken<ArrayList<String>>() {}.type
                symptomsDetected = gson.fromJson(detectionInfo, type)

                // Update UI with detection info
                if (symptomsDetected.isNotEmpty()) {
                    runOnUiThread {
                        detectionOverlay.visibility = View.VISIBLE
                        detectionResultText.text = "Detected: ${symptomsDetected.joinToString(", ")}"
                        confidenceText.visibility = View.GONE // Hide if no confidence score
                    }
                }
            } catch (e: Exception) {
                symptomsDetected = arrayListOf(detectionInfo)
            }
        } else {
            symptomsDetected = arrayListOf("No symptoms detected")
        }

        // Update location UI if we have location data
        currentLocation?.let { updateLocationUI(it) }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees.toFloat())
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getCustomDirectory(): File {
        val externalDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customDir = File(externalDir, CUSTOM_DIRECTORY_NAME)
        if (!customDir.exists()) {
            customDir.mkdirs()
        }
        return customDir
    }

    private fun saveImageToCustomDirectory() {
        try {
            val customDir = getCustomDirectory()
            val filename = "IMG_${timestamp}.jpg"
            val imageFile = File(customDir, filename)

            val bitmap = BitmapFactory.decodeByteArray(capturedNewImage, 0, capturedNewImage.size)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGalleryActivity() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }


    private fun loadDetectionInfo(extras: Bundle) {
        symptomsDetected = extras.getStringArrayList("symptomsDetected")!!
        lat = extras.getDouble("lat")
        longt = extras.getDouble("longt")
        categorize(symptomsDetected)
    }

    private fun categorize(symptomsDetected: ArrayList<String>) {
        // Your existing categorization logic
    }
}