package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DiseaseInfoDBHelper extends SQLiteOpenHelper {
    public static final String DISEASE_NAME = "DiseaseName";
    public static final String DISEASES_INFO_TABLE = "DiseasesInfo";
    public static final String COLUMN_BRACT_MOSAIC = "Bract_Mosaic";
    public static final String COLUMN_BUNCHY_TOP = "Bunchy_Top";
    public static final String COLUMN_CMV = "CMV";
    public static final String COLUMN_SCMV = "SCMV";
    public static final String COLUMN_GEN_MOSAIC = "Gen_Mosaic";
    public static final String COLUMN_NO_ALLOCATION = "No_Allocation";
    public static final String COLUMN_ID = "ID";
    private static final String TAG = "DiseaseInfoSymptomsDbHe";
    public static String TABLE_NAME;

    public DiseaseInfoDBHelper(Context context) {
        super(context, "DiseaseInfoSymptoms.db", null, 1);
    }

    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + DISEASES_INFO_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NO_ALLOCATION + " TEXT, " +
                COLUMN_BRACT_MOSAIC + " TEXT, " +
                COLUMN_BUNCHY_TOP + " TEXT, " +
                COLUMN_CMV + " TEXT, " +
                COLUMN_GEN_MOSAIC + " TEXT, " +
                COLUMN_SCMV + " TEXT )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = " DELETE FROM " + DISEASES_INFO_TABLE ;

        Cursor cursor = db.rawQuery(queryString, null);
        return cursor.isNull(0);
    }
    public boolean addOneDiseaseInfo(DiseaseInfoDBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID, dbModel.getId());
        //cv.put(SYMPTOM_NAME,dbModel.getSymptomName());
        cv.put(COLUMN_BRACT_MOSAIC, dbModel.getDiseaseName());
        cv.put(COLUMN_BUNCHY_TOP, dbModel.getDiseaseDesc());
        cv.put(COLUMN_CMV, dbModel.getPicture());
        cv.put(COLUMN_GEN_MOSAIC, dbModel.getTreatment());
        cv.put(COLUMN_SCMV, dbModel.getId());

        long insert = db.insert(DISEASES_INFO_TABLE, null, cv);
        return insert != -1;
    }

    public List<DiseaseInfoDBModel> getDiseasesInfo() {
        List<DiseaseInfoDBModel> returnList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + DISEASES_INFO_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            //loop through results, create a new disease object, put it to return list
            do {
                int diseaseID = cursor.getInt(0);
                String diseaseName = cursor.getString(1);
                String diseaseDesc = cursor.getString(2);
                String picture = cursor.getString(3);
                String treatment = cursor.getString(4);

                DiseaseInfoDBModel newDisease = new DiseaseInfoDBModel(diseaseID,diseaseName, diseaseDesc, picture, treatment);
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
}
