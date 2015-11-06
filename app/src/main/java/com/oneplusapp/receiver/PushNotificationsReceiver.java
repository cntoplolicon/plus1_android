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

/**
 * Created by cntoplolicon on 10/13/15.
 */
public class PushNotificationsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getExtras().getString("com.avos.avoscloud.Data");
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = CommonMethods.createDefaultGson().fromJson(json, type);
        String contentJson = map.get("content");
        Gson gson = CommonMethods.defaultGsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Notification notification = gson.fromJson(contentJson, Notification.class);
        PushNotificationService.getInstance().handleNotification(notification);
    }
}
