package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "abaca_app.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CAPTURES = "captures"
        const val COLUMN_ID = "id"
        const val COLUMN_IMAGE_PATH = "image_path"
        const val COLUMN_IMAGE_NAME = "image_name"
        const val COLUMN_DETECTION_RESULTS = "detection_results"
        const val COLUMN_SYMPTOMS_DETECTED = "symptoms_detected"
        const val COLUMN_CONFIDENCE_SCORES = "confidence_scores"
        const val COLUMN_BOUNDING_BOXES = "bounding_boxes"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_CAPTURES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_IMAGE_PATH + " TEXT,"
                + COLUMN_IMAGE_NAME + " TEXT,"
                + COLUMN_DETECTION_RESULTS + " TEXT,"
                + COLUMN_SYMPTOMS_DETECTED + " TEXT,"
                + COLUMN_CONFIDENCE_SCORES + " TEXT,"
                + COLUMN_BOUNDING_BOXES + " TEXT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL,"
                + COLUMN_TIMESTAMP + " INTEGER" + ")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CAPTURES")
        onCreate(db)
    }
}
