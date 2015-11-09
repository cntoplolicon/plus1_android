package com.oneplusapp.activity;

import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
    @Bind(R.id.tv_feedbacks)
    TextView tvFeedbacks;
    @Bind(R.id.tv_cache_size)
    TextView tvCacheSize;
    @Bind(R.id.tv_version_name)
    TextView tvVersionName;

    @OnClick(R.id.btn_logout)
    public void logout(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setContentView(R.layout.cache_clear_dialog);
        TextView tvConfirm = (TextView) window.findViewById(R.id.tv_confirms);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().getDiskCache().clear();
                tvCacheSize.setText(calcImageCacheSize());
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

        tvCacheSize.setText(calcImageCacheSize());
        tvVersionName.setText("版本号：" + getVersionName());
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

    private String getVersionName() {
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
