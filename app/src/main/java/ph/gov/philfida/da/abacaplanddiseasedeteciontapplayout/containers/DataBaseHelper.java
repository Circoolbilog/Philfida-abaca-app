package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String SYMPTOMS_TABLE = "SYMPTOMS_TABLE";
    public static final String COLUMN_SYMPTOM_NAME = "SymptomName";
    public static final String COLUMN_BRACT_MOSAIC = "Bract_Mosaic";
    public static final String COLUMN_BUNCHY_TOP = "Bunchy_Top";
    public static final String COLUMN_CMV = "CMV";
    public static final String COLUMN_SCMV = "SCMV";
    public static final String COLUMN_GEN_MOSAIC = "Gen_Mosaic";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "Symptoms2.db", null, 1);
    }

    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + SYMPTOMS_TABLE +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_SYMPTOM_NAME + " TEXT, " +
                COLUMN_BRACT_MOSAIC + " BOOL, " +
                COLUMN_BUNCHY_TOP + " BOOL, " +
                COLUMN_CMV + " BOOL, " +
                COLUMN_GEN_MOSAIC + " BOOL, " +
                COLUMN_SCMV + " BOOL )";

        db.execSQL(createTableStatement);

    }

    //runs when version number changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOneSymptom(SymptomModel symptomModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_SYMPTOM_NAME,symptomModel.getSymptomName());
        cv.put(COLUMN_BRACT_MOSAIC,symptomModel.getBract_Mosaic());
        cv.put(COLUMN_BUNCHY_TOP,symptomModel.getBunchy_Top());
        cv.put(COLUMN_CMV,symptomModel.getCMV());
        cv.put(COLUMN_GEN_MOSAIC,symptomModel.getGen_Mosaic());
        cv.put(COLUMN_SCMV,symptomModel.getSCMV());

        long insert = db.insert(SYMPTOMS_TABLE, null, cv);
        return insert != -1;
    }
    public List<SymptomModel> getSymptoms(){
        List<SymptomModel> returnList = new ArrayList<>();

        //get data from db

        String queryString = "SELECT * FROM " + SYMPTOMS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            //loop through results, create a new symptom object, put it to return list
            do {
                int symptomID = cursor.getInt(0);
                String symptom = cursor.getString(1);
                boolean bract = cursor.getInt(2) == 1;
                boolean bunchy = cursor.getInt(3) == 1;
                boolean gen = cursor.getInt(4) == 1;
                boolean cmv = cursor.getInt(5) == 1;
                boolean scmv = cursor.getInt(6) == 1;
                SymptomModel newSymptom = new SymptomModel(symptomID,symptom,bract,bunchy,gen,cmv,scmv);
                returnList.add(newSymptom);
            }while (cursor.moveToNext());
        }

        //close both cursor and db when done
        cursor.close();
        db.close();
        return returnList;
    }

}
