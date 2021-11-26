package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssessedImageViewer extends AppCompatActivity {

    ImageView assessedImage;
    Bitmap selectedImage;
    TextView diseaseInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesed_image_viewer);
        assessedImage =findViewById(R.id.assessedImage);
        diseaseInfo = findViewById(R.id.diseaseInfo);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            String fileName = extras.getString("file");
            selectedImage = BitmapFactory.decodeFile(fileName);
            assessedImage.setImageBitmap(selectedImage);
            String textFile = fileName.replace("jpg","_info.txt");
            if (isBuildVersionQ()){
                File file = new File(textFile);
                diseaseInfo.setText(viewInfoQ(file.getName()));
            }else{
                viewInfo(textFile);
            }

        }
    }



    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }

    private String viewInfoQ(String selected){
        if (isBuildVersionQ()){
            Uri textContentUri = MediaStore.Files.getContentUri("external");

            String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

            String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Assessment/"};

            Cursor cursor = getContentResolver().query(textContentUri, null, selection, selectionArgs, null);
            Uri uri = null;
            if (cursor.getCount() == 0){
                Toast.makeText(this, "No Info File Found in \""+Environment.DIRECTORY_DOCUMENTS+"/Assessment/\" " + "DIRECTORY" , Toast.LENGTH_SHORT).show();
            }else {
                while (cursor.moveToNext()) {
                    String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    selected = selected.replace("._info", "_info");
                    if (fileName.equals(selected)){
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                        uri = ContentUris.withAppendedId(textContentUri,id);
                        break;
                    }
                }
                cursor.close();
                if (uri == null) {
                    Toast.makeText(this, selected + " not found", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);

                        int size = inputStream.available();

                        byte[] bytes = new byte[size];

                        inputStream.read(bytes);

                        inputStream.close();

                        return new String(bytes, StandardCharsets.UTF_8);

                    } catch (IOException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                }
            }
        }

        return "no info found";
    }
    private void viewInfo(String textFile) {
        FileReader fr;
        String newFile = textFile.replace("Pictures","Documents");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            String fileContents = stringBuilder.toString();
            fileContents = fileContents.replace(")","");
            fileContents = fileContents.replace("RectF(","Location(Coordinates): ");
            diseaseInfo.append(fileContents);
        }

    }
}