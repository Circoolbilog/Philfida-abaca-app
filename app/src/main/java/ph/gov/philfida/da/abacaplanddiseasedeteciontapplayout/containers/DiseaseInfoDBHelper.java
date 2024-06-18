/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//TODO:Rework database
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class DiseaseInfoDBHelper extends SQLiteOpenHelper {
    public static final String DISEASES_INFO_TABLE = "DISEASES_INFO";
    public static final String COLUMN_DISEASE_NAME = "DiseaseName";
    public static final String COLUMN_DISEASE_DESC = "DiseaseDesc";
    public static final String COLUMN_PICTURE = "Picture";
    public static final String COLUMN_TREATMENT = "Treatment";
    public static final String COLUMN_ID = "ID";
    public static final int DATABASE_VERSION = 4;
    private static final String TAG = "DiseaseInfoDBHelper";
    private Context mContext;

    public DiseaseInfoDBHelper(Context context) {
        super(context, "DiseaseInfo.db", null, DATABASE_VERSION);
        this.mContext = context;
    }

    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the initial tables
        createInitialTables(db);
        createNewTablesAndData(db);
        Log.d(TAG, "onCreate: Database created and tables populated.");

    }

    public void createInitialTables(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + DISEASES_INFO_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DISEASE_NAME + " TEXT, " +
                COLUMN_DISEASE_DESC + " TEXT, " +
                COLUMN_PICTURE + " TEXT, " +
                COLUMN_TREATMENT + " TEXT )";
        db.execSQL(createTableStatement);
        String createTable2 = "CREATE TABLE IF NOT EXISTS Table2 ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "imageFileName TEXT, "
                + "detectedDiseases TEXT, "  // Store as JSON or separate table, depending on complexity
                + "symptoms TEXT, "  // Store as JSON or separate table, depending on complexity
                + "geolocation TEXT, "
                + "groupID TEXT)";
        db.execSQL(createTable2);
    }

    public void createNewTablesAndData(SQLiteDatabase db) {
        // Insert data into DISEASES_INFO table
        db.execSQL("INSERT OR IGNORE INTO " + DISEASES_INFO_TABLE + " (" +
                COLUMN_DISEASE_NAME + ", " + COLUMN_DISEASE_DESC + ", " +
                COLUMN_PICTURE + ", " + COLUMN_TREATMENT + ") VALUES" +
                "('Bract_Mosaic', '" + mContext.getResources().getString(R.string.bract_mosaic_desc) + "', 'https://firebasestorage.googleapis.com/v0/b/abaca-app-1608255969777.appspot.com/o/Diseases%2FBANANA%20BRACT%20MOSAIC%20DISEASE.png?alt=media&token=641c0799-3061-448d-bf87-50ffe0ce7889', 'value')," +
                "('Bunchy_Top', '" + mContext.getResources().getString(R.string.bunchy_top_desc) + "', 'https://firebasestorage.googleapis.com/v0/b/abaca-app-1608255969777.appspot.com/o/Diseases%2FBUNCHY%20TOP%20VIRUS.png?alt=media&token=5d84a775-3cd2-4e93-964f-1778cb6119a5', 'value')," +
                "('CMV', '" + mContext.getResources().getString(R.string.CMV_desc) + "', 'value', 'value')," +
                "('Gen_Mosaic', '" + mContext.getResources().getString(R.string.Gen_mosaic_desc) + "', 'https://firebasestorage.googleapis.com/v0/b/abaca-app-1608255969777.appspot.com/o/Diseases%2FGen_Mosaic.png?alt=media&token=37c03ca2-4fb6-41f5-8dbc-69c8ec61ff15', 'value')," +
                "('SCMV', '" + mContext.getResources().getString(R.string.SCMV_desc) + "', 'https://firebasestorage.googleapis.com/v0/b/abaca-app-1608255969777.appspot.com/o/Diseases%2FSUGARCANE%20MOSAIC%20DISEASE%20IN%20ABACA.png?alt=media&token=b9e82707-e1c2-4d9c-a042-ba96a51ee55a', 'value')");

        // Create Diseases table
        db.execSQL("CREATE TABLE IF NOT EXISTS Diseases (" +
                "grouping_name TEXT PRIMARY KEY," +
                "diseases TEXT)");

        // Insert data into Diseases table
        db.execSQL("INSERT OR IGNORE INTO Diseases (grouping_name, diseases) VALUES" +
                "('0_No_Allocation', 'Yellow_patches_peduncle')," +
                "('Bract_Mosaic', 'Alt_green_yellow_streaks_spindled_chlorosis, Bract_red_brown_mosaic_pattern, ...')," +
                "('Bunchy_Top', 'Brown_leaf_margin, Dark_green_thick_veins, Jhooks, ...')," +
                "('CMV', 'Chlorosis_midrib, Dark_green_leaf_streaks, ...')," +
                "('Gen_Mosaic', 'Alt_green_yellow_streaks_spindled_chlorosis, Bract_red_brown_mosaic_pattern, ...')," +
                "('SCMV', 'Chlorosis_midrib, Dashes_dots_lamina, ...')");
        Log.d(TAG, "createNewTablesAndData: this method ran");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            createNewTablesAndData(db);
        }
    }


    public int getLastGroupID() {
        String queryString = "SELECT * FROM Table2";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int lastGroupID = 0;

        if (cursor.moveToFirst()) {
            lastGroupID = Integer.parseInt(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return lastGroupID;
    }


    public List<DetectedObjectsData> getDetectedObjects() {
        List<DetectedObjectsData> dataObjects = new ArrayList<>();
        String queryString = "SELECT * FROM " + "Table2";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        Log.d(TAG, "getDetectedObjects: database path: " + db.getPath());

        if (cursor.moveToFirst()) {
            do {
                String imageFileName = cursor.getString(1);
                List<String> detectedDiseases = toListString(cursor.getString(2));
                List<String> symptoms = toListString(cursor.getString(3));
                String geoLocation = cursor.getString(4);
                int groupID = cursor.getInt(5);
                DetectedObjectsData detectedObjectsData = new DetectedObjectsData(imageFileName, detectedDiseases, symptoms, geoLocation, groupID);
                dataObjects.add(detectedObjectsData);
            } while (cursor.moveToNext());

        }
        return dataObjects;
    }

    public List<DetectedObjectsData> searchByImageFileName(String imageFileName) {
        List<DetectedObjectsData> resultList = new ArrayList<>();

        // Define the table name for the detected objects
        String tableName = "Table2";

        // Define the columns you want to retrieve
        String[] columns = {"id", "imageFileName", "detectedDiseases", "symptoms", "geolocation", "groupID"};

        // Define the selection criteria
        String selection = "imageFileName = ?";
        String[] selectionArgs = {imageFileName};

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(tableName, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int columnID = cursor.getInt(0); // Assuming "id" is the first column
                String retrievedImageFileName = cursor.getString(1); // Assuming "imageFileName" is the second column
                List<String> detectedDiseases = toListString(cursor.getString(2)); // Assuming "detectedDiseases" is the third column
                List<String> symptoms = toListString(cursor.getString(3)); // Assuming "symptoms" is the fourth column
                String geolocation = cursor.getString(4); // Assuming "geolocation" is the fifth column
                int groupID = cursor.getInt(5); // Assuming "groupID" is the sixth column

                DetectedObjectsData dataObject = new DetectedObjectsData(retrievedImageFileName, detectedDiseases, symptoms, geolocation, groupID);
                resultList.add(dataObject);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return resultList;
    }


    public boolean addDetected(DetectedObjectsData detectedObjectsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("imageFileName", detectedObjectsData.getImageFileName());
        cv.put("detectedDiseases", fromListString(detectedObjectsData.getDetectedDiseases()));
        cv.put("symptoms", fromListString(detectedObjectsData.getSymptoms()));
        cv.put("geolocation", detectedObjectsData.getGeolocation());
        cv.put("groupID", detectedObjectsData.getGroupID());

        long insert = db.insert("Table2", null, cv);
        return insert != -1;
    }

    private static List<String> toListString(String toConvert) {
        return Arrays.asList(toConvert.split(","));
    }

    private static String fromListString(List<String> list) {
        return String.join(",", list);
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

                DiseaseInfoDBModel newDisease = new DiseaseInfoDBModel(diseaseID, diseaseName, diseaseDesc, picture, treatment);
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
