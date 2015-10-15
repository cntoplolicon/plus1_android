package swj.swj.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.yunba.android.manager.YunBaManager;
import swj.swj.common.PushNotificationService;

/**
 * Created by cntoplolicon on 10/13/15.
 */
public class PushNotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
            return;
        }

        String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
        String message = intent.getStringExtra(YunBaManager.MQTT_MSG);
        StringBuilder logMessage = new StringBuilder()
                .append("Received message from server: ")
                .append(YunBaManager.MQTT_TOPIC)
                .append(" = ")
                .append(topic)
                .append(" ")
                .append(YunBaManager.MQTT_MSG)
                .append(" = ")
                .append(message);
        Log.d(PushNotificationService.class.getName(), logMessage.toString());

        PushNotificationService.getInstance().handleNotification(topic, message);
    }
}
