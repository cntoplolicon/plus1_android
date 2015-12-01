package com.oneplusapp.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oneplusapp.BuildConfig;
import com.oneplusapp.common.EventChecker;
import com.oneplusapp.common.LocalUserInfo;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;

import net.danlew.android.joda.JodaTimeAndroid;

public class SnsApplication extends Application {
    public static final DisplayImageOptions DEFAULT_DISPLAY_OPTION = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .resetViewBeforeLoading(true)
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
        ActiveAndroid.initialize(getApplicationContext());
        initLeanCloud(getApplicationContext());
        // push notification must be initialized after lean count and before current user
        PushNotificationService.init(getApplicationContext());
        EventChecker.initialize(getApplicationContext());
        loadCurrentUser();
    }

    private void initLeanCloud(Context context) {
        AVOSCloud.initialize(context, "uRHAcbn8PbOthR28S6hd6ArI", "HQmVcG0WJ67OiCcV2OnhiBew");
        if (BuildConfig.DEBUG) {
            AVOSCloud.setDebugLogEnabled(true);
        }
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCacheSizePercentage(20)
                .defaultDisplayImageOptions(DEFAULT_DISPLAY_OPTION);
        if (BuildConfig.DEBUG) {
            config.writeDebugLogs();
        }
        ImageLoader.getInstance().init(config.build());
    }

    private void loadCurrentUser() {
        String userJson = LocalUserInfo.getPreferences().getString(User.CURRENT_USER_KEY, "");
        Log.d("user_json", userJson);
        if (!userJson.isEmpty()) {
            User.updateCurrentUser(userJson);
        }
    }
}
