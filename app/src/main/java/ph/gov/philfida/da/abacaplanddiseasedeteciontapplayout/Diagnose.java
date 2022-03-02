package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.nio.ByteBuffer;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.DiagnoseModeDialog;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.env.ImageUtils;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.env.Logger;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.AssessmentActivity;

public abstract class Diagnose extends AppCompatActivity
        implements ImageReader.OnImageAvailableListener,
        Camera.PreviewCallback,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        DiagnoseModeDialog.DiagModeListener {
    private static final Logger LOGGER = new Logger();

    RelativeLayout layout;
    private static final int MY_WRITE_PERMISSION_CODE = 102;
    private static final int PERMISSIONS_REQUEST = 1;
    public Handler uiHandler = new Handler();
    DialogFragment diagnoseMode ;
    ImageView captureButton;

    public Image image;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private Handler handler;
    private Handler handler2;
    private HandlerThread handlerThread;
    private HandlerThread handlerThread2;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private final byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private int yRowStride;
    private Runnable postInferenceCallback;
    private Runnable imageConverter;

    protected TextView detectionModeText;
    //    private SwitchCompat apiSwitchCompat;
    private TextView threadsTextView;

    public FusedLocationProviderClient fusedLocationProviderClient;
    static float savedConfidence;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        savedConfidence = ((SettingsContainer) this.getApplication()).getConfidence();
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_diagnose);
        assignedIDs();
        layout= findViewById(R.id.loading);
        runInBackground(checkPerms());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }
    private Runnable checkPerms(){
        if (!hasPermission()) {
            requestPermission();
        }else {
            setFragment();
        }
        return null;
    }
    public void openAssessmentActivity(View view) {
        Intent assessment = new Intent(this, AssessmentActivity.class);
        startActivity(assessment);
    }

    private void assignedIDs() {
        diagnoseMode = new DiagnoseModeDialog();
        captureButton = findViewById(R.id.capture);
        captureButton.setEnabled(false);
    }

    public void createDialog() {
        if (!((SettingsContainer) this.getApplication()).getDiagDialogRemember()) {
            diagnoseMode.show(getSupportFragmentManager(), "Choose Diagnose Mode");
            return;
        }
        if (((SettingsContainer) this.getApplication()).getDiagnoseMode() == 0) {
            setDialogText("DEFAULT: " + "Single Capture Mode");
            layout.setVisibility(View.GONE);
        } else {
            setDialogText("DEFAULT: " + "Dual Capture Mode");
            layout.setVisibility(View.GONE);
        }
    }

    public void setDialogText(String text) {
        detectionModeText.setText(text);
    }

    @Override
    public void onSingleModeClicked(Boolean isRemember) {
        ((SettingsContainer) this.getApplication()).setDiagnoseMode(0);
        ((SettingsContainer) this.getApplication()).setDiagDialogRemember(isRemember);
        setDialogText("Single Capture Mode");
        layout.setVisibility(View.GONE);
    }

    @Override
    public void onDualModeClicked(Boolean isRemember) {
        ((SettingsContainer) this.getApplication()).setDiagnoseMode(1);
        ((SettingsContainer) this.getApplication()).setDiagDialogRemember(isRemember);
        setDialogText("Dual Capture Mode");
        layout.setVisibility(View.GONE);
    }

    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected int getLuminanceStride() {
        return yRowStride;
    }

    protected byte[] getLuminance() {
        return yuvBytes[0];
    }

    /**
     * Callback for android.hardware.Camera API
     */
    @Override
    public void onPreviewFrame(final byte[] bytes, final Camera camera) {
        if (isProcessingFrame) {
            LOGGER.w("Dropping frame!");
            return;
        }

        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                previewHeight = previewSize.height;
                previewWidth = previewSize.width;
                rgbBytes = new int[previewWidth * previewHeight];
                onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
            }
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
            return;
        }

        isProcessingFrame = true;
        yuvBytes[0] = bytes;
        yRowStride = previewWidth;

        imageConverter =
                new Runnable() {
                    @Override
                    public void run() {
                        ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                    }
                };

        postInferenceCallback =
                () -> {
                    camera.addCallbackBuffer(bytes);
                    isProcessingFrame = false;
                };
        processImage();
    }

    /**
     * Callback for Camera2 API
     */
    @Override
    public void onImageAvailable(final ImageReader reader) {

        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return;
        }
        if (rgbBytes == null) {
            rgbBytes = new int[previewWidth * previewHeight];
        }
        try {
            image = reader.acquireLatestImage();
            if (image == null) {
                return;
            }

            if (isProcessingFrame) {
                image.close();
                return;
            }
            isProcessingFrame = true;
            Trace.beginSection("imageAvailable");
            final Image.Plane[] planes = image.getPlanes();
            fillBytes(planes, yuvBytes);
            yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();

            imageConverter =
                    () -> ImageUtils.convertYUV420ToARGB8888(
                            yuvBytes[0],
                            yuvBytes[1],
                            yuvBytes[2],
                            previewWidth,
                            previewHeight,
                            yRowStride,
                            uvRowStride,
                            uvPixelStride,
                            rgbBytes);

            postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            image.close();
                            isProcessingFrame = false;
                        }
                    };

            processImage();
        } catch (final Exception e) {
            LOGGER.e(e, "Exception!");
            Trace.endSection();
            return;
        }
        Trace.endSection();


    }

    @Override
    public synchronized void onStart() {
        LOGGER.d("onStart " + this);
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        LOGGER.d("onResume " + this);
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handlerThread2 = new HandlerThread("inference");
        handlerThread2.start();
        handler = new Handler(handlerThread.getLooper());
        handler2 = new Handler(handlerThread2.getLooper());
    }

    @Override
    public synchronized void onPause() {
        LOGGER.d("onPause " + this);

        handlerThread.quitSafely();
        handlerThread2.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handlerThread2.join();
            handlerThread2 = null;
            handler = null;
            handler2 = null;
        } catch (final InterruptedException e) {
            LOGGER.e(e, "Exception!");
        }

        super.onPause();
    }

    @Override
    public synchronized void onStop() {
        LOGGER.d("onStop " + this);
        super.onStop();
    }

    @Override
    public synchronized void onDestroy() {
        LOGGER.d("onDestroy " + this);
        super.onDestroy();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }
    protected synchronized void runInBackground2(final Runnable r) {
        if (handler2 != null) {
            handler2.post(r);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST ) {
            if (allPermissionsGranted(grantResults)) {
                setFragment();
                Toast.makeText(this, "PERMISSIONS GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
        }
        if (requestCode == MY_WRITE_PERMISSION_CODE){
            if (allPermissionsGranted(grantResults)){
                setFragment();
                Toast.makeText(this, "PERMISSIONS GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                requestPermission();
            }
        }
        super.onRestart();
    }

    private static boolean allPermissionsGranted(final int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission() {
        return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
            Toast.makeText(
                    Diagnose.this,
                    "Camera permission is required for this demo",
                    Toast.LENGTH_LONG)
                    .show();
        }
        requestPermissions(new String[]{PERMISSION_CAMERA,WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST);
    }

    // Returns true if the device supports the required hardware level, or better.
    private boolean isHardwareLevelSupported(
            CameraCharacteristics characteristics) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return false;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return android.hardware.camera2.CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL <= deviceLevel;
    }

    private String chooseCamera() {
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (final String cameraId : manager.getCameraIdList()) {
                final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                final StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                if (map == null) {
                    continue;
                }

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API =
                        (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                                || isHardwareLevelSupported(
                                characteristics);
                LOGGER.i("Camera API lv2?: %s", useCamera2API);
                return cameraId;
            }
        } catch (CameraAccessException e) {
            LOGGER.e(e, "Not allowed to access camera");
        }

        return null;
    }

    protected void setFragment() {
        String cameraId = chooseCamera();

        Fragment fragment;
        if (useCamera2API) {
            CameraConnectionFragment camera2Fragment =
                    CameraConnectionFragment.newInstance(
                            (size, rotation) -> {
                                previewHeight = size.getHeight();
                                previewWidth = size.getWidth();
                                Diagnose.this.onPreviewSizeChosen(size, rotation);
                            },
                            this,
                            getLayoutId(),
                            getDesiredPreviewFrameSize());

            camera2Fragment.setCamera(cameraId);
            fragment = camera2Fragment;
        } else {
            fragment =
                    new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
        }

        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (int i = 0; i < planes.length; ++i) {
            final ByteBuffer buffer = planes[i].getBuffer();
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                yuvBytes[i] = new byte[buffer.capacity()];
            }
            buffer.get(yuvBytes[i]);
        }
    }

    public boolean isDebug() {
        boolean debug = false;
        return debug;
    }

    protected void readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback.run();
        }
    }

    protected int getScreenOrientation() {
        switch (getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_0:
                return 0;
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.plus) {
            String threads = threadsTextView.getText().toString().trim();
            int numThreads = Integer.parseInt(threads);
            if (numThreads >= 9) return;
            numThreads++;
            threadsTextView.setText(String.valueOf(numThreads));
            setNumThreads(numThreads);
        } else if (v.getId() == R.id.minus) {
            String threads = threadsTextView.getText().toString().trim();
            int numThreads = Integer.parseInt(threads);
            if (numThreads == 1) {
                return;
            }
            numThreads--;
            threadsTextView.setText(String.valueOf(numThreads));
            setNumThreads(numThreads);
        }
    }

    protected abstract void processImage();

    protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

    protected abstract int getLayoutId();

    protected abstract Size getDesiredPreviewFrameSize();

    protected abstract void setNumThreads(int numThreads);

    public void CaptureImage(View v) {
    }

}
