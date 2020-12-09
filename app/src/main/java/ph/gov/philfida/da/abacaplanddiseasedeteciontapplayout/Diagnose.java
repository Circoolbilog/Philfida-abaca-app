package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.logging.Logger;

import androidx.appcompat.app.AppCompatActivity;

public class Diagnose extends AppCompatActivity
        implements ImageReader.OnImageAvailableListener,
        Camera.PreviewCallback,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final int PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnose);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (hasPermission()) {
            setFragment();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                        Diagnose.this,
                        "Camera permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show();
            }
            requestPermissions(new String[]{PERMISSION_CAMERA}, PERMISSIONS_REQUEST);
        }
    }

    private void setFragment() {
        //TODO set Fragment, Choose camera, use camera2API

    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {

    }

    @Override
    public void onImageAvailable(ImageReader reader) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }
}