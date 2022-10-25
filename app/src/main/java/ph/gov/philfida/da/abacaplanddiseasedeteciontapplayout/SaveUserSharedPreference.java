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
package ph.gov.philfida.da.abacaplanddiseasedeteciontapplayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveUserSharedPreference {
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
