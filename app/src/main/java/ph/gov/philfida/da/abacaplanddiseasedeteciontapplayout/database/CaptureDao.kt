package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class CaptureDao(private val db: SQLiteDatabase) {

    fun insertCapture(record: CaptureRecord): Long {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_IMAGE_PATH, record.imagePath)
            put(DatabaseHelper.COLUMN_IMAGE_NAME, record.imageName)
            put(DatabaseHelper.COLUMN_DETECTION_RESULTS, record.detectionResults)
            put(DatabaseHelper.COLUMN_SYMPTOMS_DETECTED, record.symptomsDetected)
            put(DatabaseHelper.COLUMN_CONFIDENCE_SCORES, record.confidenceScores)
            put(DatabaseHelper.COLUMN_BOUNDING_BOXES, record.boundingBoxes)
            put(DatabaseHelper.COLUMN_LATITUDE, record.latitude)
            put(DatabaseHelper.COLUMN_LONGITUDE, record.longitude)
            put(DatabaseHelper.COLUMN_TIMESTAMP, record.timestamp)
        }
        return db.insert(DatabaseHelper.TABLE_CAPTURES, null, values)
    }

    fun deleteCaptureByImagePath(imagePath: String): Int {
        return db.delete(
            DatabaseHelper.TABLE_CAPTURES,
            "${DatabaseHelper.COLUMN_IMAGE_PATH} = ?",
            arrayOf(imagePath)
        )
    }
    
    // You might want to add a method to query all captures if needed for the gallery
}
