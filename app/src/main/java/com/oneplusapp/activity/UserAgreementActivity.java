package com.oneplusapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.oneplusapp.R;
import com.oneplusapp.common.RestClient;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/19.
 */
public class UserAgreementActivity extends Activity {
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
