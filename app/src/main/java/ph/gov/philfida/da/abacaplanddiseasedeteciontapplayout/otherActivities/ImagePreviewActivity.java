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
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DetectedObjectsData;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

public class ImagePreviewActivity extends AppCompatActivity {
    public String timestamp;
    int noAllocationScore = 0, bractScore = 0, bunchyScore = 0, cmvScore = 0, genMosaicScore = 0, scmvScore = 0;
    ArrayList<String> no_allocation_list, bract_list, bunchy_list, cmv_list, gen_mosaic_list, scmv_list;
    ArrayList<String> detectedDiseasesWScores = new ArrayList<>();

    byte[] bs2;
    ArrayList<String> symptomsDetected;
    String detectionInfo;
    ImageView prev;
    Bitmap bitmap, boxedBitmap;
    TextView title;
    CardView nextCapture;
    RelativeLayout loading;
    double lat, longt;
    private static final int REQUEST = 112;
    private static final String TAG = "ImagePreviewActivity";
    int groupID = 0;
    boolean increment = true;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        assignIDs();
        loadDB();
        importExtras();
        getLastGroupID();
        //make sure that there are no duplicate names
    }

    private void getLastGroupID() {
        DiseaseInfoDBHelper infoDBHelper = new DiseaseInfoDBHelper(this);
        groupID = infoDBHelper.getLastGroupID();
    }

    private void assignIDs() {
        prev = findViewById(R.id.imageView);
        loading = findViewById(R.id.loading);
        title = findViewById(R.id.diseasePredicion);
        nextCapture = findViewById(R.id.captureNextImage);
    }

    private void loadDB() {
        no_allocation_list = new ArrayList<>();
        bract_list = new ArrayList<>();
        bunchy_list = new ArrayList<>();
        cmv_list = new ArrayList<>();
        gen_mosaic_list = new ArrayList<>();
        scmv_list = new ArrayList<>();
        DiseaseSymptomsDbHelper dbHelper = new DiseaseSymptomsDbHelper(this);
        List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
        for (DiseaseDBModel dbModel : dbModelList) {
            addIfValid(no_allocation_list, dbModel.getNo_Allocation());
            addIfValid(bract_list, dbModel.getBract_Mosaic());
            addIfValid(bunchy_list, dbModel.getBunchy_Top());
            addIfValid(cmv_list, dbModel.getCMV());
            addIfValid(gen_mosaic_list, dbModel.getGen_Mosaic());
            addIfValid(scmv_list, dbModel.getSCMV());
        }
    }

    public void addIfValid(List<String> list, String value) {
        if (!"NULL".equals(value) && !value.isEmpty()) {
            list.add(value);
        }
    }

    private void importExtras() {
        Bundle extras = getIntent().getExtras();
        //import image passed from previous activity
        if (extras != null) {
            try {
                loadImage(extras);
            } catch (Exception e) {
                Log.d(TAG, "importExtras: " + e.getMessage());
            }
            //get detected symptoms and disease name
            loadDetectionInfo(extras);
        }
        if (((SettingsContainer) this.getApplication()).getDiagnoseMode() != 0) {
            nextCapture.setVisibility(View.VISIBLE);
        }
    }

    private void loadDetectionInfo(Bundle extras) {
        symptomsDetected = extras.getStringArrayList("symptomsDetected");
        lat = extras.getDouble("lat");
        longt = extras.getDouble("longt");
        categorize(symptomsDetected);
    }

    private void categorize(ArrayList<String> symptoms) {
        //Sort from highest to lowest,
        //format the highest to be <h3>
        //convert scores to percentage.  (number of detections corresponding to this disease(score)/number of total detections) * 100
        //add text on top, describing the meaning of the percentage ("likelihood of the detected symptoms to be the disease not the likelihood of the plant having the disease")
        List<ScoredDiseases> sortedDiseases;
        sortedDiseases = new ArrayList<>();
        for (String symptom : symptoms) {
            if (no_allocation_list.contains(symptom)) noAllocationScore++;
            if (bract_list.contains(symptom)) bractScore++;
            if (bunchy_list.contains(symptom)) bunchyScore++;
            if (cmv_list.contains(symptom)) cmvScore++;
            if (gen_mosaic_list.contains(symptom)) genMosaicScore++;
            if (scmv_list.contains(symptom)) scmvScore++;
        }

        List<ScoredDiseases> scoredDiseases;
        scoredDiseases = new ArrayList<>();
        if (!symptoms.isEmpty()) {
            scoredDiseases.add(new ScoredDiseases("No Allocation", noAllocationScore));
            scoredDiseases.add(new ScoredDiseases("Bract Mosaic", bractScore));
            scoredDiseases.add(new ScoredDiseases("Bunchy Top", bunchyScore));
            scoredDiseases.add(new ScoredDiseases("CMV", cmvScore));
            scoredDiseases.add(new ScoredDiseases("General Mosaic", genMosaicScore));
            scoredDiseases.add(new ScoredDiseases("SCMV", scmvScore));
        }

        for (ScoredDiseases disease : scoredDiseases) {
            if (disease.getScore() != 0) sortedDiseases.add(disease);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sortedDiseases.sort((t0, t1) -> t1.getScore() - t0.getScore());
        }

        StringBuilder symptomScores = new StringBuilder();
        int index = 0;
        for (ScoredDiseases disease : sortedDiseases) {
            disease.setScore((int) (((float) disease.getScore() / ((float) symptoms.size() * (float) sortedDiseases.size())) * 100));
            detectedDiseasesWScores.add(disease.toString());
            if (index == 0) {
                symptomScores = new StringBuilder(("<h2><b>" + disease.getName() + " : " + disease.getScore() + "%" + "</b></h2>"));
            } else {
                symptomScores.append("<p>").append(disease.getName()).append(" : ").append(disease.getScore()).append("%").append("<br>");
            }
            index++;
        }
        symptomScores.append("<small><sub><i>*the percentage shown is the likelihood of the disease relative to the symptoms detected</i></sub></small></p>");

        detectionInfo = symptomScores + "<br><p><i>" + getSymptoms() + "</i></p><br>" + "Longitude: " + longt + "<br>" + "Latitude: " + lat;
        title.setText(Html.fromHtml(symptomScores.toString()));
    }

    private static class ScoredDiseases {
        int score;
        String name;

        @NonNull
        @Override
        public String toString() {
            return name + ": " + score + "%" + '\'';
        }

        ScoredDiseases(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private void loadImage(Bundle extras) {
        //load image from camera
        byte[] bs = extras.getByteArray("byteArray");
        bs2 = extras.getByteArray("backUpImage");
        bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
        boxedBitmap = BitmapFactory.decodeByteArray(bs2, 0, bs2.length);
        prev.setImageBitmap(bitmap);
        loading.setVisibility(View.GONE);
    }

    public void requestPerms(View view) {

        if (ContextCompat.checkSelfPermission(ImagePreviewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImagePreviewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST);
        } else {
            if (view.getId() == R.id.captureNextImage) {
                Toast.makeText(this, "button clicked: " + view, Toast.LENGTH_SHORT).show();
                increment = false;
            }
            try {
                saveImage(bitmap);
                saveBoxedImage(boxedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "requestPerms: " + e.getMessage());
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "WRITE EXTERNAL STORAGE PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                //saveImage2();
                try {
                    saveImage(bitmap);
                    saveBoxedImage(boxedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onRequestPermissionsResult: " + e.getMessage());
                }
            } else {
                Toast.makeText(this, "WRITE EXTERNAL STORAGE PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveBoxedImage(Bitmap bitmap) {
        String name = "localBoxed_" + timestamp;
        OutputStream fosOne = null; // image file 1 output stream
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures/AssessmentBoxed");
        if (!dir.exists()) {
            if (dir.mkdirs())
                Toast.makeText(this, "Pictures Directory Created", Toast.LENGTH_SHORT).show();
        }
        if (isBuildVersionQ()) {
            //Save Image File
            Uri imagePathUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver imageOneResolver = getContentResolver();
            ContentValues imageOneCV = new ContentValues();
            imageOneCV.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
            imageOneCV.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageOneCV.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AssessmentBoxed/");

            Uri imageUri = imageOneResolver.insert(imagePathUri, imageOneCV);
            try {
                fosOne = imageOneResolver.openOutputStream(Objects.requireNonNull(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            //below android Q
            //Save Image File
            File imageFileOne = new File(dir, name + "jpg");
            try {
                fosOne = new FileOutputStream(imageFileOne);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosOne);
        try {
            Objects.requireNonNull(fosOne).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    private void saveImage(Bitmap bitmap3) throws IOException {
        DiseaseInfoDBHelper dbHelper = new DiseaseInfoDBHelper(this);

        String name = "localImage_" + timestamp;
        OutputStream fosOne; // image file 1 output stream
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures/Assessment");
        File textDir = new File(Environment.getExternalStorageDirectory(), "Documents/Assessment");
        if (!dir.exists() || !textDir.exists()) {
            if (dir.mkdirs())
                Toast.makeText(this, "Pictures Directory Created", Toast.LENGTH_SHORT).show();
            if (textDir.mkdirs())
                Toast.makeText(this, "Info/Documents Directory Created", Toast.LENGTH_SHORT).show();
        }
        if (isBuildVersionQ()) {
            //Save Image File
            Uri imagePathUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver imageOneResolver = getContentResolver();
            ContentValues imageOneCV = new ContentValues();
            imageOneCV.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
            imageOneCV.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageOneCV.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Assessment");

            Uri imageUri = imageOneResolver.insert(imagePathUri, imageOneCV);
            fosOne = imageOneResolver.openOutputStream(Objects.requireNonNull(imageUri));

        } else {
            //below android Q
            //Save Image File
            File imageFileOne = new File(dir, name + "jpg");
            fosOne = new FileOutputStream(imageFileOne);
        }
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fosOne);
        Objects.requireNonNull(fosOne).close();

        finish();

        //Save to database
        if (detectedDiseasesWScores != null) {
            String filename = name + ".jpg";
            List<String> detectedDiseases = detectedDiseasesWScores;
            List<String> detectedSymptoms = symptomsDetected;
            String geolocation = lat + "," + longt;
            if(!increment) groupID++;
            DetectedObjectsData detectedObjectsData = new DetectedObjectsData(filename, detectedDiseases, detectedSymptoms, geolocation, groupID);
            boolean success = dbHelper.addDetected(detectedObjectsData);
            if (!success)
                Toast.makeText(getApplicationContext(), "try Failed to update local database", Toast.LENGTH_LONG).show();
        } else {
            String filename = name + ".jpg";
            List<String> detectedDiseases = new ArrayList<>();
            detectedDiseases.add("");
            List<String> detectedSymptoms = new ArrayList<>();
            detectedSymptoms.add("");
            String geolocation = lat + "," + longt;
            groupID++;
            DetectedObjectsData detectedObjectsData = new DetectedObjectsData(filename, detectedDiseases, detectedSymptoms, geolocation, groupID);
            boolean success = dbHelper.addDetected(detectedObjectsData);
            if (!success)
                Toast.makeText(getApplicationContext(), "try Failed to update local database", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }

    public void discardImage(View view) {
        finish();
    }

    public void showMoreInfo(View view) {
        Intent intent = new Intent(ImagePreviewActivity.this, MoreInfo.class);
        intent.putExtra("Symptoms", getSymptoms());
        intent.putExtra("lat", lat);
        intent.putExtra("longt", longt);
        intent.putExtra("imageWithBox", bs2);
        startActivity(intent);
    }

    public String getSymptoms() {
        String s = "Symptoms: <br>";
        if (symptomsDetected == null) return s;
        StringBuilder sBuilder = new StringBuilder("Symptoms: \n");
        for (String symptom : symptomsDetected) {
            sBuilder.append(symptom).append("<br>");
        }
        s = sBuilder.toString();
        return s;
    }
}