package com.oneplusapp.common;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oneplusapp.R;
import com.oneplusapp.activity.LoginActivity;
import com.oneplusapp.adapter.InfectionsAdapter;
import com.oneplusapp.model.User;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by silentgod on 15-9-5.
 */
public final class CommonMethods {
    // input pattern for validation checked on front_end
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[ -~]{6,20}$");
    private static final Pattern SECURITY_CODE_PATTERN = Pattern.compile("^[0-9]{6}$");

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    private CommonMethods() {
    }

    //method to check input validation
    //username 11digits
    public static boolean isValidUsername(String username) {
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }

    //pwd 6-20 digits or letters
    public static boolean isValidPwd(String pwd) {
        Matcher matcher = PASSWORD_PATTERN.matcher(pwd);
        return matcher.matches();
    }

    //security code 6 digits
    public static boolean isValidSCode(String sCode) {
        Matcher matcher = SECURITY_CODE_PATTERN.matcher(sCode);
        return matcher.matches();
    }

    //check if there is an SD card
    public static boolean hasSdCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getFirstError(JSONObject errorMessages, String field) {
        JSONArray fieldErrors = errorMessages.optJSONArray(field);
        if (fieldErrors != null && fieldErrors.length() > 0) {
            return fieldErrors.optString(0, "");
        }
        return "";
    }

    public static void showError(Context context, JSONObject errors, String field) {
        String errorDetail = getFirstError(errors, field);
        if (!errorDetail.isEmpty()) {
            errorDetail = errorDetail.replace(" ", "_");
            int resourceId = context.getResources().getIdentifier(field + "_" + errorDetail, "string", context.getPackageName());
            if (resourceId == 0) {
                resourceId = R.string.unknown_error;
            }
            CommonDialog.showDialog(context, resourceId);
        }
    }

    public static Gson createDefaultGson() {
        return defaultGsonBuilder().create();
    }

    public static GsonBuilder defaultGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.registerTypeAdapter(DateTime.class, new GsonJodaTimeHandler());
        return builder;
    }

    public static void clientSideSignOut(Context context) {
        User.clearCurrentUser();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void chooseNicknameColorViaGender(TextView textView, User user, Context context) {
        if (user.getGender() == User.GENDER_MALE) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.personal_common_male_username));
        } else if (user.getGender() == User.GENDER_FEMALE) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.personal_common_female_username));
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.unknown_gender));
        }
    }

    public static File getFileFromMediaUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        ContentResolver resolver = context.getContentResolver();
        if (SCHEME_FILE.equals(uri.getScheme())) {
            return new File(uri.getPath());
        }
        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (filePath != null && !filePath.isEmpty()) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (SecurityException e) {
                Log.w(BitmapUtil.class.getName(), "failed converting uri to file", e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return null;
    }

}
