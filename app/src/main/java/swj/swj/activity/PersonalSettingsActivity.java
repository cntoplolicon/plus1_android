package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
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

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    public static final int NOTIFICATION_ENABLED = 1;
    public static final int NOTIFICATION_DISABLED = 0;

    @Bind(R.id.switch_notification)
    Switch switchNotification;

    @Bind(R.id.tv_personal_settings_nickname)
    TextView tvNickName;

    @OnClick(R.id.tv_personal_settings_nickname)
    public void submit() {
        startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
    }

    @OnClick(R.id.tv_idea)
    public void idea() {
        startActivity(new Intent(PersonalSettingsActivity.this, FeedbackActivity.class));
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        new AlertDialog.Builder(PersonalSettingsActivity.this)
                .setMessage(getResources().getString(R.string.log_out_confirm))
                .setPositiveButton(getResources().getString(R.string.submit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RestClient.getInstance().signOut().fail(new JsonErrorListener(getApplicationContext(), null));
                                CommonMethods.clientSideSignOut(PersonalSettingsActivity.this);
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                .create().show();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalUserInfo.getInstance().setPreference("notification_switch", (isChecked ? NOTIFICATION_ENABLED : NOTIFICATION_DISABLED));
            }
        });
    }

    public void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
        switchNotification.setChecked(LocalUserInfo.getInstance().getPreference("notification_switch") == NOTIFICATION_ENABLED);
    }
}
