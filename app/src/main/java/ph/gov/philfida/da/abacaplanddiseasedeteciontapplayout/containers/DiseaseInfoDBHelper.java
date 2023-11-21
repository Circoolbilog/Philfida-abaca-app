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

import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiseaseInfoDBHelper extends SQLiteOpenHelper {
    public static final String DISEASES_INFO_TABLE = "DISEASES_INFO";
    public static final String COLUMN_DISEASE_NAME = "DiseaseName";
    public static final String COLUMN_DISEASE_DESC = "DiseaseDesc";
    public static final String COLUMN_PICTURE = "Picture";
    public static final String COLUMN_TREATMENT = "Treatment";
    public static final String COLUMN_ID = "ID";
    public static final int DATABASE_VERSION = 2;

    public DiseaseInfoDBHelper(Context context) {
        super(context, "DiseaseInfo.db", null, DATABASE_VERSION);
    }

    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE IF NOT EXISTS " + DISEASES_INFO_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DISEASE_NAME + " TEXT, " +
                COLUMN_DISEASE_DESC + " TEXT, " +
                COLUMN_PICTURE + " TEXT, " +
                COLUMN_TREATMENT +  " TEXT )";
// Create Table2
        String createTable2 = "CREATE TABLE IF NOT EXISTS Table2 ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "imageFileName TEXT, "
                + "detectedDiseases TEXT, "  // Store as JSON or separate table, depending on complexity
                + "symptoms TEXT, "  // Store as JSON or separate table, depending on complexity
                + "geolocation TEXT, "
                + "groupID TEXT)";
        db.execSQL(createTable2);
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create Table2
            String createTable2 = "CREATE TABLE IF NOT EXISTS Table2 ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "imageFileName TEXT, "
                    + "detectedDiseases TEXT, "
                    + "symptoms TEXT, "
                    + "geolocation TEXT, "
                    + "groupID TEXT)";
            db.execSQL(createTable2);
        }
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

    public int getLastGroupID(){
        String queryString = "SELECT MAX(groupID) FROM Table2";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        int lastGroupID = 0;
        if (cursor.moveToFirst()) {
            lastGroupID = Integer.parseInt(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return lastGroupID;
    }


    public List<DetectedObjectsData> getDetectedObjects(){
        List<DetectedObjectsData> dataObjects = new ArrayList<>();
        String queryString = "SELECT * FROM " + "Table2";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if (cursor.moveToFirst()){
            do{
                String imageFileName = cursor.getString(1);
                List<String> detectedDiseases = toListString(cursor.getString(2));
                List<String> symptoms = toListString(cursor.getString(3));
                String geoLocation = cursor.getString(4);
                int groupID = cursor.getInt(5);
                DetectedObjectsData detectedObjectsData = new DetectedObjectsData(imageFileName,detectedDiseases,symptoms,geoLocation,groupID);
                dataObjects.add(detectedObjectsData);
            }while(cursor.moveToNext());

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


    public boolean addDetected(DetectedObjectsData detectedObjectsData){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("imageFileName", detectedObjectsData.getImageFileName());
        cv.put("detectedDiseases", fromListString(detectedObjectsData.getDetectedDiseases()));
        cv.put("symptoms", fromListString(detectedObjectsData.getSymptoms()));
        cv.put("geolocation",detectedObjectsData.getGeolocation());
        cv.put("groupID",detectedObjectsData.getGroupID());

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
