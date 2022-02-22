package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.SettingsDialog1;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity implements SettingsDialog1.SettingsDialogListener {
    public static final String LAST_NAME = "LAST_NAME";
    public static final String FIRST_NAME = "FIRST_NAME";
    LinearLayout captureMode, userAccount, locationSettings, confidenceThreshold, showWelcome,
            language, about;
    TextView captureModeVal, userAccountVal, locationSettingsVal, confidenceThresholdVal, showWelcomeVal,
            languageVal, aboutVal;
    public static final String SHARED_PREFS = "USER_DATA";
    String sCaptureMode, sUserAccount, sLocationSettings, sConfidenceThreshold, sShowWelcome,
            sLanguage, sAbout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");
        setupIDs();
        loadSharedPrefs();
        getUserAccount();
        if (((SettingsContainer) this.getApplication()).getGuest()){
            userAccount.setVisibility(View.GONE);
        }

        captureMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Capture Mode",
                        "Select preferred Detection Method: ",
                        "Always Ask First",
                        "Single Capture Mode",
                        "Multi-Capture Mode",
                        0);
            }
        });
        userAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AccountDetails.class);
                startActivity(intent);
            }

        });
        locationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        confidenceThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Enter Confidence Threshold",
                        "Enter desired value for minimum confidence(cancel if you don't know what this does)",
                        "Okay",
                        "Cancel",
                        null,
                        R.layout.custom_confidence_threshold_dialog);
            }
        });
        showWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Show Welcome Screen",
                        "Show welcome screen at start of app? ",
                        "Yes",
                        "No",
                        null,
                        0);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AboutApp.class);
                startActivity(intent);
            }
        });
        languageVal.setText("Work In Progress");

        //TODO: Add Change Camera
        //TODO: Add Dark Mode
        //TODO: Add update and notif settings
        //TODO: Weather
    }

    private void getUserAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sUserAccount = sharedPreferences.getString(FIRST_NAME, "")
                + " " +sharedPreferences.getString(LAST_NAME, "");
        userAccountVal.setText(sUserAccount);
    }

    private void openDialog(String title, String message, String option1,
                            String option2, String option3, int customLayout) {
        SettingsDialog1 dialog1 = new SettingsDialog1();
        dialog1.setCustomDialogLayout(customLayout);
        dialog1.setContext(SettingsActivity.this);
        dialog1.setDialogTitle(title);
        dialog1.setDialogMessage(message);
        dialog1.setOption1(option1);
        dialog1.setOption2(option2);
        dialog1.setOption3(option3);
        dialog1.show(getSupportFragmentManager(), "dialog1");
    }


    private void loadSharedPrefs() {
        if (!((SettingsContainer) this.getApplication()).getDiagDialogRemember()) {
            sCaptureMode = "Always Ask";
        } else {
            switch (((SettingsContainer) this.getApplication()).getDiagnoseMode()) {
                case 0:
                    sCaptureMode = "Single Capture Mode";
                    break;
                case 1:
                    sCaptureMode = "Multi-Capture Mode";
                    break;
            }
        }
        showWelcomeVal.setText(((SettingsContainer) this.getApplication()).getShowWelcome().toString());
        captureModeVal.setText(sCaptureMode);
        String conValThresh = String.valueOf(Math.round(((SettingsContainer) this.getApplication()).getConfidence() * 100));
        confidenceThresholdVal.setText(conValThresh + "%");
    }

    private void setupIDs() {
        languageVal = findViewById(R.id.languageVal);
        showWelcomeVal = findViewById(R.id.ShowWelcomeScreenVal);
        userAccountVal = findViewById(R.id.userAccVal);
        captureModeVal = findViewById(R.id.captureModeVal);
        captureMode = findViewById(R.id.captureModeSet);
        userAccount = findViewById(R.id.userAccSet);
        locationSettings = findViewById(R.id.LocationSet);
        confidenceThreshold = findViewById(R.id.ConfidenceThreshSet);
        confidenceThresholdVal = findViewById(R.id.ConfidenceThreshVal);
        showWelcome = findViewById(R.id.ShowWelcomeScreen);
        language = findViewById(R.id.languageSet);
        about = findViewById(R.id.aboutApp);
    }

    public void setCaptureMode(int Selection) {
        switch (Selection) {
            case 1:
                ((SettingsContainer) getApplication()).setDiagDialogRemember(false);
                break;
            case 2:
                ((SettingsContainer) this.getApplication()).setDiagDialogRemember(true);
                ((SettingsContainer) this.getApplication()).setDiagnoseMode(0);
                break;
            case 3:
                ((SettingsContainer) this.getApplication()).setDiagDialogRemember(true);
                ((SettingsContainer) this.getApplication()).setDiagnoseMode(1);
                break;
        }
        loadSharedPrefs();


    }


    public void setWelcomeScreen(int selection) {
        switch (selection) {
            case 1:
                ((SettingsContainer) this.getApplication()).setShowWelcome(true);
                break;
            case 2:
                ((SettingsContainer) this.getApplication()).setShowWelcome(false);
                break;
        }
        loadSharedPrefs();
    }
    public void setConfidenceThreshold(int confidenceThreshold){
        float f=confidenceThreshold;
        loadSharedPrefs();
        Toast.makeText(this, "confidence: " + f, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void applyConfidence(int confidence) {
        loadSharedPrefs();
        float fConfidence = confidence/100f;
        Toast.makeText(this, "confidence: " + confidence, Toast.LENGTH_SHORT).show();
        ((SettingsContainer) this.getApplication()).setConfidence(fConfidence);
    }
}
