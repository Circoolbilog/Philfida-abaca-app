package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

public class ImagePreviewActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String fileNum = "fileNumber";
    public static final String SYMPTOMS_ARRAY = "symptomsArray";
    public int fileNumber = 0;
    String filename, location, detection, confidence,detectionInfo;
    Image image;
    ImageView prev;
    Bitmap bitmap,bitmap2;
    OutputStream outputStream;
    TextView title;
    CardView nextCapture;
    String[] detectedSymptoms;
    String symptomNamesToSave;
    private static final int REQUEST = 112;
    private static final String TAG = "ImagePreviewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        prev = findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        title = findViewById(R.id.diseasePredicion);
        nextCapture = findViewById(R.id.captureNextImage);

        //import image passed from previous activity
        if (extras != null) {
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            float degrees = 90;
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            bitmap2 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            prev.setImageBitmap(bitmap2);
            detection  = extras.getString("diseaseName");
  //          confidence = extras.getString("confidence");
            location = extras.getString("location");
            title.setText("");
            //title.append(detection + " "+ confidence);
            detectionInfo = detection + " " + confidence + "\n" + location;
            detectedSymptoms = extras.getStringArray("diseaseNameArray");
            Log.d(TAG, "onCreate: detected: " + Arrays.toString(detectedSymptoms));
            try {
                StringBuilder sb = new StringBuilder();
                for(String names: detectedSymptoms){
                    sb.append(names);
                    title.append("Symptoms: ");
                    title.append(names);
                    title.append("\n");
                }
                symptomNamesToSave = String.valueOf(sb);
            }catch (Exception e){
                Toast.makeText(this, "eception "+ e, Toast.LENGTH_LONG).show();
            }
        }
        if( ((SettingsContainer) this.getApplication()).getDiagnoseMode() != 0){
            nextCapture.setVisibility(View.VISIBLE);
        }
        //make sure that there are no duplicate names
        loadFileNumber();
    }

    public void requestPerms(View view){
        if (ContextCompat.checkSelfPermission(ImagePreviewActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ImagePreviewActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST);
        }
        else{
            //saveImage2();
            try {
                saveImage3(bitmap2,filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"WRITE EXTERNAL STORAGE PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
                //saveImage2();
                try {
                    saveImage3(bitmap2,filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this,"WRITE EXTERNAL STORAGE PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage3(Bitmap bitmap3, String name) throws IOException {
        name = "localImage_" + fileNumber;
        OutputStream fos; // file output stream
        File dir = new File(Environment.getExternalStorageDirectory(),"Pictures/Assessment");
        if (!dir.exists()){
            boolean success = dir.mkdir();
            if (success){
                Toast.makeText(this,"Created Directory",Toast.LENGTH_LONG).show();
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentResolver resolver =getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpg");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+"/Assessment");
            Uri uri;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri imageUri = resolver.insert(uri,contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
        }else{
            //below android Q
            File imageFile =  new File(dir,name + "jpg");
            fos = new FileOutputStream(imageFile);
        }
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        Objects.requireNonNull(fos).close();
        File textFile = new File(dir, name + ".txt");
        fos=new FileOutputStream(textFile);
        fos.write(detectionInfo.getBytes());
        incrementFileNumber();
        finish();
    }


    public void setSymptomsArray(){
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor spE = sp.edit();
        spE.putString(SYMPTOMS_ARRAY,symptomNamesToSave);
        spE.apply();
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
        finish();
    }
}