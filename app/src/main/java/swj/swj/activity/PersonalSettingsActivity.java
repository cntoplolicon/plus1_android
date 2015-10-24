package swj.swj.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;


/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends BaseActivity {

    @Bind(R.id.sb_notification)
    SwitchButton switchButton;
    @Bind(R.id.tv_nickname)
    TextView tvNickName;
    @Bind(R.id.tv_personal_profiles)
    TextView tvPersonalProfile;
    @Bind(R.id.tv_feedbacks)
    TextView tvFeedbacks;
    @Bind(R.id.tv_cache_size)
    TextView tvCacheSize;

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

    @OnClick(R.id.tv_clear_cache)
    public void clearCache(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        Window window = alertDialog.getWindow();
        alertDialog.show();
        window.setContentView(R.layout.cache_clear_dialog);
        TextView tvConfirm = (TextView) window.findViewById(R.id.tv_confirms);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long size = 0;
                File[] cachedFiles = getCacheDir().listFiles();
                for (File file : cachedFiles) {
                    size += (file.delete() ? 0 : file.length());
                }
                size = size / 1024 / 1024;
                tvCacheSize.setText(size + "M");
                alertDialog.cancel();
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
        tvPersonalProfile.setOnClickListener(new ActivityHyperlinkClickListener(this, PersonalProfileActivity.class));
        tvFeedbacks.setOnClickListener(new ActivityHyperlinkClickListener(this, FeedbackActivity.class));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalUserInfo.getPreferences().edit().putBoolean("notification_enabled", isChecked).commit();
            }
        });

        long size = 0;
        File[] cachedFiles = getCacheDir().listFiles();
        for (File file : cachedFiles) {
            size += file.length();
        }
        size = size / 1024 / 1024;
        tvCacheSize.setText(size + "M");
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
        switchButton.setChecked(LocalUserInfo.getPreferences().getBoolean("notification_enabled", true));
    }
}
