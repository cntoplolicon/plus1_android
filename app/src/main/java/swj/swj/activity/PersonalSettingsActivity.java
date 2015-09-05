package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.others.LocalUserInfo;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    private static final String TAG = "PersonalSettings";

    private Button btnLogOut;
    private TextView tv_nickname, tvPersonalProfile;
    String nickname;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initView();
    }

    private void initView() {
        tv_nickname = (TextView) findViewById(R.id.tv_personal_settings_nickname);
        tvPersonalProfile = (TextView) findViewById(R.id.tv_personal_settings_profile);
        btnLogOut = (Button) findViewById(R.id.btnLogout);
        temp_initUser();
        nickname = LocalUserInfo.getInstance(PersonalSettingsActivity.this).getUserInfo("nick_name");
        if (nickname == "") {
            tv_nickname.setText("未登陆");
            tvPersonalProfile.setTextColor(Color.GRAY);
            btnLogOut.setVisibility(View.GONE);
        } else {
            tv_nickname.setText(nickname);
            btnLogOut = (Button) findViewById(R.id.btnLogout);
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(PersonalSettingsActivity.this);
                    alertdialogbuilder.setMessage("确认退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //quit fun
                        }
                    })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog altLogOut = alertdialogbuilder.create();
                    altLogOut.show();
                }
            });

            tvPersonalProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
                }
            });
        }
    }

    private void temp_initUser() {

        String user_avatar = "avatar";
        String nickname = "andy";
        String sign = "word is big, let me see see";
        String telephone = "10000000000";
        String test_password = "11111111";

        LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("nick_name", nickname);
        LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("sign", sign);
        LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("telephone", telephone);
        LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("password", test_password);
        /*LocalUserInfo.getInstance(PersonalSettingsActivity.this).setUserInfo("avatar",
                avatar);*/
    }

    public void back(View view) {
        finish();
    }

    public void onResume() {
        super.onResume();
        String nickname_temp = LocalUserInfo.getInstance(PersonalSettingsActivity.this).getUserInfo("nick_name");
        if (!nickname_temp.equals(nickname)) {
            if (nickname_temp == "") {
                tv_nickname.setText("未设置");
            }
            tv_nickname.setText(nickname_temp);
        }
    }
}
