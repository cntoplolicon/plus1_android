package com.oneplusapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.CommonDialog;
import com.oneplusapp.common.UpdateChecker;

import org.jdeferred.DoneCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductInfoActivity extends BaseActivity {

    @Bind(R.id.tv_feedbacks)
    TextView tvFeedbacks;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    @Bind(R.id.tv_server_version_name)
    TextView tvServerVersionName;
    @Bind(R.id.iv_new)
    ImageView ivNew;
    @Bind(R.id.user_agreement)
    TextView tvAgreement;
    private UpdateChecker.AppReleaseReadyCallback onAppReleaseReady = new UpdateChecker.AppReleaseReadyCallback() {
        @Override
        public void onAppReleaseReady(UpdateChecker.AppRelease appRelease) {
            showUpdateInformation(appRelease);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productinfo);
        ButterKnife.bind(this);

        tvFeedbacks.setOnClickListener(new ActivityHyperlinkClickListener(this, FeedbackActivity.class));
        tvVersion.setText(getResources().getString((R.string.apk_version_name), UpdateChecker.getInstance().getCurrentVersionName(this)));
        tvAgreement.setOnClickListener(new ActivityHyperlinkClickListener(this, UserAgreementActivity.class));
        showUpdateInformation(UpdateChecker.getInstance().getAppRelease());
        UpdateChecker.getInstance().registerAppReleaseReadyCallback(onAppReleaseReady);
        UpdateChecker.getInstance().loadLatestAppRelease(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateChecker.getInstance().unregisterAppReleaseReadyCallback(onAppReleaseReady);
    }

    @OnClick(R.id.tv_update)
    public void checkUpdate(View view) {
        UpdateChecker.getInstance().loadLatestAppRelease(this).done(new DoneCallback<UpdateChecker.AppRelease>() {
            @Override
            public void onDone(UpdateChecker.AppRelease appRelease) {
                if (UpdateChecker.getInstance().getCurrentVersionCode(ProductInfoActivity.this) >= appRelease.versionCode) {
                    CommonDialog.showDialog(ProductInfoActivity.this, R.string.update_dialog);
                } else {
                    UpdateChecker.getInstance().showUpdateNotification(ProductInfoActivity.this, appRelease);
                }
            }
        });
    }

    private void showUpdateInformation(UpdateChecker.AppRelease appRelease) {
        if (appRelease == null) {
            return;
        }
        tvServerVersionName.setText(appRelease.versionName);
        if (UpdateChecker.getInstance().getCurrentVersionCode(this) >= appRelease.versionCode) {
            ivNew.setVisibility(View.GONE);
        } else {
            ivNew.setVisibility(View.VISIBLE);
        }
    }
}