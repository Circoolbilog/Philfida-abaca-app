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
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseDBModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseSymptomsDbHelper;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

public class ImagePreviewActivity extends AppCompatActivity {
    public String timestamp;
    int noAllocationScore = 0, bractScore = 0, bunchyScore = 0, cmvScore = 0, genMosaicScore = 0, scmvScore = 0;
    ArrayList<String> no_allocation_list, bract_list, bunchy_list, cmv_list, gen_mosaic_list, scmv_list;

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

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        assignIDs();
        loadDB();
        importExtras();
        //make sure that there are no duplicate names
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
        categorize(symptomsDetected);
    }

    private void categorize(ArrayList<String> symptoms) {
        //Sort from highest to lowest,
        //format the highest to be <h3>
        //convert scores to percentage.  (number of detections corresponding to this disease(score)/number of total detections) * 100
        //add text on top, describing the meaning of the percentage ("likelihood of the detected symptoms to be the disease not the likelihood of the plant having the disease")
        for (String symptom : symptoms) {
            if (no_allocation_list.contains(symptom)) noAllocationScore++;
            if (bract_list.contains(symptom)) bractScore++;
            if (bunchy_list.contains(symptom)) bunchyScore++;
            if (cmv_list.contains(symptom)) cmvScore++;
            if (gen_mosaic_list.contains(symptom)) genMosaicScore++;
            if (scmv_list.contains(symptom)) scmvScore++;
        }
        String symptomScores = "";
        if (noAllocationScore != 0)
            symptomScores = symptomScores.concat("(No Allocation) score: " + noAllocationScore + "<br>");
        if (bractScore != 0) symptomScores = symptomScores.concat("Bract Mosaic score: " + bractScore + "<br>");
        if (bunchyScore != 0) symptomScores = symptomScores.concat("Bunchy Top score: " + bunchyScore + "<br>");
        if (cmvScore != 0) symptomScores = symptomScores.concat("CMV score: " + cmvScore + "<br>");
        if (genMosaicScore != 0) symptomScores = symptomScores.concat("General Mosaic score: " + genMosaicScore + "<br>");
        if (scmvScore != 0) symptomScores = symptomScores.concat("SCMV score: " + scmvScore + "<br>");
        detectionInfo = "<h2><b>" + symptomScores + "</b></h2>"
                + "<br><h4>"+getSymptoms() + "</h4>"+ "<br>"+"Longitude: " + longt + "<br>" + "Latitude: " + lat;
        title.setText(Html.fromHtml(symptomScores));
        title.setText(HtmlCompat.fromHtml(symptomScores,HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    private void loadImage(Bundle extras) {
        //load image from camera
        byte[] bs = extras.getByteArray("byteArray");
        bs2 = extras.getByteArray("backUpImage");
        bitmap = BitmapFactory.decodeByteArray(bs, 0, bs.length);
        boxedBitmap = BitmapFactory.decodeByteArray(bs2,0, bs2.length);
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
    private void saveBoxedImage(Bitmap bitmap){
        String name = "localBoxed_" + timestamp;
        OutputStream fosOne = null; // image file 1 output stream
        File dir = new File(Environment.getExternalStorageDirectory(), "Pictures/Assessment/boxed");
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
            imageOneCV.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Assessment/boxed/");

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
        String name = "localImage_" + timestamp;
        OutputStream fosOne; // image file 1 output stream
        OutputStream fosText; // text file output stream
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
            Uri textPathUri = MediaStore.Files.getContentUri("external");
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
            textCV.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Assessment/");

            Uri textUri = imageOneResolver.insert(textPathUri, textCV);
            fosText = textContentResolver.openOutputStream(textUri);
        } else {
            //below android Q
            //Save Image File
            File imageFileOne = new File(dir, name + "jpg");
            fosOne = new FileOutputStream(imageFileOne);
            //Save Text file
            File textFile = new File(textDir, name + "_info.txt");
            fosText = new FileOutputStream(textFile);
        }
        fosText.write(detectionInfo.getBytes());
        fosText.close();
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fosOne);
        Objects.requireNonNull(fosOne).close();

        finish();
    }

    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
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
        Intent intent = new Intent(ImagePreviewActivity.this, MoreInfo.class);
        intent.putExtra("Symptoms", getSymptoms());
        intent.putExtra("lat", lat);
        intent.putExtra("longt", longt);
        intent.putExtra("imageWithBox", bs2);
        startActivity(intent);
    }

    public String getSymptoms() {
        String s = "Symptoms: \n";
        if (symptomsDetected == null) return s;
        StringBuilder sBuilder = new StringBuilder("Symptoms: \n");
        for (String symptom : symptomsDetected) {
            sBuilder.append(symptom).append("\n");
        }
        s = sBuilder.toString();
        return s;
    }

}