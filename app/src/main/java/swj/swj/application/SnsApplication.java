package swj.swj.application;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;

/**
 * Created by cntoplolicon on 9/15/15.
 */
public class SnsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
    }
}
