package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoSymptomsDbHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

public class ImagePreviewActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String fileNum = "fileNumber";
    public static final String SYMPTOMS_ARRAY = "symptomsArray";
    public int fileNumber = 0;
    int noAllocationScore = 0, bractScore = 0, bunchyScore = 0, cmvScore = 0, genMosaicScore = 0, scmvScore = 0;
    ArrayList<String> no_allocation_list, bract_list, bunchy_list, cmv_list, gen_mosaic_list, scmv_list;

    byte[] bs2;
    ArrayList<String> symptomsDetected;
    String filename, location, detection, confidence, detectionInfo;
    Image image;
    ImageView prev;
    Bitmap bitmap;
    OutputStream outputStream;
    TextView title;
    CardView nextCapture;
    String[] detectedSymptoms;
    String symptomNamesToSave;
    RelativeLayout loading;
    double lat, longt;
    private static final int REQUEST = 112;
    private static final String TAG = "ImagePreviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        assignIDs();
        loadDB();
        importExtras();
        //make sure that there are no duplicate names
        loadFileNumber();
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
        DiseaseInfoSymptomsDbHelper dbHelper = new DiseaseInfoSymptomsDbHelper(this);
        List<DiseaseDBModel> dbModelList = dbHelper.getDiseases();
        for (DiseaseDBModel dbModel : dbModelList) {
            if (!dbModel.getNo_Allocation().equals("NULL") | !dbModel.getNo_Allocation().equals(""))
                no_allocation_list.add(dbModel.getNo_Allocation());
            if (!dbModel.getBract_Mosaic().equals("NULL") | !dbModel.getBract_Mosaic().equals(""))
                bract_list.add(dbModel.getBract_Mosaic());
            if (!dbModel.getBunchy_Top().equals("NULL") | !dbModel.getBunchy_Top().equals(""))
                bunchy_list.add(dbModel.getBunchy_Top());
            if (!dbModel.getCMV().equals("NULL") | !dbModel.getCMV().equals(""))
                cmv_list.add(dbModel.getCMV());
            if (!dbModel.getGen_Mosaic().equals("NULL") | !dbModel.getGen_Mosaic().equals(""))
                gen_mosaic_list.add(dbModel.getGen_Mosaic());
            if (!dbModel.getSCMV().equals("NULL") | !dbModel.getSCMV().equals(""))
                scmv_list.add(dbModel.getSCMV());
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
        title.setText("");
        categorize(symptomsDetected);
        //          confidence = extras.getString("confidence");
    }

    private void categorize(ArrayList<String> symptoms) {
        for (String symptom : no_allocation_list) {
            if (symptoms.contains(symptom)) noAllocationScore++;
        }
        for (String symptom : bract_list) {
            if (symptoms.contains(symptom)) bractScore++;
        }
        for (String symptom : bunchy_list) {
            if (symptoms.contains(symptom)) bunchyScore++;
        }
        for (String symptom : cmv_list) {
            if (symptoms.contains(symptom)) cmvScore++;
        }
        for (String symptom : gen_mosaic_list) {
            if (symptoms.contains(symptom)) genMosaicScore++;
        }
        for (String symptom : scmv_list) {
            if (symptoms.contains(symptom)) scmvScore++;
        }

        if (noAllocationScore !=0 ){
            title.append("(No Allocation) score: " + noAllocationScore + "\n");
        }
        if (bractScore !=0 ){
            title.append("Bract Mosaic score: " + bractScore + "\n");
        }
        if (bunchyScore !=0 ){
            title.append("Bunchy Top score: " + bunchyScore + "\n");
        }
        if (cmvScore !=0 ){
            title.append("CMV score: " + cmvScore + "\n");
        }
        if (genMosaicScore !=0 ){
            title.append("General Mosaic score: " + genMosaicScore + "\n");
        }
        if (scmvScore !=0 ){
            title.append("SCMV score: " + scmvScore + "\n");
        }
        detectionInfo = title.getText().toString() + "\n" + getSymptoms() + "Lognitude: " + longt + "\n"+ "Latitude: "+lat;
    }

    private void loadImage(Bundle extras) {
        //load image from camera
        byte[] bs = extras.getByteArray("byteArray");
        bs2 = extras.getByteArray("backUpImage");
        bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
        prev.setImageBitmap(bitmap);
        loading.setVisibility(View.GONE);
    }

    public void requestPerms(View view) {
        if (ContextCompat.checkSelfPermission(ImagePreviewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImagePreviewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST);
        } else {
            try {
                saveImage(bitmap);
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
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onRequestPermissionsResult: " + e.getMessage());
                }
            } else {
                Toast.makeText(this, "WRITE EXTERNAL STORAGE PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage(Bitmap bitmap3) throws IOException {
        String name = "localImage_" + fileNumber;
        OutputStream fosOne; // image file 1 output stream
        OutputStream fosText; // text file output stream
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures/Assessment");
        if (!dir.exists()) {
            boolean success = dir.mkdir();
            if (success) {
                Toast.makeText(this, "Created Directory", Toast.LENGTH_LONG).show();
            }
        }
        if (isBuildVersionQ()){
            //Save Image File
            Uri imagePathUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            ContentResolver imageOneResolver = getContentResolver();
            ContentValues imageOneCV = new ContentValues();
            imageOneCV.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
            imageOneCV.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            imageOneCV.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Assessment");

            Uri imageUri = imageOneResolver.insert(imagePathUri, imageOneCV);
            fosOne = imageOneResolver.openOutputStream(Objects.requireNonNull(imageUri));

            //Save Text file
            ContentResolver textContentResolver = getContentResolver();
            ContentValues textCV = new ContentValues();

            textCV.put(MediaStore.MediaColumns.DISPLAY_NAME, name + "_info.txt");
            textCV.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            textCV.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Assessment/");

            try {
                Uri textUri = imageOneResolver.insert(imagePathUri,textCV);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "saveImage: " + e.getMessage());
            }
            fosText = textContentResolver.openOutputStream(imageUri);
            fosText.write(detectionInfo.getBytes());
            fosText.close();
        }
        else {
            //below android Q
            //Save Image File
            File imageFileOne = new File(dir, name + "jpg");
            fosOne = new FileOutputStream(imageFileOne);
            //Save Text file
            File textFile = new File(dir, name + "_info.txt");
            fosText = new FileOutputStream(textFile);
            fosText.write(detectionInfo.getBytes());
            fosText.close();
        }
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fosOne);
        Objects.requireNonNull(fosOne).close();

        incrementFileNumber();
        finish();
    }

    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT>Build.VERSION_CODES.Q;
    }


    public void incrementFileNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        fileNumber += 1;
        editor.putInt(fileNum, fileNumber);
        editor.apply();
    }

    public void loadFileNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        fileNumber = sharedPreferences.getInt(fileNum, 0);
    }


    public void discardImage(View view) {
        finish();
    }

    public void captureNextImage(View view) {
        Toast.makeText(this, "Feature Under Development", Toast.LENGTH_SHORT).show();
        //save symptoms to an array
        finish();
    }

    public void showMoreInfo(View view) {
        Intent intent = new Intent(ImagePreviewActivity.this,MoreInfo.class);
        intent.putExtra("Symptoms",getSymptoms());
        intent.putExtra("lat",lat);
        intent.putExtra("longt" , longt);
        intent.putExtra("imageWithBox",bs2);
        startActivity(intent);
    }

    public String getSymptoms() {
        String s = "Symptoms: \n";
        if (symptomsDetected == null) return s;
        StringBuilder sBuilder = new StringBuilder("Symptoms: \n");
        for (String symptom: symptomsDetected){
            sBuilder.append(symptom).append("\n");
        }
        s = sBuilder.toString();
        return s;
    }

}