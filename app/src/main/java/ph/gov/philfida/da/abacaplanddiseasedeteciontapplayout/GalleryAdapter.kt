package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.CaptureDao
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database.DatabaseHelper
import java.io.File

class GalleryAdapter(
    private val onImageClick: (File) -> Unit,
    private val onImageDeleted: () -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>() {

    private var imageFiles: List<File> = emptyList()

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.galleryImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageFile = imageFiles[position]

        // Load image thumbnail with proper scaling
        val options = BitmapFactory.Options()
        options.inSampleSize = 4 // Scale down for thumbnails
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)
        holder.imageView.setImageBitmap(bitmap)

        // Set click listener
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ImageDetailActivity::class.java)
            intent.putExtra("IMAGE_PATH", imageFile.absolutePath)
            holder.itemView.context.startActivity(intent)
        }
        
        // Set long click listener for delete
        holder.itemView.setOnLongClickListener {
            showDeleteDialog(holder.itemView, imageFile)
            true
        }
    }

    override fun getItemCount(): Int = imageFiles.size

    fun updateImages(newImages: List<File>) {
        imageFiles = newImages
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = imageFiles.isEmpty()
    
    private fun showDeleteDialog(view: View, imageFile: File) {
        AlertDialog.Builder(view.context)
            .setTitle("Delete Image")
            .setMessage("Delete ${imageFile.name}?")
            .setPositiveButton("Delete") { _, _ ->
                deleteImage(view, imageFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteImage(view: View, imageFile: File) {
        try {
            val dbHelper = DatabaseHelper(view.context)
            val captureDao = CaptureDao(dbHelper.writableDatabase)
            captureDao.deleteCaptureByImagePath(imageFile.absolutePath)
            imageFile.delete()
            Toast.makeText(view.context, "Image deleted", Toast.LENGTH_SHORT).show()
            onImageDeleted()
        } catch (e: Exception) {
            Toast.makeText(view.context, "Failed to delete image", Toast.LENGTH_SHORT).show()
        }
    }
}