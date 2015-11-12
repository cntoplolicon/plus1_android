package com.oneplusapp.common;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.oneplusapp.R;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by cntoplolicon on 11/2/15.
 */
public class UpdateChecker {

    public static UpdateChecker instance;
    public String serverVersionName;

    private static String APK_MIME_TYPE = "application/vnd.android.package-archive";

    private boolean updateNotified;
    private Long downloadId;
    private ApkDownloadReceiver receiver;

    public static UpdateChecker getInstance() {
        if (instance == null) {
            instance = new UpdateChecker();
        }
        return instance;
    }

    public void checkUpdate(final Context context) {
        if (updateNotified) {
            return;
        }

        RestClient.getInstance().getAppRelease().done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                AppRelease appRelease = CommonMethods.createDefaultGson().fromJson(result.toString(), AppRelease.class);
                showUpdateNotification(context, appRelease);
                updateNotified = true;
            }
        }).fail(new JsonErrorListener(context, null));
    }

    public int getCurrentVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("version code must be specified in manifest", e);
        }
    }

    public String getCurrentVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("version name must be specified in manifest", e);
        }
    }

    public void showUpdateNotification(final Context context, final AppRelease appRelease) {
        serverVersionName = appRelease.versionName;
        if (getCurrentVersionCode(context) >= appRelease.versionCode) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.custom_dialog_update_versions);
        WindowManager.LayoutParams lp = window.getAttributes();
        Display display = window.getWindowManager().getDefaultDisplay();
        lp.width = (int) (display.getWidth() * 0.8);
        window.setAttributes(lp);

        TextView tvReminder = (TextView) window.findViewById(R.id.tv_reminder);
        tvReminder.setText(appRelease.message);

        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        final TextView tvConfirm = (TextView) window.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvConfirm.setEnabled(false);
                downloadRelease(context, appRelease);
                alertDialog.cancel();
            }
        });
    }

    private void downloadRelease(Context context, AppRelease appRelease) {
        if (receiver == null) {
            receiver = new ApkDownloadReceiver();
            context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        Uri uri = Uri.parse(appRelease.downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setMimeType(APK_MIME_TYPE);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    private void promptUpdate(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, APK_MIME_TYPE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static class AppRelease {
        public int versionCode;
        public String message;
        public String versionName;
        public String downloadUrl;
    }

    private class ApkDownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                return;
            }
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != UpdateChecker.this.downloadId) {
                return;
            }
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = null;
            try {
                cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        File downloadedFile = CommonMethods.getFileFromMediaUri(context, Uri.parse(uri));
                        if (downloadedFile == null) {
                            Toast.makeText(context, R.string.downloaded_apk_missing, Toast.LENGTH_LONG).show();
                            return;
                        }
                        promptUpdate(context, Uri.fromFile(downloadedFile));
                        if (receiver != null) {
                            context.unregisterReceiver(receiver);
                            receiver = null;
                        }
                    } else {
                        Toast.makeText(context, R.string.download_apk_error, Toast.LENGTH_LONG).show();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public Promise<AppRelease, VolleyError, Void> getAppRelease(final Context context) {
        final ThrowableDeferredObject<AppRelease, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();

        RestClient.getInstance().getAppRelease().done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                AppRelease appRelease = CommonMethods.createDefaultGson().fromJson(result.toString(), AppRelease.class);
                deferredObject.resolve(appRelease);
            }
        }).fail(new JsonErrorListener(context, null) {
            @Override
            public void onFail(VolleyError error) {
                super.onFail(error);
                deferredObject.reject(error);
            }
        });

        return deferredObject.promise();
    }
}
