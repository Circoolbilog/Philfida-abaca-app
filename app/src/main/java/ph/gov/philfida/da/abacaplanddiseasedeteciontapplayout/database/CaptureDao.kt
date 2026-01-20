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

    fun getAllCaptures(): List<CaptureRecord> {
        val captures = mutableListOf<CaptureRecord>()
        val cursor = db.query(
            DatabaseHelper.TABLE_CAPTURES,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_TIMESTAMP} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val record = CaptureRecord(
                    id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    imagePath = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_PATH)),
                    imageName = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_NAME)),
                    detectionResults = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DETECTION_RESULTS)),
                    symptomsDetected = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_SYMPTOMS_DETECTED)),
                    confidenceScores = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONFIDENCE_SCORES)),
                    boundingBoxes = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BOUNDING_BOXES)),
                    latitude = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE))) null else getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE)),
                    longitude = if (isNull(getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE))) null else getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE)),
                    timestamp = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIMESTAMP))
                )
                captures.add(record)
            }
            close()
        }
        return captures
    }

    fun deleteCaptureByImagePath(imagePath: String): Int {
        return db.delete(
            DatabaseHelper.TABLE_CAPTURES,
            "${DatabaseHelper.COLUMN_IMAGE_PATH} = ?",
            arrayOf(imagePath)
        )
    }
}
