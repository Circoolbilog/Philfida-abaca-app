package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseInfo;

public class DiseaseInfoSymptomsDbHelper extends SQLiteOpenHelper {

    public static final String SYMPTOM_NAME = "DiseaseName";
    public static final String DISEASES_TABLE = "Diseases";
    public static final String COLUMN_BRACT_MOSAIC = "Bract_Mosaic";
    public static final String COLUMN_BUNCHY_TOP = "Bunchy_Top";
    public static final String COLUMN_CMV = "CMV";
    public static final String COLUMN_SCMV = "SCMV";
    public static final String COLUMN_GEN_MOSAIC = "Gen_Mosaic";
    public static final String COLUMN_NO_ALLOCATION = "No_Allocation";
    public static final String COLUMN_ID = "ID";
    private static final String TAG = "DiseaseInfoSymptomsDbHe";
    public static String TABLE_NAME;

    public DiseaseInfoSymptomsDbHelper(@Nullable Context context) {
        super(context, "DiseaseInfoSymptoms.db", null, 1);
    }


    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + DISEASES_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NO_ALLOCATION + " TEXT, " +
                COLUMN_BRACT_MOSAIC + " TEXT, " +
                COLUMN_BUNCHY_TOP + " TEXT, " +
                COLUMN_CMV + " TEXT, " +
                COLUMN_GEN_MOSAIC + " TEXT, " +
                COLUMN_SCMV + " TEXT )";

        db.execSQL(createTableStatement);
    }

    //runs when version number changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<DiseaseDBModel> getDiseases() {
        List<DiseaseDBModel> returnList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + DISEASES_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            //loop through results, create a new disease object, put it to return list
            do {
                int diseaseID = cursor.getInt(0);
                String noAlloc = cursor.getString(1);
                String bract = cursor.getString(2);
                String bunch = cursor.getString(3);
                String cmv = cursor.getString(4);
                String genMos = cursor.getString(5);
                String scmv = cursor.getString(6);

                DiseaseDBModel newDisease = new DiseaseDBModel(diseaseID, noAlloc, bract, bunch, cmv, genMos, scmv);
                returnList.add(newDisease);
            } while (cursor.moveToNext());
        } else {
            //do not add blah blah blah
        }
        //close both cursor and db when done
        cursor.close();
        db.close();
        return returnList;
    }

    public boolean clear(@org.jetbrains.annotations.NotNull DiseaseDBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = " DELETE FROM " + DISEASES_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.isNull(0);
    }

    public boolean addOneSymptom(DiseaseDBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, dbModel.getId());
        //cv.put(SYMPTOM_NAME,dbModel.getSymptomName());
        cv.put(COLUMN_BRACT_MOSAIC, dbModel.getBract_Mosaic());
        cv.put(COLUMN_BUNCHY_TOP, dbModel.getBunchy_Top());
        cv.put(COLUMN_CMV, dbModel.getCMV());
        cv.put(COLUMN_GEN_MOSAIC, dbModel.getGen_Mosaic());
        cv.put(COLUMN_SCMV, dbModel.getSCMV());
        cv.put(COLUMN_SCMV, dbModel.getNo_Allocation());

        long insert = db.insert(DISEASES_TABLE, null, cv);
        return insert != -1;
    }
}
