package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

import android.content.Context;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AppCompatActivity;
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
    Button saveImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        prev = findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        saveImage = findViewById(R.id.save);

        //import image passed from previous activity
        if (extras != null) {
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            float degrees = 90;
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            bitmap2 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            prev.setImageBitmap(bitmap2);
            saveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAssessedImage(bitmap2);
                }
            });
        }
        //make sure that there are no duplicate names
        loadFileNumber();
    }
    public void saveAssessedImage(Bitmap bitmapX){
        String fileName = "localImage_" + fileNumber;
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File outFile = new File(extStorageDirectory,fileName);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmapX.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            incrementFileNumber();
            // remember close file output
            fo.close();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
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
}