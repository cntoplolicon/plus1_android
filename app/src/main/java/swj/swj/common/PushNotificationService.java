package swj.swj.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import io.yunba.android.manager.YunBaManager;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 10/12/15.
 */
public class PushNotificationService {

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
                if (newTopics.length > 0) {
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
        if (user == null || !user.isNotificationsEnabled()) {
            return new String[]{};
        }
        return new String[]{user.getUsername()};
    }

    public PushNotificationService getInstance() {
        return instance;
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
