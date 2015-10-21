package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;
import swj.swj.view.SlideSwitch;


/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity implements SlideSwitch.SlideListener {

    private SlideSwitch switch_btn;

    @Bind(R.id.tv_nickname)
    TextView tvNickName;

    @OnClick(R.id.tv_personal_profiles)
    public void submit() {
        startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
    }

    @OnClick(R.id.tv_idea)
    public void idea() {
        startActivity(new Intent(PersonalSettingsActivity.this, FeedbackActivity.class));
    }

    @OnClick(R.id.btn_logout)
    public void logout(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        Window window = alertDialog.getWindow();
        alertDialog.show();
        window.setContentView(R.layout.log_out_dialog);
        TextView tvConfirm = (TextView) window.findViewById(R.id.tv_confirms);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestClient.getInstance().signOut().fail(new JsonErrorListener(getApplicationContext(), null));
                CommonMethods.clientSideSignOut(PersonalSettingsActivity.this);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        switch_btn = (SlideSwitch) findViewById(R.id.switch_btn);
        switch_btn.setSlideListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
        switch_btn.setState(LocalUserInfo.getPreferences().getBoolean("notification_enabled", true));
    }

    @Override
    public void open() {
        LocalUserInfo.getPreferences().edit().putBoolean("notification_enabled", true).commit();
    }

    @Override
    public void close() {
        LocalUserInfo.getPreferences().edit().putBoolean("notification_enabled", false).commit();
    }
}
