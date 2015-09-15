package swj.swj.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.fragment.FriendFragment;
import swj.swj.fragment.HomeFragment;
import swj.swj.fragment.MessageFragment;
import swj.swj.fragment.MySelfFragment;
import swj.swj.fragment.PushFragment;


public class HomeActivity extends Activity {

    RadioButton radioButton;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    public void init() {
        radioButton = (RadioButton) findViewById(R.id.rb_home);
        tv_title = (TextView) findViewById(R.id.tv_title);
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
        tv_title.setText(getResources().getString(R.string.home_tab));
    }

    public void onFriends(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new FriendFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText(getResources().getString(R.string.friends_tab));
    }

    /*相机*/
    public void onCamera(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new PushFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText(getResources().getString(R.string.push_title));
    }

    public void onMessage(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new MessageFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText(getResources().getString(R.string.message_tab));
    }

    public void onMySelf(View view) {
        getFragmentManager().beginTransaction().replace(R.id.fl, new MySelfFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText(getResources().getString(R.string.myself_tab));
    }


}
