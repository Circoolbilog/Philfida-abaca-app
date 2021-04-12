package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsContainer extends Application {
    public static final String SHARED_PREFS = "sharedPrefs";

    public static final String DIAGNOSE_MODE = "diagnoseMode";
    int diagnoseMode;

//    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

    public int getDiagnoseMode() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        diagnoseMode = sharedPreferences.getInt(DIAGNOSE_MODE, 0);
        return diagnoseMode;
    }
    public void setDiagnoseMode(int diagnoseMode) {
        this.diagnoseMode = diagnoseMode;
    }
}
