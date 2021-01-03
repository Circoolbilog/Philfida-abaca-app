package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class ImagePreviewActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String fileNum = "fileNumber";
    public int fileNumber = 0;
    String filename;
    Image image;
    ImageView prev;
    Bitmap bitmap,bitmap2;
    OutputStream outputStream;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        prev = findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();

        //import image passed from previous activity
        if (extras != null) {
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            float degrees = 90;
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            bitmap2 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            prev.setImageBitmap(bitmap2);
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
            saveImage2();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"WRITE EXTERNAL STORAGE PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
                saveImage2();
            }else{
                Toast.makeText(this,"WRITE EXTERNAL STORAGE PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void saveImage2() {
        filename = "localImage_" + fileNumber;
        File dir = new File(Environment.getExternalStorageDirectory(),"Assessment");
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,filename);
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap2.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        Toast.makeText(ImagePreviewActivity.this,"Successfuly Saved",Toast.LENGTH_SHORT).show();

        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
            incrementFileNumber();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void saveImage(View view) {
        saveImage2();
    }
}