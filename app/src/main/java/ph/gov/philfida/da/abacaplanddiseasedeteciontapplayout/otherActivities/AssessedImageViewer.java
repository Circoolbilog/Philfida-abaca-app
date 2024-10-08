/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DetectedObjectsData;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.DiseaseInfoDBHelper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class AssessedImageViewer extends AppCompatActivity {
    private static final String TAG = "AssessedImageViewer";
    ImageView assessedImage;
    Bitmap selectedImage, boxedImage;
    TextView diseaseInfo;
    Button buttonViewBox, openMap;
    float latitude, longitude;
    String info;
    boolean viewBoxed = false;
    List<DetectedObjectsData> dbModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesed_image_viewer);
        assessedImage = findViewById(R.id.assessedImage);
        diseaseInfo = findViewById(R.id.diseaseInfo);
        buttonViewBox = findViewById(R.id.buttonViewBoxes);
        buttonViewBox.setOnClickListener(v ->
                {
                    if (!viewBoxed) {
                        assessedImage.setImageBitmap(boxedImage);
                        viewBoxed = true;
                        buttonViewBox.setText(R.string.b);
                    } else {
                        buttonViewBox.setText(R.string.vb);
                        viewBoxed = false;
                        assessedImage.setImageBitmap(selectedImage);
                    }
                }
        );
        openMap = findViewById(R.id.buttonOpenMaps);
        openMap.setOnClickListener(view -> {
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=" + latitude + "," + longitude, latitude, longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            this.startActivity(intent);
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String fileName = extras.getString("file");
            selectedImage = BitmapFactory.decodeFile(fileName);
            String BfileName = fileName.replace("Assessment", "AssessmentBoxed");
            BfileName = BfileName.replace("Image", "Boxed");
            boxedImage = BitmapFactory.decodeFile(BfileName);

            assessedImage.setImageBitmap(selectedImage);

            String textFile = fileName.replace(".jpg", "_info.txt");
            textFile = textFile.replace("Pictures", "Documents");

            // Check if the text file exists
            if (new File(textFile).exists()) {
                if (isBuildVersionQ()) {
                    File file = new File(textFile);
                    info = viewInfoQ(file.getName());
                    diseaseInfo.setText(HtmlCompat.fromHtml(info, HtmlCompat.FROM_HTML_MODE_LEGACY));
                    try {
                        String remove = info.replaceAll("[\n]", "");
                        remove = remove.replaceAll(".*Lo", "Lo");
                        String longt = remove.replaceAll("Latitude: .*", "");
                        longt = longt.replaceAll("i", "");
                        String lat = remove.replaceAll(".*Longitude: .*L", "L");
                        longitude = Float.parseFloat(longt.replaceAll("[a-zA-Z<>:_]", ""));
                        latitude = Float.parseFloat(lat.replaceAll("[a-zA-Z<>:_]", ""));
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    textFile = fileName.replace("jpg", "_info.txt");
                    diseaseInfo.setText(HtmlCompat.fromHtml(viewInfo(textFile), HtmlCompat.FROM_HTML_MODE_LEGACY));
                    try {
                        String remove = viewInfo(textFile).replaceAll("[\n]", "");
                        remove = remove.replaceAll(".*Lo", "Lo");
                        String longt = remove.replaceAll("Latitude: .*", "");
                        longt = longt.replaceAll("i", "");
                        String lat = remove.replaceAll(".*Longitude: .*L", "L");
                        longitude = Float.parseFloat(longt.replaceAll("[a-zA-Z<>:_]", ""));
                        latitude = Float.parseFloat(lat.replaceAll("[a-zA-Z<>:_]", ""));
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                // If the text file doesn't exist, fetch information from the database
                getInfoFromDB(fileName);
            }
        }
    }

    private boolean isBuildVersionQ() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.Q;
    }

    private void getInfoFromDB(String file) {
        String infos = "";
        File filePath = new File(file);
        String filename = filePath.getName();
        DiseaseInfoDBHelper infoDBHelper = new DiseaseInfoDBHelper(this);
        diseaseInfo.setText("");
        List<DetectedObjectsData> fromDB = infoDBHelper.searchByImageFileName(filename);
        if (fromDB.size() == 1) {
            DetectedObjectsData detectedObjectsData = fromDB.get(0);
            infos = String.join(",",detectedObjectsData.getDetectedDiseases());
            infos = infos + "\n\nSymptoms: \n" + String.join(",",detectedObjectsData.getSymptoms());
            infos = infos + "\n\nGeolocation:\n" + detectedObjectsData.getGeolocation();
            infos = infos.replace(",","\n");
            diseaseInfo.setText(infos);
        } else {
            for (DetectedObjectsData detectedObjectsData : fromDB) {
                infos = String.join(",",detectedObjectsData.getDetectedDiseases());
                infos = infos + "\n\nSymptoms: \n" + String.join(",",detectedObjectsData.getSymptoms());
                infos = infos + "\n\nGeolocation:\n" + detectedObjectsData.getGeolocation();
                infos = infos.replace(",","\n");
                diseaseInfo.setText(infos);
            }
        }
    }

    //in case future android versions requires mediastore in the future.
    private String viewInfoQ(String selected) {
        if (isBuildVersionQ()) {

            Uri textContentUri = MediaStore.Files.getContentUri("external");
            String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";
            String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Assessment/"};

            Cursor cursor = getContentResolver().query(textContentUri, null, selection, selectionArgs, null);
            Uri uri = null;
            if (cursor.getCount() == 0) {
                Log.d(TAG, "viewInfoQ: " + "No Info File Found in \"" + textContentUri + "DIRECTORY");
            } else {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                    selected = selected.replace("._info", "_info");
                    if (fileName.equals(selected)) {
                        @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                        uri = ContentUris.withAppendedId(textContentUri, id);
                        break;
                    }
                }
                cursor.close();
                if (uri == null) {
                    Toast.makeText(this, selected + " not found", Toast.LENGTH_LONG).show();
                } else {
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
        return "no such file in directory";
    }

    private String viewInfo(String textFile) {
        FileReader fr;
        String newFile = textFile.replace("Pictures", "documents");
        StringBuilder stringBuilder = new StringBuilder();
        String output;
        try {
            fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "viewInfo: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            String fileContents = stringBuilder.toString();
            fileContents = fileContents.replace(")", "");
            output = fileContents.replace("RectF(", "Location(Coordinates): ");
        }
        return output;
    }
}