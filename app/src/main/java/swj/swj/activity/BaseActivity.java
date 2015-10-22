package swj.swj.activity;

import android.app.Activity;

/**
 * Created by Administrator on 2015/10/22.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPause(this);
    }
}
