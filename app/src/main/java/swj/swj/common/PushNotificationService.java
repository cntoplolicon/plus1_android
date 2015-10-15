package swj.swj.common;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joda.time.DateTime;

import io.yunba.android.manager.YunBaManager;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.model.Notification;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 10/12/15.
 */
public class PushNotificationService {

    public static final String TYPE_COMMENT = "comment";

    private static PushNotificationService instance;
    private Context context;

    public static void init(final Context context) {
        YunBaManager.start(context);
        instance = new PushNotificationService(context);
    }

    private PushNotificationService(final Context context) {
        this.context = context;
        User.setUserChangedCallback(new User.UserChangedCallback() {

            @Override
            public void onUserChanged(User oldUser, User newUser) {
                final FailCallback<Throwable> failCallback = new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable t) {
                        Log.e(PushNotificationService.class.getName(), "failed subscribing or unsubscribing", t);
                        Toast.makeText(context, "更新推送配置失败", Toast.LENGTH_SHORT).show();
                    }
                };
                String[] oldTopics = getUserTopics(oldUser);
                String[] newTopics = getUserTopics(newUser);
                if (oldTopics.length > 0) {
                    unsubscribe(oldTopics).fail(failCallback);
                }
                if (newTopics.length > 0 && newUser != null && newUser.isNotificationsEnabled()) {
                    subscribe(newTopics).fail(failCallback);
                }
            }
        });
    }

    private Promise<IMqttToken, Throwable, Void> unsubscribe(String topics[]) {
        final DeferredObject<IMqttToken, Throwable, Void> deferred = new DeferredObject<>();
        YunBaManager.unsubscribe(context, topics, new NotifyPromiseListener(deferred));
        return deferred.promise();
    }

    private Promise<IMqttToken, Throwable, Void> subscribe(String topics[]) {
        final DeferredObject<IMqttToken, Throwable, Void> deferred = new DeferredObject<>();
        YunBaManager.subscribe(context, topics, new NotifyPromiseListener(deferred));
        return deferred.promise();
    }

    private String[] getUserTopics(User user) {
        return user == null ? new String[]{} : getUserTopics(user.getId());
    }

    private String[] getUserTopics(int userId) {
        return new String[]{"user_" + userId};
    }

    public static PushNotificationService getInstance() {
        return instance;
    }

    public void handleNotification(String topic, String message) {
        Gson gson = CommonMethods.defaultGsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Notification notification = gson.fromJson(message, Notification.class);
        if (User.current != null && notification.getUserId() > 0
                && notification.getUserId() != User.current.getId()) {
            unsubscribe(getUserTopics(notification.getUserId()));
            return;
        }
        notification.setReceiveTime(DateTime.now());
        notification.save();

        Intent intent = new Intent(context, CardDetailsActivity.class);
        intent.putExtra("notification", notification);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(CardDetailsActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notificaiton_small)
                .setContentTitle("new comment")
                .setContentText("new comment to you")
                .setContentIntent(pendingIntent);
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);
        notifyManager.notify(notification.getId().intValue(), notificationBuilder.build());
    }

    private static class NotifyPromiseListener implements IMqttActionListener {
        private DeferredObject<IMqttToken, Throwable, Void> deferredObject;

        private NotifyPromiseListener(DeferredObject<IMqttToken, Throwable, Void> deferredObject) {
            this.deferredObject = deferredObject;
        }

        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            deferredObject.resolve(iMqttToken);
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            deferredObject.reject(throwable);
        }
    }
}
