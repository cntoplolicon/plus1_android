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
import org.json.JSONObject;

import swj.swj.BuildConfig;
import swj.swj.adapter.InfectionsAdapter;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.PushNotificationService;
import swj.swj.common.RestClient;
import swj.swj.model.AppInfo;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 9/15/15.
 */
public class SnsApplication extends Application {

    private static String DEFAULT_IMAGE_HOST = "http://infection-development.s3-website.cn-north-1.amazonaws.com.cn/";
    private static AppInfo appInfo;

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
        PushNotificationService.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
        InfectionsAdapter.initialize(getApplicationContext());
        ActiveAndroid.initialize(getApplicationContext());
        loadCurrentUser();
        loadAppInfo();
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

    public static String getImageServerUrl() {
        return appInfo.getImageHosts()[0];
    }

    private void loadCurrentUser() {
        String userJson = LocalUserInfo.getPreferences().getString(User.CURRENT_USER_KEY, "");
        Log.d("user_json", userJson);
        if (!userJson.isEmpty()) {
            User.updateCurrentUser(userJson);
        }
    }

    private void loadAppInfo() {
        appInfo = new AppInfo();
        appInfo.setApiVersion(BuildConfig.VERSION_NAME);
        String imageHost = LocalUserInfo.getPreferences().getString("image_host", "");
        if (imageHost.isEmpty()) {
            imageHost = DEFAULT_IMAGE_HOST;
        }
        appInfo.setImageHosts(new String[]{imageHost});
        RestClient.getInstance().getAppInfo().done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        appInfo = CommonMethods.createDefaultGson().fromJson(response.toString(), AppInfo.class);
                        LocalUserInfo.getPreferences().edit().putString("image_host", getImageServerUrl()).commit();
                    }
                }
        ).fail(new JsonErrorListener(getApplicationContext(), null));
    }
}
