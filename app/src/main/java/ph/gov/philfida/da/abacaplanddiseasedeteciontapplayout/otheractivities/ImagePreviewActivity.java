package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePreviewActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String fileNum = "fileNumber";
    public int fileNumber = 0;
    String filename;
    Image image;
    ImageView prev;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        prev = findViewById(R.id.imageView);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            prev.setImageBitmap(bitmap);
        }
        loadFileNumber();
    }

    public void incrementFileNumber(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        fileNumber += 1;
        editor.putInt(fileNum,fileNumber);
        editor.apply();
    }
    public void loadFileNumber(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        fileNumber = sharedPreferences.getInt(fileNum,0);
    }
    public void saveImage(View view) {
        filename = "localImage_"+fileNumber;
        try (FileOutputStream out = new FileOutputStream(filename)) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        // PNG is a lossless format, the compression factor (100) is ignored
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    public void discardImage(View view) {
        finish();
    }
}