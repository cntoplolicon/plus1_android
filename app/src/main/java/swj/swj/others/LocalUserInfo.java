package swj.swj.others;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jiewei on 9/4/15.
 */
public class LocalUserInfo {
    public static final String PREFERENCE_NAME = "local_userinfo";
    private static SharedPreferences mSharedPreferences;
    private static LocalUserInfo mLocalUserInfo;
    private static SharedPreferences.Editor editor;

    private LocalUserInfo(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static LocalUserInfo getInstance(Context context) {
        if (mLocalUserInfo == null) {
            mLocalUserInfo = new LocalUserInfo(context);
        }
        editor = mSharedPreferences.edit();
        return mLocalUserInfo;
    }

    public void setUserInfo(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    public String getUserInfo(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }
}
