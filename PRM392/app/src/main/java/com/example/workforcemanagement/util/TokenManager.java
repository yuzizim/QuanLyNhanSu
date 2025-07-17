//// TokenManager
//package com.example.workforcemanagement.util;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//public class TokenManager {
//    private final SharedPreferences sharedPreferences;
//
//    public TokenManager(Context context) {
//        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
//    }
//
//    public void saveToken(String token) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(Constants.KEY_TOKEN, token);
//        editor.apply();
//    }
//
//    public String getToken() {
//        return sharedPreferences.getString(Constants.KEY_TOKEN, null);
//    }
//
//    public void clearToken() {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(Constants.KEY_TOKEN);
//        editor.apply();
//    }
//
//    public boolean hasToken() {
//        return getToken() != null;
//    }
//}


package com.example.workforcemanagement.util;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs"; // Moved from Constants
    private static final String KEY_TOKEN = "token"; // Moved from Constants
    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public boolean hasToken() {
        return getToken() != null;
    }
}