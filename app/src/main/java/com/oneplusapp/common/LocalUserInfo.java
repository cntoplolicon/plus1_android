package com.oneplusapp.common;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalUserInfo {

    private static final String PREFERENCE_NAME = "local_userinfo";

    private static SharedPreferences preferences;

    public synchronized static void initialize(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
    }

    private LocalUserInfo() {
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }
}
