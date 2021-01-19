package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {
    static final String PREF_USER_NAME = "username";

    static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    public static void setPrefUserName(Context context, String username){
        SharedPreferences.Editor editor =getSharedPreference(context).edit();
        editor.putString(PREF_USER_NAME,username);
        editor.apply();
    }
    public static String getUsername(Context context){
        return getSharedPreference(context).getString(PREF_USER_NAME,"");
    }
    public static void clearUsername(Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.clear();
        editor.apply();
    }
}
