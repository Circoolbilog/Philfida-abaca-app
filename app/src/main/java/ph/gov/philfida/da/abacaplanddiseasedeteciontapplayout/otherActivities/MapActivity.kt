/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureDao
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.DatabaseHelper
import java.io.File

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var btnRecenter: FloatingActionButton
    private val TAG = "MapActivity"

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load/initialize the osmdroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        btnRecenter = findViewById(R.id.btnRecenter)

        // Set up the map
        setupMap()

        // Load pins from database
        loadPinsFromDatabase()

        // Setup recenter button
        btnRecenter.setOnClickListener {
            recenterMap()
        }

        // Request permissions
        requestPermissionsIfNecessary(arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun setupMap() {
        // Set tile source
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Enable multitouch controls
        mapView.setMultiTouchControls(true)

        // Set default zoom level
        mapView.controller.setZoom(10.0)
        
        // Add location overlay
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)
    }

    private fun recenterMap() {
        val myLocation = locationOverlay.myLocation
        if (myLocation != null) {
            mapView.controller.animateTo(myLocation)
            mapView.controller.setZoom(15.0)
        } else {
            // If no GPS lock, center on the first pin found
            val dbHelper = DatabaseHelper(this)
            val captureDao = CaptureDao(dbHelper.readableDatabase)
            val captures = captureDao.getAllCaptures()
            val firstWithLocation = captures.firstOrNull { it.latitude != null && it.longitude != null }
            
            if (firstWithLocation != null) {
                mapView.controller.animateTo(GeoPoint(firstWithLocation.latitude!!, firstWithLocation.longitude!!))
                mapView.controller.setZoom(15.0)
            } else {
                // Default Philippines center
                mapView.controller.animateTo(GeoPoint(12.8797, 121.7740))
            }
        }
    }

    private fun loadPinsFromDatabase() {
        try {
            val dbHelper = DatabaseHelper(this)
            val captureDao = CaptureDao(dbHelper.readableDatabase)
            val captures = captureDao.getAllCaptures()

            Log.d(TAG, "Loading ${captures.size} pins from database")

            var hasValidPoints = false
            var minLat = 90.0
            var maxLat = -90.0
            var minLon = 180.0
            var maxLon = -180.0

            for (capture in captures) {
                if (capture.latitude != null && capture.longitude != null && capture.latitude != 0.0) {
                    hasValidPoints = true
                    
                    // Update bounds for auto-zoom
                    minLat = minOf(minLat, capture.latitude)
                    maxLat = maxOf(maxLat, capture.latitude)
                    minLon = minOf(minLon, capture.longitude)
                    maxLon = maxOf(maxLon, capture.longitude)

                    val marker = Marker(mapView)
                    marker.position = GeoPoint(capture.latitude, capture.longitude)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    
                    val diseaseInfo = capture.symptomsDetected ?: "Unknown Disease"
                    marker.title = "Detection: $diseaseInfo"
                    marker.snippet = "Date: ${java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(java.util.Date(capture.timestamp * 1000))}"

                    // Ensure icon is visible
                    val imgFile = File(capture.imagePath)
                    if (imgFile.exists()) {
                        val options = BitmapFactory.Options()
                        options.inSampleSize = 4 
                        val bitmap = BitmapFactory.decodeFile(capture.imagePath, options)
                        if (bitmap != null) {
                            val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, 120, 120, true)
                            marker.icon = BitmapDrawable(resources, scaledBitmap)
                        }
                    } else {
                        // Use default marker if image missing
                        marker.icon = ContextCompat.getDrawable(this, R.drawable.ic_m_map)
                    }

                    mapView.overlays.add(marker)
                }
            }

            if (hasValidPoints) {
                // If we have points, center the map to include them
                val centerLat = (minLat + maxLat) / 2
                val centerLon = (minLon + maxLon) / 2
                mapView.controller.setCenter(GeoPoint(centerLat, centerLon))
            } else {
                recenterMap()
            }
            
            mapView.invalidate() 
        } catch (e: Exception) {
            Log.e(TAG, "Error loading pins from database", e)
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationOverlay.enableMyLocation()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationOverlay.disableMyLocation()
    }
}