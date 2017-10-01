package com.foursquare.android.sample;


import android.content.Context;
import android.content.SharedPreferences;

public class PersistentTokenStore {
    private static final String SHARED_PREFERENCES_NAME = "persistent";
    private static final String KEY_TOKEN = "token";

    private static PersistentTokenStore self;
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PersistentTokenStore getInstance() {
        if (self == null) {
            self = new PersistentTokenStore();
        }

        return self;
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }
}
