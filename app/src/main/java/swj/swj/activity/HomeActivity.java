package swj.swj.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import swj.swj.R;
import swj.swj.fragment.CameraFragment;
import swj.swj.fragment.FriendFragment;
import swj.swj.fragment.HomeFragment;
import swj.swj.fragment.MessageFragment;
import swj.swj.fragment.MySelfFragment;


public class HomeActivity extends Activity {

    RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    public void init() {
        radioButton = (RadioButton) findViewById(R.id.rb_Home);
        onHome(radioButton);
//        radioButton.setTextColor(Color.RED);
    }

    /*首页*/
    public void onHome(View view) {
        //new对象
        HomeFragment homeFragment = new HomeFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, homeFragment);
        radioButton.setTextColor(Color.RED);
        //提交事务
        ft.commit();
    }

    /*好友*/
    public void onFriends(View view) {
        //new对象
        FriendFragment friendFragment = new FriendFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, friendFragment);
        radioButton.setTextColor(Color.WHITE);
        //提交事务
        ft.commit();
    }

    /*相机*/
    public void onCamera(View view) {
        //new对象
        CameraFragment cameraFragment = new CameraFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, cameraFragment);
        radioButton.setTextColor(Color.WHITE);
        //提交事务
        ft.commit();
    }

    /*消息*/
    public void onMessage(View view) {
        //new对象
        MessageFragment messageFragment = new MessageFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, messageFragment);
        radioButton.setTextColor(Color.WHITE);
        //提交事务
        ft.commit();
    }

    /*个人*/
    public void onMySelf(View view) {
        //new对象
        MySelfFragment mySelfFragment = new MySelfFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, mySelfFragment);
        radioButton.setTextColor(Color.WHITE);
        //提交事务
        ft.commit();
    }




}
