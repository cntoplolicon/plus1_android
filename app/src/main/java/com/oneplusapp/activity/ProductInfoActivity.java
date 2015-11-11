package com.oneplusapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.UpdateChecker;

import org.jdeferred.DoneCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/11/11.
 */
public class ProductInfoActivity extends BaseActivity {

    @Bind(R.id.tv_feedbacks)
    TextView tvFeedbacks;
    @Bind(R.id.tv_version)
    TextView tvVersion;
//    @Bind(R.id.tv_versioncode)
//    TextView tvVersionCode;
    @Bind(R.id.iv_new)
    ImageView ivNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productinfo);
        ButterKnife.bind(this);
        tvFeedbacks.setOnClickListener(new ActivityHyperlinkClickListener(this, FeedbackActivity.class));
        tvVersion.setText(getResources().getString(R.string.apk_version_name) + UpdateChecker.getInstance().getCurrentVersionName(this));

        UpdateChecker.getInstance().getAppRelease(this).done(new DoneCallback<UpdateChecker.AppRelease>() {
            @Override
            public void onDone(UpdateChecker.AppRelease appRelease) {
                showUpdateInformation(appRelease);
            }
        });
    }

    @OnClick(R.id.tv_update)
    public void checkUpdate(View view) {
        UpdateChecker.getInstance().getAppRelease(this).done(new DoneCallback<UpdateChecker.AppRelease>() {
            @Override
            public void onDone(UpdateChecker.AppRelease appRelease) {
                UpdateChecker.getInstance().showUpdateNotification(ProductInfoActivity.this, appRelease);
            }
        });
    }

    public void showUpdateInformation(UpdateChecker.AppRelease appRelease) {
        if (UpdateChecker.getInstance().getCurrentVersionCode(this) >= appRelease.versionCode) {
            ivNew.setVisibility(View.GONE);
        } else {
            ivNew.setVisibility(View.VISIBLE);
        }
    }
}
