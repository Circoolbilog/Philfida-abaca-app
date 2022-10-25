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

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

public class AboutDev extends AppCompatActivity {
    TextView devsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dev);
        devsTextView = findViewById(R.id.developers);
        String developersHTML = "<p>Android Application: </p><a href = \"https://github.com/Circoolbilog/\" >Clark L. Llarena</a> <p>Machine Learning Model: </p><a href = \"https://github.com/TaliyaB/\">Christalline Joie Borjal</a>";
        devsTextView.setText(HtmlCompat.fromHtml(developersHTML,HtmlCompat.FROM_HTML_MODE_LEGACY));
        devsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}