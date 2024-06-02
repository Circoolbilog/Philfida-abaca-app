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
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;
//TODO: Rework Database

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.DiseaseInfo;

public class DiseaseSymptomsDbHelper extends SQLiteOpenHelper {

    public static final String DISEASES_TABLE = "Diseases";
    public static final String COLUMN_BRACT_MOSAIC = "Bract_Mosaic";
    public static final String COLUMN_BUNCHY_TOP = "Bunchy_Top";
    public static final String COLUMN_CMV = "CMV";
    public static final String COLUMN_SCMV = "SCMV";
    public static final String COLUMN_GEN_MOSAIC = "Gen_Mosaic";
    public static final String COLUMN_NO_ALLOCATION = "No_Allocation";
    public static final String COLUMN_ID = "ID";
    private static final String TAG = "DiseaseSymptomsDbHelper";

    private Context mContext;

    public DiseaseSymptomsDbHelper(@Nullable Context context) {
        super(context, "DiseaseInfoSymptoms.db", null, 5);
        mContext=context;
    }


    //firstTime db access
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + DISEASES_TABLE +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY, " +
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
        if(oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DISEASES_TABLE);
            onCreate(db);

            // Read JSON data
            Map<String, List<String>> symptomsMap = readSymptomsFromJson(mContext);

            // Create the table
            String createTableStatement = "CREATE TABLE IF NOT EXISTS " + DISEASES_TABLE +
                    "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NO_ALLOCATION + " TEXT, " +
                    COLUMN_BRACT_MOSAIC + " TEXT, " +
                    COLUMN_BUNCHY_TOP + " TEXT, " +
                    COLUMN_CMV + " TEXT, " +
                    COLUMN_GEN_MOSAIC + " TEXT, " +
                    COLUMN_SCMV + " TEXT )";
            db.execSQL(createTableStatement);

            // Insert data from JSON File
            for (Map.Entry<String, List<String>> entry : symptomsMap.entrySet()) {
                String column = entry.getKey();
                List<String> symptoms = entry.getValue();
                for (String symptom : symptoms){
                    ContentValues cv = new ContentValues();
                    cv.put(column, symptom);
                    db.insert(DISEASES_TABLE, null, cv);
                    Log.d(TAG, "onUpgrade: inserted: " + symptom);
                }
            }
        }

    }



    public Map<String, List<String>> readSymptomsFromJson(Context context) {
        Map<String, List<String>> symptomsMap = new HashMap<>();
        String json = null;

        try {
            InputStream is = context.getAssets().open("symptom_map.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);

            // Parse the JSON data into the Map
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                JSONArray jsonArray = jsonObject.getJSONArray(key);
                List<String> symptomsList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    symptomsList.add(jsonArray.getString(i));
                }
                symptomsMap.put(key, symptomsList);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return symptomsMap;
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

}
