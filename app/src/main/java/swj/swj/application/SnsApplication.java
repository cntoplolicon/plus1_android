package swj.swj.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.danlew.android.joda.JodaTimeAndroid;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import swj.swj.BuildConfig;
import swj.swj.adapter.InfectionsAdapter;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.PushNotificationService;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 9/15/15.
 */
public class SnsApplication extends Application {

    private static String DEFAULT_IMAGE_HOST = "http://infection-development.s3-website.cn-north-1.amazonaws.com.cn/";
    private static String imageHost;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        PushNotificationService.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
        InfectionsAdapter.initialize(getApplicationContext());
        ActiveAndroid.initialize(getApplicationContext());
        loadCurrentUser();
        loadImageHosts();
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                .memoryCacheSizePercentage(20)
                .defaultDisplayImageOptions(displayOptions);
        if (BuildConfig.DEBUG) {
            config.writeDebugLogs();
        }
        ImageLoader.getInstance().init(config.build());
    }

    public static String getImageServerUrl() {
        if (imageHost != null && !imageHost.isEmpty()) {
            return imageHost;
        }
        imageHost = LocalUserInfo.getInstance().getUserInfo("image_host");
        if (imageHost != null && !imageHost.isEmpty()) {
            return imageHost;
        }
        return DEFAULT_IMAGE_HOST;
    }

    private static void setImageServerUrl(String imageServerUrl) {
        imageHost = imageServerUrl;
        LocalUserInfo.getInstance().setUserInfo("image_host", imageHost);
    }

    private void loadCurrentUser() {
        LocalUserInfo userInfo = LocalUserInfo.getInstance();
        String userJson = userInfo.getUserInfo(User.CURRENT_USER_KEY);
        Log.d("user_json", userJson);
        if (userJson != null) {
            User.updateCurrentUser(userJson);
        }
    }

    private void loadImageHosts() {
        RestClient.getInstance().loadImageServerUrl().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        SnsApplication.setImageServerUrl(response.optString(0));
                    }

                }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

}
