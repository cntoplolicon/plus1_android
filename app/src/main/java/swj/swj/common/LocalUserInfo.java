package swj.swj.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jiewei on 9/4/15.
 */
public class LocalUserInfo {

    private static final String PREFERENCE_NAME = "local_userinfo";

    private static LocalUserInfo instance;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public synchronized static void initialize(Context context) {
        if (instance != null) {
            return;
        }
        instance = new LocalUserInfo(context);
    }

    private LocalUserInfo(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static LocalUserInfo getInstance() {
        return instance;
    }

    public void setUserInfo(String strName, String strValue) {
        editor.putString(strName, strValue);
        editor.commit();
    }

    public String getUserInfo(String strName) {
        return preferences.getString(strName, "");
    }
}
