package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TempDBHelper  extends SQLiteOpenHelper {
    public static final String HOLDER_TABLE = "HOLDER_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_IMAGE_NAME = "IMAGE_NAME";
    public static final String COLUMN_SYMPTOMS_DETECTED = "SYMPTOMS_DETECTED";
    public static final String COLUMN_CAPTURED_IMAGE_1 = "CAPTURED_IMAGE1";
    public static final String COLUMN_CAPTURED_IMAGE_2 = "CAPTURED_IMAGE2";
    public static final String COLUMN_CONFIDENCE = "CONFIDENCE";

    public TempDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + HOLDER_TABLE + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_NAME + " TEXT, " + COLUMN_SYMPTOMS_DETECTED + " TEXT, " + COLUMN_CAPTURED_IMAGE_1 + " BLOB, " +
                COLUMN_CAPTURED_IMAGE_2 + " BLOB, " + COLUMN_CONFIDENCE + " REAL )";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void deleteTable(){
        SQLiteDatabase database = this.getWritableDatabase();
        String queryString = "DROP TABLE IF EXISTS " + HOLDER_TABLE;
        database.execSQL(queryString);
    }

    public boolean addEntry(TempDBModel dbModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IMAGE_NAME, dbModel.getImageName());
        cv.put(COLUMN_CAPTURED_IMAGE_1, dbModel.getCapturedImage1());
        cv.put(COLUMN_CAPTURED_IMAGE_2, dbModel.getCapturedImage2());
        cv.put(COLUMN_SYMPTOMS_DETECTED,dbModel.getSymptomsDetected());
        cv.put(COLUMN_CONFIDENCE, dbModel.getConfidence());

        long insert = db.insert(HOLDER_TABLE, null, cv);
        return insert != -1;
    }
    public List<TempDBModel> getHoldedData(){
        List<TempDBModel> re = new ArrayList<>();
        //get the temporaryly stored data from the database
        String queryString = "SELECT * FROM " + HOLDER_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(queryString);
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()){
            do {
                int id = cursor.getInt(0);
                String imageNeme = cursor.getString(2);
                byte[] capturedImage1 = cursor.getBlob(3);
                byte[] capturedImage2 = cursor.getBlob(4);
                String symptomsDetected = cursor.getString(5);
                Float confidence = cursor.getFloat(6);
                TempDBModel newEntry = new TempDBModel(id,imageNeme,capturedImage1,capturedImage2,symptomsDetected,confidence);
            }while (cursor.moveToNext());
        }else {
            //do not add?
        }
        cursor.close();
        db.close();
        return re;
    }
}
