package swj.swj.activity;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2015/10/24.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
