package com.example.workforcemanagement.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TokenManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "token";
    private final SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        Log.d("TOKEN_CHECK", "SAVE TOKEN: " + token);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getToken() {
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        Log.d("TOKEN_CHECK", "GET TOKEN: " + token);
        return token;
    }

    public String getBearerToken() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }

    public void clearToken() {
        Log.d("TOKEN_CHECK", "CLEAR TOKEN");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public boolean hasToken() {
        return getToken() != null;
    }
}