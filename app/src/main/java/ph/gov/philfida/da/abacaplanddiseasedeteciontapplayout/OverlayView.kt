package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.LinkedList
import org.tensorflow.lite.task.vision.detector.Detection
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val TAG = "OverlayView"

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()
    private var debugPaint = Paint()

    private var scaleFactor: Float = 1f
    private var scaleFactorX: Float = 1f
    private var scaleFactorY: Float = 1f

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        Log.d(TAG, "Clearing overlay")
        results = LinkedList<Detection>()
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        debugPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        // Text background paint
        textBackgroundPaint.apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40f
            alpha = 180
        }

        // Text paint
        textPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 40f
            isAntiAlias = true
        }

        // Bounding box paint
        boxPaint.apply {
            color = ContextCompat.getColor(context!!, R.color.bounding_box_color)
            strokeWidth = 6F
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        // Debug paint for testing
        debugPaint.apply {
            color = Color.RED
            strokeWidth = 4F
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d(TAG, "onDraw called - Results: ${results.size}, View size: ${width}x${height}, Image size: ${imageWidth}x${imageHeight}")
        Log.d(TAG, "Scale factors - X: $scaleFactorX, Y: $scaleFactorY, Combined: $scaleFactor")

        // Draw a debug border around the entire view
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), debugPaint)

        if (results.isEmpty()) {
            Log.d(TAG, "No detection results to draw")
            return
        }

        for ((index, result) in results.withIndex()) {
            val boundingBox = result.boundingBox

            Log.d(TAG, "Drawing detection $index:")
            Log.d(TAG, "  Original box: left=${boundingBox.left}, top=${boundingBox.top}, right=${boundingBox.right}, bottom=${boundingBox.bottom}")

            // Calculate scaled coordinates using separate X and Y scale factors
            val left = boundingBox.left * scaleFactorX
            val top = boundingBox.top * scaleFactorY
            val right = boundingBox.right * scaleFactorX
            val bottom = boundingBox.bottom * scaleFactorY

            Log.d(TAG, "  Scaled box: left=$left, top=$top, right=$right, bottom=$bottom")

            // Ensure coordinates are within view bounds
            val clampedLeft = max(0f, min(left, width.toFloat()))
            val clampedTop = max(0f, min(top, height.toFloat()))
            val clampedRight = max(0f, min(right, width.toFloat()))
            val clampedBottom = max(0f, min(bottom, height.toFloat()))

            Log.d(TAG, "  Clamped box: left=$clampedLeft, top=$clampedTop, right=$clampedRight, bottom=$clampedBottom")

            // Skip if the box is too small or invalid
            if (clampedRight - clampedLeft < 10 || clampedBottom - clampedTop < 10) {
                Log.w(TAG, "  Skipping box $index - too small or invalid")
                continue
            }

            // Draw bounding box
            val drawableRect = RectF(clampedLeft, clampedTop, clampedRight, clampedBottom)
            canvas.drawRect(drawableRect, boxPaint)

            // Create label text
            val category = result.categories.firstOrNull()
            val label = category?.label ?: "Unknown"
            val confidence = category?.score ?: 0f
            val drawableText = "$label ${String.format("%.2f", confidence)}"

            Log.d(TAG, "  Label: $drawableText")

            // Calculate text dimensions
            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width().toFloat()
            val textHeight = bounds.height().toFloat()

            // Position text above the bounding box if possible, otherwise inside
            val textX = clampedLeft + BOUNDING_RECT_TEXT_PADDING
            val textY = if (clampedTop - textHeight - BOUNDING_RECT_TEXT_PADDING > 0) {
                clampedTop - BOUNDING_RECT_TEXT_PADDING
            } else {
                clampedTop + textHeight + BOUNDING_RECT_TEXT_PADDING
            }

            // Draw text background
            val backgroundLeft = textX - BOUNDING_RECT_TEXT_PADDING / 2
            val backgroundTop = textY - textHeight - BOUNDING_RECT_TEXT_PADDING / 2
            val backgroundRight = textX + textWidth + BOUNDING_RECT_TEXT_PADDING / 2
            val backgroundBottom = textY + BOUNDING_RECT_TEXT_PADDING / 2

            canvas.drawRect(
                backgroundLeft,
                backgroundTop,
                backgroundRight,
                backgroundBottom,
                textBackgroundPaint
            )

            // Draw text
            canvas.drawText(drawableText, textX, textY, textPaint)

            Log.d(TAG, "  Drew detection $index successfully")
        }

        Log.d(TAG, "onDraw completed")
    }

    fun setResults(
        detectionResults: MutableList<Detection>,
        imageHeight: Int,
        imageWidth: Int,
    ) {
        Log.d(TAG, "setResults called:")
        Log.d(TAG, "  Detection count: ${detectionResults.size}")
        Log.d(TAG, "  Image dimensions: ${imageWidth}x${imageHeight}")
        Log.d(TAG, "  View dimensions: ${width}x${height}")

        results = detectionResults
        this.imageWidth = imageWidth
        this.imageHeight = imageHeight

        // Only calculate scale factors if view dimensions are available
        if (width > 0 && height > 0 && imageWidth > 0 && imageHeight > 0) {
            // Calculate separate scale factors for X and Y
            scaleFactorX = width.toFloat() / imageWidth.toFloat()
            scaleFactorY = height.toFloat() / imageHeight.toFloat()

            // Also calculate the original combined scale factor for backwards compatibility
            scaleFactor = max(scaleFactorX, scaleFactorY)

            Log.d(TAG, "  Scale factors calculated:")
            Log.d(TAG, "    X: $scaleFactorX")
            Log.d(TAG, "    Y: $scaleFactorY")
            Log.d(TAG, "    Combined: $scaleFactor")
        } else {
            Log.w(TAG, "  Cannot calculate scale factors yet - invalid dimensions")
            scaleFactorX = 1f
            scaleFactorY = 1f
            scaleFactor = 1f
        }

        // Log details about each detection
        detectionResults.forEachIndexed { index, detection ->
            val category = detection.categories.firstOrNull()
            Log.d(TAG, "  Detection $index: ${category?.label} (${category?.score}) at ${detection.boundingBox}")
        }

        // Trigger redraw with post to ensure it happens on UI thread
        post {
            invalidate()
            Log.d(TAG, "  invalidate() called, view will redraw")
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged: ${oldw}x${oldh} -> ${w}x${h}")

        // Recalculate scale factors when view size changes
        if (imageWidth > 0 && imageHeight > 0) {
            scaleFactorX = w.toFloat() / imageWidth.toFloat()
            scaleFactorY = h.toFloat() / imageHeight.toFloat()
            scaleFactor = max(scaleFactorX, scaleFactorY)

            Log.d(TAG, "  Updated scale factors - X: $scaleFactorX, Y: $scaleFactorY, Combined: $scaleFactor")

            // Trigger redraw with new scale factors
            invalidate()
        }
    }

    // Get the detected objects
    fun getDetectedObjects(): List<Detection> {
        Log.d(TAG, "getDetectedObjects called - returning ${results.size} objects")
        return results
    }

    // Force a redraw for debugging
    fun forceRedraw() {
        Log.d(TAG, "forceRedraw called")
        post {
            invalidate()
            requestLayout()
        }
    }

    // Check if view is actually drawable
    fun checkDrawableState(): String {
        val state = StringBuilder()
        state.append("Drawable state: ")
        state.append("visibility=${visibility}, ")
        state.append("width=${width}, height=${height}, ")
        state.append("isShown=${isShown}, ")
        state.append("willNotDraw=${willNotDraw()}, ")
        state.append("hasOverlappingRendering=${hasOverlappingRendering()}, ")
        state.append("alpha=${alpha}")
        return state.toString()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 16
        private const val TAG = "OverlayView"
    }
}