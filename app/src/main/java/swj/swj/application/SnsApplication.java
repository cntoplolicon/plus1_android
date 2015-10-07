package swj.swj.application;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import net.danlew.android.joda.JodaTimeAndroid;

import swj.swj.BuildConfig;
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
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 1);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (BuildConfig.DEBUG) {
            config.writeDebugLogs(); // Remove for release app
        }

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }


    public static String getImageServerUrl() {
        return imageHost;
    }

    public static void setImageServerUrl(String imageServerUrl) {
        imageHost = imageServerUrl;
    }

}
