/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.tensorflow.lite.task.vision.detector.Detection
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AboutApp
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseIndex
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.MapActivity
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.SettingsActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Objects

// Import stuff
class MainActivity : AppCompatActivity(), ObjectDetectorHelper.DetectorListener {
    var toggle: ActionBarDrawerToggle? = null
    private var user: FirebaseUser? = null
    private val reference2: DatabaseReference? = null
    var firstName: String? = null
    var lastName: String? = null
    var middleName: String? = null
    var email: String? = null
    var birthday: String? = null
    var permAdd: String? = null
    var occupation: String? = null
    var institution: String? = null

    //    private boolean Bract_Mosaic, Bunchy_Top, CMV, Gen_Mosaic, SCMV;
    private val stringVal_Bract_Mosaic: String? = null
    private val stringVal_Bunchy_Top: String? = null
    private val stringVal_CMV: String? = null
    private val stringVal_Gen_Mosaic: String? = null
    private val stringVal_SCMV: String? = null
    private val stringVal_No_Allocation: String? = null
    private val DIName: String? = null
    private val DIDesc: String? = null
    private val DIPicture: String? = null
    private val DITreatment: String? = null

    private val currentDbSize = 39

    var ALL_PERMISSIONS: Int = 101
    var saving: Boolean = false

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private var uploadedBitmap: Bitmap? = null
    private lateinit var loadingScreen: LinearLayout
    private lateinit var loadingText: TextView
    private var imageLocation: DoubleArray? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            processUploadedImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingScreen = findViewById(R.id.loadingScreen)
        loadingText = findViewById(R.id.loadingText)

        if ((this.getApplication() as SettingsContainer).getShowWelcome() == null) {
            (this.getApplication() as SettingsContainer).setShowWelcome(true)
        }
        if ((this.getApplication() as SettingsContainer).getShowWelcome()) {
            welcomeScreen()
        }
        askPermissions()
        try {
            if (!(this.getApplication() as SettingsContainer).getGuest()) {
                this.userDBDetails
                loadUserData()
                saveUserData()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
        // Initialize the database
        val dbHelper = DiseaseInfoDBHelper(this)

        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            objectDetectorListener = this
        )
    }

    private fun askPermissions() {
        // Ask user for permission for read and write, and location
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)
    }

    override fun onResume() {
        super.onResume()
    }


    private fun welcomeScreen() {
        startActivity(intent)
    }


    //Load user data from Shared Preference(locally stored)
    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        lastName = sharedPreferences.getString(LAST_NAME, "")
        firstName = sharedPreferences.getString(FIRST_NAME, "")
        middleName = sharedPreferences.getString(MIDDLE_NAME, "")
        email = sharedPreferences.getString(EMAIL, "")
        birthday = sharedPreferences.getString(BIRTHDAY, "")
        permAdd = sharedPreferences.getString(PERM_ADD, "")
        occupation = sharedPreferences.getString(OCCUPATION, "")
        institution = sharedPreferences.getString(INSTITUTION, "")
    }


    //set up what happens whe user selects item from the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private val userDBDetails: Unit
        //Download user data from Firebase Database
        get() {
            user = FirebaseAuth.getInstance().getCurrentUser()
            val reference = FirebaseDatabase.getInstance().getReference("Users")
            val userID = user!!.getUid()
            reference.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userProfile =
                        snapshot.getValue<User?>(
                            User::class.java
                        )
                    if (userProfile != null) {
                        firstName = userProfile.firstName
                        lastName = userProfile.lastName
                        middleName = userProfile.middleName
                        email = userProfile.email
                        birthday = userProfile.birthday
                        permAdd = userProfile.permanentAddress
                        institution = userProfile.institution
                        occupation = userProfile.occupation
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

    //Open Diagnose Activity
    fun openDiagnoseActivity(view: View?) {
        val options = arrayOf("Open Camera", "Upload Picture")
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options) {
            override fun isEnabled(position: Int): Boolean {
                return true
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(Color.BLACK)
                return view
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Select Option")
            .setAdapter(adapter) { _, which ->
                if (which == 0) {
                    val diagnose = Intent(this, NewDetectorActivity::class.java)
                    startActivity(diagnose)
                } else if (which == 1) {
                    getContent.launch("image/*")
                }
            }
            .show()
    }

    private fun processUploadedImage(uri: Uri) {
        showLoading("Processing Uploaded Image...")
        
        Thread {
            try {
                // Try to extract location metadata first
                imageLocation = try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val exifInterface = ExifInterface(inputStream)
                        val latLong = FloatArray(2)
                        if (exifInterface.getLatLong(latLong)) {
                            doubleArrayOf(latLong[0].toDouble(), latLong[1].toDouble())
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error extracting EXIF metadata", e)
                    null
                }

                uploadedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri).copy(Bitmap.Config.ARGB_8888, true)
                }

                uploadedBitmap?.let {
                    objectDetectorHelper.detect(it, 0)
                } ?: run {
                    runOnUiThread {
                        hideLoading()
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing uploaded image", e)
                runOnUiThread {
                    hideLoading()
                    Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun showLoading(text: String) {
        runOnUiThread {
            loadingText.text = text
            loadingScreen.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            loadingScreen.visibility = View.GONE
        }
    }

    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        val bitmap = uploadedBitmap ?: run {
            hideLoading()
            return
        }
        
        // If we found a location in metadata, use it
        if (imageLocation != null) {
            val location = android.location.Location("exif")
            location.latitude = imageLocation!![0]
            location.longitude = imageLocation!![1]
            startCaptureActivity(bitmap, results, location)
            return
        }

        // Otherwise, fall back to current location
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // If location permission not granted, proceed with default location
            startCaptureActivity(bitmap, results, null)
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            startCaptureActivity(bitmap, results, location)
        }.addOnFailureListener {
            startCaptureActivity(bitmap, results, null)
        }
    }

    private fun startCaptureActivity(bitmap: Bitmap, results: MutableList<Detection>?, location: android.location.Location?) {
        try {
            // Save bitmap to a temporary file to avoid TransactionTooLargeException (DeadObjectException)
            val tempFile = File(cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }

            val gson = Gson()
            val jsonDetection = gson.toJson(results)

            val intent = Intent(this, CaptureImageNewActivity::class.java)
            intent.putExtra("captured_image_path", tempFile.absolutePath)
            intent.putExtra("detectionJson", jsonDetection)
            intent.putExtra("rotation", 0)
            
            if (location != null) {
                intent.putExtra("location", doubleArrayOf(location.latitude, location.longitude))
            } else {
                intent.putExtra("location", doubleArrayOf(0.0, 0.0))
            }

            runOnUiThread {
                hideLoading()
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting capture activity", e)
            runOnUiThread {
                hideLoading()
                Toast.makeText(this, "Error preparing detection results", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onError(error: String) {
        runOnUiThread {
            hideLoading()
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    //Open Assesment Activity
    fun openAssessmentActivity(view: View?) {
        val assessment = Intent(this, GalleryActivity::class.java)
        startActivity(assessment)
    }


    //open Disease Index Activity
    fun openDiseaseIndex(view: View?) {
        val intent = Intent(this@MainActivity, DiseaseIndex::class.java)
        startActivity(intent)
    }

    fun openAboutApp(view: View?) {
        val intent = Intent(this@MainActivity, AboutApp::class.java)
        startActivity(intent)
    }
    fun openSettings(view: View?) {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun openMap(view: View?) {
        val intent: Intent = Intent(this@MainActivity, MapActivity::class.java)
        startActivity(intent)
    }



    fun downloadImage(imageURL: String?, name: String) {
        Log.d(TAG, "downloadImage: try download")
        if (!verifyPermissions()) {
            return
        }

        val dirPath = Environment.getExternalStorageDirectory().toString() + "/Pictures/Diseases/"

        val dir = File(dirPath)

        val fileName = name

        Glide.with(this)
            .load(imageURL)
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).getBitmap()
                    saveImage(bitmap, dir, fileName)
                    Log.d(TAG, "downloadImage: save " + imageURL)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Log.d(TAG, "onLoadFailed: failed")
                }
            })
    }

    fun verifyPermissions(): Boolean {
        // This will return the current Status

        val permissionExternalMemory =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = arrayOf<String?>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1)
            return false
        }

        return true
    }

    private fun saveImage(image: Bitmap, storageDir: File, imageFileName: String) {
        val img = File(storageDir.toString() + "/" + imageFileName + ".jpg")
        var successDirCreated = false
        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir()
            Log.d(TAG, "downloadImage: try download")
        }

        Log.d(TAG, "downloadImage: " + img.toString() + " exists: " + img.exists())
        if (img.exists()) {
            Log.d(TAG, "downloadImage: File exists")
            return
        }
        val imageFile = File(storageDir, imageFileName)
        val fOut: OutputStream?
        if (this.isBuildVersionQ) {
            //Save Image File
            val imagePathUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val imageOneResolver = getContentResolver()
            val imageOneCV = ContentValues()
            imageOneCV.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName + ".jpg")
            imageOneCV.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            imageOneCV.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/Diseases"
            )

            val imageUri = imageOneResolver.insert(imagePathUri, imageOneCV)
            Log.d(TAG, "downloadImage: try save Q")
            try {
                fOut = imageOneResolver.openOutputStream(Objects.requireNonNull<Uri?>(imageUri))
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut!!)
                Objects.requireNonNull<OutputStream?>(fOut).close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.d(TAG, "downloadImage: " + e.message)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Log.d(TAG, "downloadImage: saved")
        } else {
            try {
                fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
                Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //save user info to shared prefs(store locally)
    private val isBuildVersionQ: Boolean
        /**
         * end
         * download images from database
         */
        get() = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q

    fun saveUserData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(LAST_NAME, lastName)
        editor.putString(FIRST_NAME, firstName)
        editor.putString(MIDDLE_NAME, middleName)
        editor.putString(EMAIL, email)
        editor.putString(BIRTHDAY, birthday)
        editor.putString(PERM_ADD, permAdd)
        editor.putString(OCCUPATION, occupation)
        editor.putString(INSTITUTION, institution)
        editor.apply()
    }

    companion object {
        //    private int symptomID;
        const val SHARED_PREFS: String = "USER_DATA"
        const val EMAIL: String = "EMAIL"
        const val LAST_NAME: String = "LAST_NAME"
        const val FIRST_NAME: String = "FIRST_NAME"
        const val MIDDLE_NAME: String = "MIDDLE_NAME"
        const val BIRTHDAY: String = "BIRTHDAY"
        const val PERM_ADD: String = "PERM_ADD"
        const val OCCUPATION: String = "OCCUPATION"
        const val INSTITUTION: String = "INSTITUTION"
        private const val TAG = "MainActivity"
    }

}
