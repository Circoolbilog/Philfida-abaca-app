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
    public static final String TABLE_BRACT_MOSAIC = "Bract_Mosaic";
    public static final String TABLE_BUNCHY_TOP = "Bunchy_Top";
    public static final String TABLE_CMV = "CMV";
    public static final String TABLE_SCMV = "SCMV";
    public static final String TABLE_GEN_MOSAIC = "Gen_Mosaic";
    public static final String TABLE_NO_ALLOCATION = "No_Allocation";
    public static final String COLUMN_ID = "ID";
    private static final String TAG = "DiseaseInfoSymptomsDbHe";

    public DiseaseInfoSymptomsDbHelper(@Nullable Context context) {
        super(context,"DiseaseInfoSymptoms.db", null, 1);
    }


    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i=0;i<5;i++){
            String table = "CREATE TABLE " + tables(i) +
                    " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SYMPTOM_NAME + " TEXT, " +
                    " TEXT )";
            db.execSQL(table);
            Log.d(TAG, "onCreate: "+ tables(i)+i);
        }
    }

    private String tables(int diseaseID) {
        String TABLE_NAME;
        switch (diseaseID){
            case 0:
                TABLE_NAME = TABLE_NO_ALLOCATION;
                break;
            case 1: TABLE_NAME = TABLE_BRACT_MOSAIC;
                break;
            case 2:
                TABLE_NAME = TABLE_BUNCHY_TOP;
                break;
            case 3:
                TABLE_NAME = TABLE_CMV;
                break;
            case 4:
                TABLE_NAME = TABLE_GEN_MOSAIC;
                break;
            case 5:
                TABLE_NAME = TABLE_SCMV;
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + diseaseID);
        }

        return TABLE_NAME;
    }

    //runs when version number changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public List<DiseaseDBModel> getDiseases() {
        List<DiseaseDBModel> returnList = new ArrayList<>();
        //get data from db

        String queryString = "SELECT * FROM " + TABLE_BRACT_MOSAIC;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //loop through results, create a new disease object, put it to return list
            do {
                int symptomID = cursor.getInt(0);
                String symptom = cursor.getString(1);
                DiseaseDBModel newSymptom = new DiseaseDBModel(symptomID,symptom);
                returnList.add(newSymptom);
            }while (cursor.moveToNext());
        }else {
            //do not add blah blah blah
        }
        //close both cursor and db when done
        cursor.close();
        db.close();
        return returnList;
    }
    public boolean clear(DiseaseDBModel dbModel, int tableID){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = " DELETE FROM " + tables(tableID) + " WHERE " + COLUMN_ID + " = " + dbModel.getId();
        Log.d(TAG, "onCreate: "+ tables(tableID)+tableID);

        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.isNull(0)){
            return true;
        }else {
            return false;
        }
    }
    public boolean addOneSymptom(DiseaseDBModel dbModel,int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID,dbModel.getId());
        cv.put(SYMPTOM_NAME,dbModel.getSymptomName());
        long insert = db.insert(tables(i), null, cv);
        return insert != -1;
    }
}
