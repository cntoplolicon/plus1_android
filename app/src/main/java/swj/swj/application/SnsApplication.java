package swj.swj.application;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.HashSet;
import java.util.Set;

import swj.swj.BuildConfig;
import swj.swj.R;
import swj.swj.adapter.HomePageListItemViewsAdapter;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;

/**
 * Created by cntoplolicon on 9/15/15.
 */
public class SnsApplication extends Application {

    private static String imageHost;

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
        HomePageListItemViewsAdapter.initialize(getApplicationContext());
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
        return imageHost.isEmpty() ? LocalUserInfo.getInstance().getUserInfo("image_host") : imageHost;
    }

    public static void setImageServerUrl(String imageServerUrl) {
        imageHost = imageServerUrl;
        LocalUserInfo.getInstance().setUserInfo("image_host", imageHost);
    }

}
