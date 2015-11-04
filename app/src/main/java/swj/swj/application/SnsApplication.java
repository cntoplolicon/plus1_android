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

import swj.swj.BuildConfig;
import swj.swj.adapter.InfectionsAdapter;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.PushNotificationService;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 9/15/15.
 */
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
        PushNotificationService.init(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
        InfectionsAdapter.initialize(getApplicationContext());
        ActiveAndroid.initialize(getApplicationContext());
        loadCurrentUser();
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
