package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.otherActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.Dialogs.SettingsDialog1;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.R;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleArrayAdapter;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.adapters.SimpleItem;
import ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers.SettingsContainer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
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

        captureMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Capture Mode",
                        "Select preferred Detection Method: ",
                        "Always Ask First",
                        "Single Capture Mode",
                        "Multi-Capture Mode");
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

            }
        });
        showWelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog("Show Welcome Screen",
                        "Show welcome screen at start of app? ",
                        "Yes",
                        "No",
                        null);
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
                            String option2, String option3) {
        SettingsDialog1 dialog1 = new SettingsDialog1();
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


    public void setWelcomeScreen(int Selection) {
        switch (Selection) {
            case 1:
                ((SettingsContainer) this.getApplication()).setShowWelcome(true);
                break;
            case 2:
                ((SettingsContainer) this.getApplication()).setShowWelcome(false);
                break;
        }
        loadSharedPrefs();
    }
}