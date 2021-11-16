package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout.containers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsContainer extends Application {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String DIAG_DIALOG_REMEMBER = "diagDialogRemember";

    public static final String DIAGNOSE_MODE = "diagnoseMode";

    int diagnoseMode;
    Boolean diagDialogRemember;
    float confidence;

    public float getConfidence() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        confidence = sharedPreferences.getFloat("CONFIDENCE", 50);
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
        saveFloatToSharedPrefs("CONFIDENCE",confidence);
    }

    public Boolean getShowWelcome() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        showWelcome = sharedPreferences.getBoolean("SHOW_WELCOME", true);
        return showWelcome;
    }

    public void setShowWelcome(Boolean showWelcome) {
        this.showWelcome = showWelcome;
        saveBooleanToSharedPrefs("SHOW_WELCOME",showWelcome);
    }

    Boolean showWelcome;

    public void setDiagDialogRemember(Boolean diagDialogRemember) {
        this.diagDialogRemember = diagDialogRemember;
        saveBooleanToSharedPrefs(DIAG_DIALOG_REMEMBER,diagDialogRemember);
    }

    public Boolean getDiagDialogRemember() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        diagDialogRemember = sharedPreferences.getBoolean(DIAG_DIALOG_REMEMBER, false);
        return diagDialogRemember;
    }
//    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

    public int getDiagnoseMode() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        diagnoseMode = sharedPreferences.getInt(DIAGNOSE_MODE, 0);
        return diagnoseMode;
    }
    public void setDiagnoseMode(int diagnoseMode) {
        this.diagnoseMode = diagnoseMode;
        saveIntToSharedPrefs(DIAGNOSE_MODE,diagnoseMode);
    }

    //Method used to save a string to shared prefs.
    public void saveStringToSharedPrefs(String whatToSave, String valueToSave){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(whatToSave, valueToSave);
        editor.apply();
    }
    //Method used to save a boolean to shared prefs.
    public void saveBooleanToSharedPrefs(String whatToSave, Boolean valueToSave){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(whatToSave, valueToSave);
        editor.apply();
    }
    //Method used to save a int to shared prefs.
    public void saveIntToSharedPrefs(String whatToSave, int valueToSave){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(whatToSave, valueToSave);
        editor.apply();
    }
    //Method used to save a float to shared prefs.
    public void saveFloatToSharedPrefs(String whatToSave, float valueToSave){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putFloat(whatToSave, valueToSave);
        editor.apply();
    }
}
