package swj.swj.common;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swj.swj.R;

/**
 * Created by silentgod on 15-9-5.
 */
public class CommonMethods {
    // input pattern for validation checked on front_end
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[0-9]{11}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[ -~]{6,20}$");
    private static final Pattern SECURITY_CODE_PATTERN = Pattern.compile("^[0-9]{6}$");

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

    public static void toastError(Context context, JSONObject errors, String field) {
        String errorDetail = getFirstError(errors, field);
        if (!errorDetail.isEmpty()) {
            errorDetail.replace(" ", "_");
            int resourceId = context.getResources().getIdentifier(field + "_" + errorDetail, "string", context.getPackageName());
            if (resourceId == 0) {
                resourceId = R.string.unknown_error;
            }
            Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
        }
    }

    public static Gson createDefaultGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        builder.registerTypeAdapter(DateTime.class, new GsonJodaTimeHandler());
        return builder.create();
    }
}
