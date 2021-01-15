package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class AssessedImageViewer extends AppCompatActivity {

    ImageView assessedImage;
    Bitmap selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesed_image_viewer);
        assessedImage =findViewById(R.id.assessedImage);
        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            selectedImage = BitmapFactory.decodeFile(extras.getString("file"));
            assessedImage.setImageBitmap(selectedImage);
        }
    }
}