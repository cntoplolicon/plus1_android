package swj.swj.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swj.swj.R;
import swj.swj.activity.LoginActivity;
import swj.swj.adapter.InfectionsAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.model.User;

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
        InfectionsAdapter.getInstance().reset();
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

    public static void showImageDialog(final String resString, final Activity activity) {
        final AlertDialog imageDialog = new AlertDialog.Builder(activity).create();
        imageDialog.show();
        Window window = imageDialog.getWindow();
        window.setContentView(R.layout.dialog_image);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                .showImageOnLoading(R.color.home_title_color)
                .showImageOnFail(R.drawable.image_load_fail)
                .build();
        ImageView ivEnlargedImage = (ImageView) window.findViewById(R.id.iv_enlarged_image);
        if (resString != null) {
            ImageLoader.getInstance().displayImage(resString, ivEnlargedImage, options);
        }
        ivEnlargedImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new DownloadImageTask().execute(resString);
                Toast.makeText(activity.getApplicationContext(), "Downloading", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private static class DownloadImageTask extends AsyncTask<String, Context, Void> {

        private File imageFile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/plusOneApp/images");
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageName = "plus_one_" + timeStamp + ".jpg";
            imageFile = new File(mediaStorageDir.getPath() + File.separator + imageName);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                InputStream inputStream = url.openStream();
                try {
                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    try {
                        byte[] buffer = new byte[5 * 1024 * 1024];
                        int bytesRead = 0;
                        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >=0) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
