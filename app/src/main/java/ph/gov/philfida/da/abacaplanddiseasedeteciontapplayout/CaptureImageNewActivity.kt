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
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureDao
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureRecord
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.DatabaseHelper
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


// Data classes for parsing detection JSON
data class DetectionResult(
    val boundingBox: BoundingBox,
    val categories: List<Category>
)

data class BoundingBox(
    val bottom: Float,
    val left: Float,
    val right: Float,
    val top: Float
)

data class Category(
    val displayName: String,
    val index: Int,
    val label: String,
    val score: Float
)

class CaptureImageNewActivity : AppCompatActivity() {

    lateinit var capturedNewImage: ByteArray
    lateinit var location: DoubleArray
    lateinit var timestamp: String
    lateinit var capturedNewImageView: ImageView
    lateinit var detectionInfo: String
    lateinit var bitmap: Bitmap
    lateinit var boxedBitmap: Bitmap
    lateinit var symptomsDetected: ArrayList<String>
    private var detectionResults: List<DetectionResult> = emptyList()
    private var boundingBoxes: List<BoundingBox> = emptyList()
    private var confidenceScores: List<Float> = emptyList()
    var lat: Double? = null
    var longt: Double? = null
    


    // UI components
    private lateinit var saveImageButton: Button
    private lateinit var viewGalleryButton: Button
    private lateinit var viewOnMapButton: Button
    private lateinit var detectionOverlay: MaterialCardView
    private lateinit var detectionResultText: TextView
    private lateinit var confidenceText: TextView
    private lateinit var diseaseResultText: TextView
    private lateinit var diseaseConfidenceText: TextView
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
        diseaseResultText = findViewById(R.id.diseaseResult)
        diseaseConfidenceText = findViewById(R.id.diseaseConfidence)
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
        // Use the detection results passed from CameraFragment
        if (::bitmap.isInitialized && ::symptomsDetected.isInitialized) {
            displayDetectionResults()
        }
    }

    private fun displayDetectionResults() {
        runOnUiThread {
            if (symptomsDetected.isNotEmpty() && symptomsDetected[0] != "No symptoms detected") {
                detectionOverlay.visibility = View.VISIBLE
                
                // Identify diseases based on symptoms
                val identifiedDiseases = identifyDisease(symptomsDetected)
                if (identifiedDiseases.isNotEmpty()) {
                    // Display all detected diseases sorted by confidence
                    val diseasesText = identifiedDiseases.mapIndexed { index, (disease, confidence) ->
                        if (index == 0) {
                            "★ $disease ($confidence%)"
                        } else {
                            "• $disease ($confidence%)"
                        }
                    }.joinToString("\n")
                    
                    diseaseResultText.text = "Detected Diseases:\n$diseasesText"
                    diseaseConfidenceText.text = "${identifiedDiseases.size} disease(s) identified"
                    diseaseResultText.visibility = View.VISIBLE
                    diseaseConfidenceText.visibility = View.VISIBLE
                } else {
                    diseaseResultText.text = "Disease: Unknown"
                    diseaseConfidenceText.visibility = View.GONE
                    diseaseResultText.visibility = View.VISIBLE
                }
                
                // Show symptoms sorted by confidence
                val sortedSymptoms = getSortedSymptomsWithConfidence()
                detectionResultText.text = sortedSymptoms
                
                // Show average confidence
                if (confidenceScores.isNotEmpty()) {
                    val avgConfidence = confidenceScores.average()
                    confidenceText.text = "Avg Symptom Confidence: ${(avgConfidence * 100).roundToInt()}%"
                    confidenceText.visibility = View.VISIBLE
                } else {
                    confidenceText.visibility = View.GONE
                }
            } else {
                detectionOverlay.visibility = View.VISIBLE
                detectionResultText.text = "No symptoms detected"
                confidenceText.visibility = View.GONE
                diseaseResultText.text = "No disease identified"
                diseaseConfidenceText.visibility = View.GONE
            }

            // Update location info if available
            currentLocation?.let { location ->
                updateLocationUI(location)
                viewOnMapButton.visibility = View.VISIBLE
            }
        }
    }
    
    private fun getSortedSymptomsWithConfidence(): String {
        if (symptomsDetected.isEmpty() || confidenceScores.isEmpty()) {
            return "Symptoms: ${symptomsDetected.joinToString(", ")}"
        }
        
        // Pair symptoms with their confidence scores
        val symptomConfidencePairs = symptomsDetected.zip(confidenceScores)
            .sortedByDescending { it.second }
        
        val result = StringBuilder("Symptoms:\n")
        symptomConfidencePairs.forEachIndexed { index, (symptom, confidence) ->
            val confidencePercent = (confidence * 100).roundToInt()
            if (index == 0) {
                // Highest confidence - make it bold/highlighted
                result.append("• $symptom ($confidencePercent%) ★\n")
            } else {
                result.append("• $symptom ($confidencePercent%)\n")
            }
        }
        
        return result.toString().trimEnd()
    }
    
    private fun identifyDisease(symptoms: ArrayList<String>): List<Pair<String, Int>> {
        val dbHelper = DiseaseSymptomsDbHelper(this)
        val diseases = dbHelper.getDiseases()
        
        val diseaseMatches = mutableMapOf<String, MutableList<Float>>()
        
        // Check each symptom against disease database
        for (symptom in symptoms) {
            val cleanSymptom = symptom.replace("\r", "").trim()
            
            for (disease in diseases) {
                val diseaseSymptoms = listOfNotNull(
                    disease.no_Allocation,
                    disease.bract_Mosaic,
                    disease.bunchy_Top,
                    disease.cmv,
                    disease.gen_Mosaic,
                    disease.scmv
                ).filter { it.isNotEmpty() && it != "NULL" }
                
                if (diseaseSymptoms.contains(cleanSymptom)) {
                    val diseaseName = when {
                        disease.bract_Mosaic == cleanSymptom -> "Bract Mosaic"
                        disease.bunchy_Top == cleanSymptom -> "Bunchy Top"
                        disease.cmv == cleanSymptom -> "CMV"
                        disease.gen_Mosaic == cleanSymptom -> "General Mosaic"
                        disease.scmv == cleanSymptom -> "SCMV"
                        else -> "No Allocation"
                    }
                    
                    if (!diseaseMatches.containsKey(diseaseName)) {
                        diseaseMatches[diseaseName] = mutableListOf()
                    }
                    
                    // Add confidence score for this symptom match
                    val symptomIndex = symptoms.indexOf(symptom)
                    if (symptomIndex < confidenceScores.size) {
                        diseaseMatches[diseaseName]?.add(confidenceScores[symptomIndex])
                    }
                }
            }
        }
        
        // Return all diseases sorted by average confidence
        return diseaseMatches.map { (diseaseName, confidences) ->
            val avgConfidence = if (confidences.isNotEmpty()) {
                (confidences.average() * 100).roundToInt()
            } else 50
            Pair(diseaseName, avgConfidence)
        }.sortedByDescending { it.second }
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
        val bitmapExtra = intent.getByteArrayExtra("captured_bitmap")
        if (bitmapExtra == null) {
            Toast.makeText(this, "No image data received", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        capturedNewImage = bitmapExtra
        location = intent.getDoubleArrayExtra("location") ?: doubleArrayOf(0.0, 0.0)
        detectionInfo = intent.getStringExtra("detectionJson") ?: "No detection info"

        // If location was passed in the intent, use it
        if (location.isNotEmpty() && location[0] != 0.0 && location[1] != 0.0) {
            lat = location[0]
            longt = location[1]
            currentLocation = Location("provided")
            currentLocation?.latitude = lat ?: 0.0
            currentLocation?.longitude = longt ?: 0.0
        }

        // Convert byte array to bitmap (already rotated from CameraFragment)
        bitmap = BitmapFactory.decodeByteArray(capturedNewImage, 0, capturedNewImage.size)
        capturedNewImageView.setImageBitmap(bitmap)
        rotation = intent.getIntExtra("rotation", 0)
        // Process the detection info if available
        if (detectionInfo.isNotEmpty() && detectionInfo != "No detection info") {
            try {
                val gson = Gson()
                val type = object : TypeToken<List<DetectionResult>>() {}.type
                detectionResults = gson.fromJson(detectionInfo, type)
                
                // Extract symptoms, bounding boxes, and confidence scores
                symptomsDetected = arrayListOf()
                val tempBoundingBoxes = mutableListOf<BoundingBox>()
                val tempConfidenceScores = mutableListOf<Float>()
                
                detectionResults.forEach { detection ->
                    detection.categories.forEach { category ->
                        // Clean up the label (remove \r and trim)
                        val cleanLabel = category.label.replace("\r", "").trim()
                        symptomsDetected.add(cleanLabel)
                        tempBoundingBoxes.add(detection.boundingBox)
                        tempConfidenceScores.add(category.score)
                    }
                }
                
                boundingBoxes = tempBoundingBoxes
                confidenceScores = tempConfidenceScores
                
            } catch (e: Exception) {
                Log.e("CaptureImageNew", "Error parsing detection JSON", e)
                symptomsDetected = arrayListOf("Error parsing detections")
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

            // Save to database
            saveCaptureToDatabase(imageFile, filename)

            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveCaptureToDatabase(imageFile: File, filename: String) {
        try {
            val dbHelper = DatabaseHelper(this)
            val captureDao = CaptureDao(dbHelper.writableDatabase)
            
            Log.d("CaptureImageNew", "Saving bounding boxes: ${boundingBoxes.size} boxes")
            Log.d("CaptureImageNew", "Bounding boxes JSON: ${if (boundingBoxes.isNotEmpty()) Gson().toJson(boundingBoxes) else "null"}")
            
            val captureRecord = CaptureRecord(
                imagePath = imageFile.absolutePath,
                imageName = filename,
                detectionResults = if (detectionResults.isNotEmpty()) Gson().toJson(detectionResults) else null,
                symptomsDetected = if (symptomsDetected.isNotEmpty()) symptomsDetected.joinToString(", ") else null,
                confidenceScores = if (confidenceScores.isNotEmpty()) confidenceScores.joinToString(", ") else null,
                boundingBoxes = if (boundingBoxes.isNotEmpty()) Gson().toJson(boundingBoxes) else null,
                latitude = currentLocation?.latitude,
                longitude = currentLocation?.longitude,
                timestamp = System.currentTimeMillis() / 1000
            )
            
            captureDao.insertCapture(captureRecord)
            Log.d("CaptureImageNew", "Capture saved successfully")
        } catch (e: Exception) {
            Log.e("CaptureImageNew", "Error saving to database", e)
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