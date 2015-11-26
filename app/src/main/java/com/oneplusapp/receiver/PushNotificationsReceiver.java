package com.oneplusapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.model.Notification;

import java.lang.reflect.Type;
import java.util.Map;

public class PushNotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("com.oneplusapp.NOTIFICATION")) {
            return;
        }
        String json = intent.getExtras().getString("com.avos.avoscloud.Data");
        if (json == null) {
            return;
        }
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = CommonMethods.createDefaultGson().fromJson(json, type);
        String contentJson = map.get("content");
        Gson gson = CommonMethods.defaultGsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Notification notification = gson.fromJson(contentJson, Notification.class);
        if (notification.getType().equals(Notification.TYPE_COMMENT) || notification.getType().equals(Notification.TYPE_RECOMMEND)) {
            PushNotificationService.getInstance().handleNotification(notification);
        }
    }
}
