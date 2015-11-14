package com.oneplusapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.CommonDialog;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private UMSocialService mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104950070", "qBwAJcdsf58q3PNf");
        UMWXHandler wxHandler = new UMWXHandler(this, "wx8722fc0d2579fb13", "d4624c36b6795d1d99dcf0547af5443d");
        qqSsoHandler.addToSocialSDK();
        wxHandler.addToSocialSDK();
        wxHandler.setRefreshTokenAvailable(false);

        Button loginSubmit = (Button) findViewById(R.id.btn_submit);
        usernameInput = (EditText) findViewById(R.id.et_username);
        passwordInput = (EditText) findViewById(R.id.et_password);

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputValidation()) {
                    return;
                }
                v.setEnabled(false);
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                RestClient.getInstance().signIn(username, password).done(
                        new DoneCallback<JSONObject>() {
                            @Override
                            public void onDone(JSONObject response) {
                                User.updateCurrentUser(response.toString());
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).fail(
                        new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject errors) {
                                CommonMethods.showError(LoginActivity.this, errors, "username");
                                CommonMethods.showError(LoginActivity.this, errors, "password");
                            }
                        })).always(new ResetViewClickable<JSONObject, VolleyError>(v));
            }
        });

        TextView toForgetPwd = (TextView) findViewById(R.id.tv_to_reset_pwd);
        toForgetPwd.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPwdStepOne.class));

        TextView toRegister = (TextView) findViewById(R.id.tv_to_register);
        toRegister.setOnClickListener(new ActivityHyperlinkClickListener(this, RegisterStepOne.class));
        showGuideOnFirstLogin();
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString().trim())) {
            CommonDialog.showDialog(this, R.string.username_invalid_format);
            return false;
        } else if (!CommonMethods.isValidPwd(passwordInput.getText().toString().trim())) {
            CommonDialog.showDialog(this, R.string.password_invalid_format);
            return false;
        }
        return true;
    }

    @OnClick({R.id.iv_qq, R.id.iv_sina, R.id.iv_weixin})
    public void login(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_qq:
                loginOauth(SHARE_MEDIA.QQ);
                break;
            case R.id.iv_sina:
                loginOauth(SHARE_MEDIA.SINA);
                break;
            case R.id.iv_weixin:
                loginOauth(SHARE_MEDIA.WEIXIN);
                break;
        }
    }

    private void showGuideOnFirstLogin() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("config",
                Context.MODE_PRIVATE);
        boolean isGuideShowed = sharedPreferences.getBoolean("is_guide_showed",
                false);
        if (!isGuideShowed) {
            startActivity(new Intent(getApplicationContext(),
                    GuideActivity.class));
            finish();
        }
    }

    public void loginOauth(SHARE_MEDIA platform) {
        mController.doOauthVerify(this, platform, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                    String openid = value.getString("uid");
                    Log.d("QQuid", openid);
                }
                mController.getPlatformInfo(LoginActivity.this, platform, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        Toast.makeText(LoginActivity.this, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        if (status == 200 && info != null) {
                            StringBuilder sb = new StringBuilder();
                            Set<String> keys = info.keySet();
                            for (String key : keys) {
                                sb.append(key + "=" + info.get(key).toString() + "\r\n");
                            }
                            Toast.makeText(LoginActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("ResultData", sb.toString());
                        } else {
                            Log.d("ResultData", "发生错误：" + status);
                        }
                    }
                });
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logoutOauth(SHARE_MEDIA platform) {
        mController.deleteOauth(this, platform, new SocializeListeners.SocializeClientListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                if (status == 200) {
                    Toast.makeText(LoginActivity.this, "删除成功.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
