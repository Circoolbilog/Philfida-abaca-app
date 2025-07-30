package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryAdapter(
    private val onImageClick: (File) -> Unit
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
            onImageClick(imageFile)
        }
    }

    override fun getItemCount(): Int = imageFiles.size

    fun updateImages(newImages: List<File>) {
        imageFiles = newImages
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean = imageFiles.isEmpty()
}