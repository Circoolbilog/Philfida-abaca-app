package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.ImagesGallery;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.GalleryAdapter;

public class AssessmentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    TextView gallery_number;

    private static final int MY_READ_PERMISSION_CODE = 101;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesment);
        gallery_number = findViewById(R.id.gallery_number);
        recyclerView = findViewById(R.id.recyclerView_gallery_images);
        //Check for permission
        if (ContextCompat.checkSelfPermission(AssessmentActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AssessmentActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);
        } else {
            loadImages();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "READ EXTERNAL STORAGE PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
                loadImages();
            } else {
                Toast.makeText(this, "READ EXTERNAL STORAGE PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            images = ImagesGallery.listOfImages(this);
        }
        galleryAdapter = new GalleryAdapter(this, images, path -> {
            Intent intent = new Intent(AssessmentActivity.this, AssessedImageViewer.class);
            intent.putExtra("file", path);
            startActivity(intent);
            //open photo
        });
        recyclerView.setAdapter(galleryAdapter);
        gallery_number.setText("Photos (" + images.size() + ")");
    }
}