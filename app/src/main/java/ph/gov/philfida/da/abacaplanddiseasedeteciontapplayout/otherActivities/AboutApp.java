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

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutApp extends AppCompatActivity {
    private static final String TAG = "AboutApp";
    LinearLayout appVersion, tutorial, developer,reportBug;
    TextView appVersionVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        assignIDs();
    }

    private void assignIDs() {
        appVersion = findViewById(R.id.AppVersion);
        tutorial = findViewById(R.id.guide);
        developer = findViewById(R.id.Developer);
        reportBug = findViewById(R.id.reportBug);
        appVersionVal = findViewById(R.id.AppVersionVal);
        appVersionVal.setText(getAppVersion());
        developer.setOnClickListener(v -> {
            Intent dev = new Intent(this, AboutDev.class);
            startActivity(dev);
        }) ;
        reportBug.setOnClickListener(view -> {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "circoolardev@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Bug report: " + Build.MANUFACTURER + Build.MODEL);
            email.putExtra(Intent.EXTRA_TEXT, "App Version: " + getAppVersion() + "\nSDK Version: " + Build.VERSION.SDK_INT + "\n" +
                    "Describe the bug or problem with the app: ");

            //need this to prompts email client only
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        });
        tutorial.setOnClickListener(view ->{

        });
    }

    private String getAppVersion() {
        String version;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = e.getMessage();
        }
        return version;
    }

}