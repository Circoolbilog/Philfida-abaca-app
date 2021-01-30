package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
            fileName = fileName.replace(".jpg",".txt");

            FileReader fr = null;
            File diseaseInfoFile  = new File(fileName);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                fr = new FileReader(diseaseInfoFile);
                BufferedReader br = new BufferedReader(fr);
                String line = br.readLine();
                while (line != null){
                    stringBuilder.append(line).append("\n");
                    line = br.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String fileContents = stringBuilder.toString();
                fileContents = fileContents.replace(")","");
                fileContents = fileContents.replace("RectF(","Location(Coordinates): ");
                diseaseInfo.append(fileContents);
            }

        }
    }
}