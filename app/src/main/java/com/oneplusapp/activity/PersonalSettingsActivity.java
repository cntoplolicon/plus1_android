package com.oneplusapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.LocalUserInfo;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;
import com.oneplusapp.view.ConfirmAlertDialog;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


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
    @Bind(R.id.tv_cache_size)
    TextView tvCacheSize;
    @Bind(R.id.tv_about)
    TextView tvAbout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
        tvPersonalProfile.setOnClickListener(new ActivityHyperlinkClickListener(this, PersonalProfileActivity.class));
        tvAbout.setOnClickListener(new ActivityHyperlinkClickListener(this, ProductInfoActivity.class));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalUserInfo.getPreferences().edit().putBoolean("notification_enabled", isChecked).commit();
            }
        });
        tvCacheSize.setText(calcImageCacheSize());
    }

    @OnClick(R.id.btn_logout)
    public void logout(View view) {
        ConfirmAlertDialog confirmAlertDialog = new ConfirmAlertDialog(PersonalSettingsActivity.this);
        confirmAlertDialog.show();
        confirmAlertDialog.getTvConfirm().setText(R.string.log_out_confirm);
        confirmAlertDialog.getTvConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestClient.getInstance().signOut().fail(new JsonErrorListener(getApplicationContext(), null));
                CommonMethods.clientSideSignOut(PersonalSettingsActivity.this);
            }
        });
    }

    @OnClick(R.id.tv_clear_cache)
    public void clearCache(View view) {
        final ConfirmAlertDialog confirmAlertDialog = new ConfirmAlertDialog(PersonalSettingsActivity.this);
        confirmAlertDialog.show();
        confirmAlertDialog.getTvConfirm().setText(R.string.clear_cache_confirm);
        confirmAlertDialog.getTvConfirm().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().getDiskCache().clear();
                tvCacheSize.setText(calcImageCacheSize());
                confirmAlertDialog.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
        switchButton.setChecked(LocalUserInfo.getPreferences().getBoolean("notification_enabled", true));
    }

    private String calcImageCacheSize() {
        File[] imageCacheFiles = ImageLoader.getInstance().getDiskCache().getDirectory().listFiles();
        long size = 0;
        for (File file : imageCacheFiles) {
            size += file.length();
        }
        return size / 1024 / 1024 + "M";
    }
}
