package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DiseaseInfoDBHelper extends SQLiteOpenHelper {
    public static final String DISEASES_INFO_TABLE = "DISEASES_INFO";
    public static final String COLUMN_DISEASE_NAME = "DiseaseName";
    public static final String COLUMN_DISEASE_DESC = "DiseaseDesc";
    public static final String COLUMN_PICTURE = "Picture";
    public static final String COLUMN_TREATMENT = "Treatment";
    public static final String COLUMN_ID = "ID";

    public DiseaseInfoDBHelper(Context context) {
        super(context, "DiseaseInfo.db", null, 1);
    }

    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + DISEASES_INFO_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DISEASE_NAME + " TEXT, " +
                COLUMN_DISEASE_DESC + " TEXT, " +
                COLUMN_PICTURE + " TEXT, " +
                COLUMN_TREATMENT +  " TEXT )";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DROP TABLE IF EXISTS " + DISEASES_INFO_TABLE;
        db.execSQL("DROP TABLE IF EXISTS "+DISEASES_INFO_TABLE);

    }

    public boolean clear(DiseaseInfoDBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = " DELETE FROM " + DISEASES_INFO_TABLE + " WHERE " + COLUMN_ID + " = " + dbModel.getId();

        Cursor cursor = db.rawQuery(queryString,null);
        return cursor.isNull(0);
    }
    public boolean addOneDiseaseInfo(DiseaseInfoDBModel dbModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DISEASE_NAME, dbModel.getDiseaseName());
        cv.put(COLUMN_DISEASE_DESC, dbModel.getDiseaseDesc());
        cv.put(COLUMN_PICTURE, dbModel.getPicture());
        cv.put(COLUMN_TREATMENT, dbModel.getTreatment());;


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
