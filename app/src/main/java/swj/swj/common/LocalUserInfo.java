package swj.swj.common;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jiewei on 9/4/15.
 */
public class LocalUserInfo {

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String PREFERENCE_NAME = "local_userinfo";
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

    public void setUserInfo(String strName, String strValue) {
        editor.putString(strName, strValue);
        editor.commit();
    }

    public String getUserInfo(String strName) {
        return mSharedPreferences.getString(strName, "");
    }

    public String getUserId() {
        return getUserInfo(KEY_USER_ID);
    }

    public String getAccessToken() {
        return getUserInfo(KEY_ACCESS_TOKEN);
    }
}
