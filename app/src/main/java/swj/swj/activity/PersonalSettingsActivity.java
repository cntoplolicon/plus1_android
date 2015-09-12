package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    private static final String TAG = "PersonalSettings";

    private Button btnLogOut;
    private TextView tvNickName, tvPersonalProfile;
    private String nickname;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initView();
    }

    private void initView() {
        tvNickName = (TextView) findViewById(R.id.tv_personal_settings_nickname);
        tvPersonalProfile = (TextView) findViewById(R.id.tv_personal_settings_profile);
        btnLogOut = (Button) findViewById(R.id.btnLogout);
        nickname = LocalUserInfo.getInstance().getUserInfo("nick_name");
        Log.d(TAG, nickname);
        if (nickname == "") {
            tvNickName.setText("未登陆");
            tvPersonalProfile.setTextColor(Color.GRAY);
            btnLogOut.setVisibility(View.GONE);
            tvNickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    startActivity(new Intent(PersonalSettingsActivity.this,LoginActivity.class));
                }
            });
        } else {
            tvNickName.setText(nickname);
            btnLogOut = (Button) findViewById(R.id.btnLogout);
            btnLogOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(PersonalSettingsActivity.this);
                    alertdialogbuilder.setMessage("确认退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            LocalUserInfo.getInstance().setUserInfo("nick_name","");
                            PersonalSettingsActivity.this.finish();
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

    public void back(View view) {
        finish();
    }

    public void onResume() {
        super.onResume();
        String nickname_temp = LocalUserInfo.getInstance().getUserInfo("nick_name");
        if (!nickname_temp.equals(nickname)) {
            if (nickname_temp == "") {
                tvNickName.setText("未设置");
            }
            tvNickName.setText(nickname_temp);
        }
    }
}
