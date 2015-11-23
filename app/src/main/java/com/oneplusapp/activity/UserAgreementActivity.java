package com.oneplusapp.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.oneplusapp.R;
import com.oneplusapp.common.RestClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UserAgreementActivity extends BaseActivity {
    @Bind(R.id.web_view)
    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        ButterKnife.bind(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(RestClient.getInstance().buildAgreementUrl());
    }
}
