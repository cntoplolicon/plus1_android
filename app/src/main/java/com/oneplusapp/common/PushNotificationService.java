package com.oneplusapp.common;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.activity.HomeActivity;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;

import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cntoplolicon on 10/12/15.
 */
public class PushNotificationService {

    public static final String TYPE_COMMENT = "comment";

    private static PushNotificationService instance;
    private Context context;
    private Set<Callback> callbacks = new HashSet<>();
    private Promise<String, AVException, Void> installationPromise;

    public static void init(Context context) {
        instance = new PushNotificationService(context);
    }

    private PushNotificationService(final Context context) {
        this.context = context;
        PushService.setDefaultPushCallback(context, HomeActivity.class);
        installationPromise = saveInstallation();
        installationPromise.fail(new FailCallback<AVException>() {
            @Override
            public void onFail(AVException e) {
                Log.e(PushNotificationService.class.getName(), "failed saving av installation id", e);
                Toast.makeText(context, R.string.notification_config_error, Toast.LENGTH_LONG).show();
            }
        });
        User.registerUserChangedCallback(new User.UserChangedCallback() {
            @Override
            public void onUserChanged(User oldUser, User newUser) {
                if (newUser != null) {
                    syncUserInstallationId(newUser, AVInstallation.getCurrentInstallation().getInstallationId());
                }
            }
        });
    }

    private void syncUserInstallationId(User user, String installationId) {
        if (user.getAccessToken() == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("av_installation_id", installationId);
        RestClient.getInstance().updateAccountInfo(user.getId(), user.getAccessToken(), params)
                .fail(new JsonErrorListener(context, null));
    }

    private void trySaveInstallation(final int retry, final int delay, final DeferredObject<String, AVException, Void> deferredObject) {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    deferredObject.resolve(AVInstallation.getCurrentInstallation().getInstallationId());
                    return;
                }

                Log.e(PushNotificationService.class.getName(), "failed saving installation", e);
                if (retry == 0) {
                    deferredObject.reject(e);
                } else {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            trySaveInstallation(retry - 1, delay * 2, deferredObject);
                        }
                    }, delay);
                }
            }
        });
    }

    private Promise<String, AVException, Void> saveInstallation() {
        ThrowableDeferredObject<String, AVException, Void> deferredObject = new ThrowableDeferredObject<>();
        trySaveInstallation(5, 1000, deferredObject);  // 5 reties, exponential backoff from 1 sec
        return deferredObject.promise();
    }

    public static PushNotificationService getInstance() {
        return instance;
    }

    public void handleNotification(Notification notification) {
        if (User.current != null && notification.getUserId() > 0
                && notification.getUserId() != User.current.getId()) {
            syncUserInstallationId(User.current, AVInstallation.getCurrentInstallation().getInstallationId());
            return;
        }

        notification.setReceiveTime(DateTime.now());
        notification.save();
        for (Callback callback : callbacks) {
            callback.onNotificationReceived(notification);
        }
        if (!LocalUserInfo.getPreferences().getBoolean("notification_enabled", true)) {
            return;
        }

        if (notification.getType().equals(TYPE_COMMENT)) {
            Intent intent = new Intent(context, CardDetailsActivity.class);
            intent.putExtra("notification", notification);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(CardDetailsActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(notification.getId().intValue(),
                    PendingIntent.FLAG_ONE_SHOT);

            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            String commentFormat = (comment.getReplyToId() == 0 ?
                    context.getResources().getString(R.string.notification_card) :
                    context.getResources().getString(R.string.notification_comment));

            String notificationBarBody = String.format(commentFormat, comment.getUser().getNickname());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification_small)
                    .setContentTitle(context.getResources().getString(R.string.notification_comment_title))
                    .setContentText(notificationBarBody)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager notifyManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);
            notifyManager.notify(notification.getId().intValue(), notificationBuilder.build());
        }
    }

    public interface Callback {
        void onNotificationReceived(Notification notification);
    }

    public void registerCallback(Callback callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        callbacks.remove(callback);
    }
}
