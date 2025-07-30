package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var fullImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        fullImageView = findViewById(R.id.fullImageView)

        val imagePath = intent.getStringExtra("image_path")
        val imageName = intent.getStringExtra("image_name")

        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            fullImageView.setImageBitmap(bitmap)
        }

        // Set title to image name if available
        if (imageName != null) {
            supportActionBar?.title = imageName
        }

        // Add back button functionality
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}