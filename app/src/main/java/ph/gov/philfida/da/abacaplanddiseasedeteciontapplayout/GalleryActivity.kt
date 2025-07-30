package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryActivity : AppCompatActivity() {

    private lateinit var galleryRecyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter

    companion object {
        const val CUSTOM_DIRECTORY_NAME = "AbaddAppImages"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        // Set up action bar with back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Image Gallery"

        setupGallery()
        loadGalleryImages()
    }

    private fun setupGallery() {
        galleryRecyclerView = findViewById(R.id.galleryRecyclerView)
        galleryRecyclerView.layoutManager = GridLayoutManager(this, 3)

        galleryAdapter = GalleryAdapter { imageFile ->
            openImageInNewActivity(imageFile)
        }
        galleryRecyclerView.adapter = galleryAdapter
    }

    private fun getCustomDirectory(): File {
        val externalDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customDir = File(externalDir, CUSTOM_DIRECTORY_NAME)
        if (!customDir.exists()) {
            customDir.mkdirs()
        }
        return customDir
    }

    private fun loadGalleryImages() {
        val customDir = getCustomDirectory()
        val imageFiles = customDir.listFiles { file ->
            file.isFile && (file.extension.lowercase() == "jpg" ||
                    file.extension.lowercase() == "jpeg" ||
                    file.extension.lowercase() == "png")
        }?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()

        galleryAdapter.updateImages(imageFiles)
    }

    private fun openImageInNewActivity(imageFile: File) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra("image_path", imageFile.absolutePath)
        intent.putExtra("image_name", imageFile.name)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Refresh gallery when returning to this activity
        loadGalleryImages()
    }
}