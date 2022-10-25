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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class MoreInfo extends AppCompatActivity {
TextView title, symptomsList;
ImageView withBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        symptomsList = findViewById(R.id.symptomsList);
        withBox = findViewById(R.id.withBox);
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            double lat = extras.getDouble("lat");
            double longt = extras.getDouble("longt");
            Bitmap bitmap = BitmapFactory.decodeByteArray(extras.getByteArray("imageWithBox"), 0, extras.getByteArray("imageWithBox").length);
            withBox.setImageBitmap(bitmap);
            symptomsList.setText(HtmlCompat.fromHtml(extras.getString("Symptoms"),HtmlCompat.FROM_HTML_MODE_LEGACY));
            symptomsList.append("\n");
            symptomsList.append("GEOLOCATION: \n");
            symptomsList.append("Latitude: " + lat);
            symptomsList.append("\n");
            symptomsList.append("Longitude: " + longt);
        }
    }
}