package swj.swj.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.fragment.FriendFragment;
import swj.swj.fragment.HomeFragment;
import swj.swj.fragment.MessageFragment;
import swj.swj.fragment.MySelfFragment;
import swj.swj.fragment.PublishFragment;


public class HomeActivity extends Activity {
    RadioButton radioButton;
    TextView tvTitle, tvPersonalSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    public void init() {
        radioButton = (RadioButton) findViewById(R.id.rb_home);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvPersonalSettings = (TextView) findViewById(R.id.personal_settings_tv);
        onHome(radioButton);
    }

    public void onHome(View view) {
       /* //new对象
        HomeFragment homeFragment = new HomeFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, homeFragment);
        radioButton.setTextColor(Color.RED);
        //提交事务
        ft.commit();*/
        getFragmentManager().beginTransaction().replace(R.id.fl, new HomeFragment()).commit();
        radioButton.setTextColor(Color.RED);
        tvTitle.setText(getResources().getString(R.string.home_tab));
        tvPersonalSettings.setVisibility(View.INVISIBLE);
    }

    public void onFriends(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new FriendFragment()).commit();
        tvTitle.setText(getResources().getString(R.string.friends_tab));
        tvPersonalSettings.setVisibility(View.INVISIBLE);
    }

    public void onPublish(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new PublishFragment()).commit();
        tvTitle.setText(getResources().getString(R.string.publish_title));
        tvPersonalSettings.setVisibility(View.INVISIBLE);
    }

    public void onMessage(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new MessageFragment()).commit();
        tvTitle.setText(getResources().getString(R.string.message_tab));
        tvPersonalSettings.setVisibility(View.INVISIBLE);
    }

    public void onMySelf(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new MySelfFragment()).commit();
        tvTitle.setText(getResources().getString(R.string.myself_tab));
        tvPersonalSettings.setVisibility(View.VISIBLE);
    }
}
