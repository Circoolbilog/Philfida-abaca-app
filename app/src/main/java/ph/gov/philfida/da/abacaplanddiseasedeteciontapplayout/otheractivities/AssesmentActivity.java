package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otheractivities;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AssesmentActivity extends AppCompatActivity {
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
        if (ContextCompat.checkSelfPermission(AssesmentActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AssesmentActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MY_READ_PERMISSION_CODE);
        }
        else{
            loadImages();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_READ_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"READ EXTERNAL STORAGE PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
                loadImages();
            }else{
                Toast.makeText(this,"READ EXTERNAL STORAGE PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadImages() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        images = ImagesGallery.listOfImages(this);
        galleryAdapter = new GalleryAdapter(this, images, new GalleryAdapter.PhotoListener() {
            @Override
            public void onPhotoClick(String path) {
                Toast.makeText(AssesmentActivity.this,"asd" + path,Toast.LENGTH_SHORT).show();
                //open photo
            }
        });
        recyclerView.setAdapter(galleryAdapter);
        gallery_number.setText("Photos ("+ images.size()+")");
    }
}