/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.customview.OverlayView;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.env.BorderedText;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.env.ImageUtils;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.env.Logger;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities.ImagePreviewActivity;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.tflite.Classifier;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.tflite.TFLiteObjectDetectionAPIModel;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.tracking.MultiBoxTracker;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class DetectorActivity extends Diagnose implements OnImageAvailableListener {
    static long startTime;
    static long elapsedTime;

    private boolean initialized = false;
    private static final String TAG = "DetectorActivity";
    private static final Logger LOGGER = new Logger();
    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 640;
    private static final boolean TF_OD_API_IS_QUANTIZED = false;
    private static final String TF_OD_API_MODEL_FILE = "hpc_model2.tflite";
    private static final String TF_OD_API_LABELS_FILE = "symptoms.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.7f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 640);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 500;
    OverlayView trackingOverlay;

    private Classifier detector;

//    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    ArrayList<String> detectedSymptomsList = new ArrayList<>();
    List<Float> confidenceList = new ArrayList<>();
    int lastDetection;
    String[] names;
    private double lat, longt;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        BorderedText borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        int sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                canvas -> {
                    tracker.draw(canvas);
                    if (isDebug()) {
                        tracker.drawDebug(canvas);
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                () -> {
                    final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                    cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                    final Canvas canvas1 = new Canvas(cropCopyBitmap);
                    final Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Style.STROKE);
                    paint.setStrokeWidth(3.0f);
                    float minimumConfidence = 0.0F;
                    if (MODE == DetectorMode.TF_OD_API) minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;

                    final List<Classifier.Recognition> mappedRecognitions =
                            new LinkedList<>();

                    detectedSymptomsList.clear();
                    for (final Classifier.Recognition result : results) {

                        final RectF location = result.getLocation();
                        if (location != null && result.getConfidence() >= minimumConfidence) {
                            canvas1.drawRect(location, paint);
                            cropToFrameTransform.mapRect(location);
                            detectedSymptomsList.add(result.getTitle());
                            result.setLocation(location);
                            mappedRecognitions.add(result);
                        }
                    }

                    try {
                        names = detectedSymptomsList.toArray(new String[0]);
                    } catch (Exception e) {
                        Log.d(TAG, "run: " + e.getMessage());
                    }
                    tracker.trackResults(mappedRecognitions, currTimestamp);
                    trackingOverlay.postInvalidate();
                    computingDetection = false;
                    if (!initialized) {
                        initialize();
                        initialized = true;
                    }
                });
    }

    private void initialize() {
        detectionModeText = findViewById(R.id.detectionModeText);

        uiHandler.post(() -> {
            captureButton.setEnabled(true);
            createDialog();

        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }
//TODO: Add 2 part capture to enable detection of stunting
    //add settings to enable single capture mode

    @Override
    public void CaptureImage(View v) {
        super.CaptureImage(v);
        runInBackground2(() -> {
            try {
                getGeoLocation();
            } catch (Exception e) {
                Log.d(TAG, "CaptureImage: " + e.getMessage());
            }
            Intent imagePrev = new Intent(DetectorActivity.this, ImagePreviewActivity.class);
            try {
                Bitmap copyOfFrame;
                float degrees = 90;
                Matrix matrix = new Matrix();
                matrix.setRotate(degrees);
                copyOfFrame = Bitmap.createBitmap(rgbFrameBitmap, 0, 0, rgbFrameBitmap.getWidth(), rgbFrameBitmap.getHeight(), matrix, true);
                imagePrev.putExtra("byteArray", bitmapToArray(copyOfFrame));
                imagePrev.putExtra("backUpImage", bitmapToArray(cropCopyBitmap));
                imagePrev.putExtra("symptomsDetected", detectedSymptomsList);
                imagePrev.putExtra("longt", longt);
                imagePrev.putExtra("lat", lat);
                startActivity(imagePrev);
            } catch (Exception e) {
                Log.d(TAG, "CaptureImage: " + e.getMessage());
            }

        });

        lastDetection = confidenceList.size();
    }

    private byte[] bitmapToArray(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bs);
        return bs.toByteArray();
    }

    private void getGeoLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            longt = location.getLongitude();
            lat = location.getLatitude();
        }
         final LocationListener locationListener = location1 -> {
             longt = location1.getLongitude();
             lat = location1.getLatitude();
         };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
