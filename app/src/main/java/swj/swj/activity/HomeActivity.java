package swj.swj.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.fragment.CameraFragment;
import swj.swj.fragment.FriendFragment;
import swj.swj.fragment.HomeFragment;
import swj.swj.fragment.MessageFragment;
import swj.swj.fragment.MySelfFragment;


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
        radioButton = (RadioButton) findViewById(R.id.rb_Home);
        tv_title = (TextView) findViewById(R.id.tv_title);
        onHome(radioButton);
//        radioButton.setTextColor(Color.RED);
    }

    /*首页*/
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
        tv_title.setText("LOGO");
    }

    /*好友*/
    public void onFriends(View view) {
      /*  //new对象
        FriendFragment friendFragment = new FriendFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, friendFragment);
        //提交事务
        ft.commit();*/
        getFragmentManager().beginTransaction().replace(R.id.fl, new FriendFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText("朋友");
    }

    /*相机*/
    public void onCamera(View view) {
        /*//new对象
        CameraFragment cameraFragment = new CameraFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, cameraFragment);
        //提交事务
        ft.commit();*/
        getFragmentManager().beginTransaction().replace(R.id.fl, new CameraFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText("相机");
    }

    /*消息*/
    public void onMessage(View view) {
      /*  //new对象
        MessageFragment messageFragment = new MessageFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, messageFragment);
        //提交事务
        ft.commit();*/
        getFragmentManager().beginTransaction().replace(R.id.fl, new MessageFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText("消息");
    }

    /*个人*/
    public void onMySelf(View view) {
     /*   //new对象
        MySelfFragment mySelfFragment = new MySelfFragment();
        //获取管理器
        FragmentManager fm = getFragmentManager();
        //开启事务
        FragmentTransaction ft = fm.beginTransaction();
        //设置显示
        ft.replace(R.id.fl, mySelfFragment);
        //提交事务
        ft.commit();*/
        getFragmentManager().beginTransaction().replace(R.id.fl, new MySelfFragment()).commit();
        radioButton.setTextColor(Color.WHITE);
        tv_title.setText("个人");
    }


}
