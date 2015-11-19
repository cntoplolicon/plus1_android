package com.oneplusapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
                                signInSuccessfully(response.toString());
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

    private void signInSuccessfully(String response) {
        User.updateCurrentUser(response);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
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
                loginViaOauth(SHARE_MEDIA.QQ);
                break;
            case R.id.iv_sina:
                loginViaOauth(SHARE_MEDIA.SINA);
                break;
            case R.id.iv_weixin:
                loginViaOauth(SHARE_MEDIA.WEIXIN);
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

    public void loginViaOauth(SHARE_MEDIA platform) {
        mController.doOauthVerify(this, platform, new SocializeListeners.UMAuthListener() {

            private ProgressDialog dialog;

            @Override
            public void onStart(SHARE_MEDIA platform) {
                dialog = ProgressDialog.show(LoginActivity.this, "获取授权", "正在处理");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                dialog.dismiss();
            }

            @Override
            public void onComplete(final Bundle bundle, final SHARE_MEDIA platform) {
                mController.getPlatformInfo(LoginActivity.this, platform, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                        // do nothing
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> oauthInfo) {
                        if (status != 200 && oauthInfo == null) {
                            Toast.makeText(LoginActivity.this, "获取第三方账号信息失败", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Map<String, Object> signInInfo = getLoginInfoFromOauthInfo(
                                platform.toString(), bundle, oauthInfo);
                        Log.d("normalized oauth info", signInInfo.toString());
                        RestClient.getInstance().signInViaOauth(signInInfo).done(new DoneCallback<JSONObject>() {
                            @Override
                            public void onDone(JSONObject response) {
                                signInSuccessfully(response.toString());
                            }
                        }).fail(new JsonErrorListener(getApplicationContext(), null)).always(new AlwaysCallback<JSONObject, VolleyError>() {
                            @Override
                            public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    private Map<String, Object> getLoginInfoFromOauthInfo(String platform, Bundle bundle, Map<String, Object> oauthInfo) {
        Map<String, Object> loginInfo = new HashMap<>();

        loginInfo.put("platform", platform);
        if (oauthInfo.containsKey("nickname")) {
            loginInfo.put("nickname", oauthInfo.get("nickname"));
        } else {
            loginInfo.put("nickname", oauthInfo.get("screen_name"));
        }

        String uidFromBundle = bundle.getString("uid");
        if (uidFromBundle != null) {
            loginInfo.put("uid", uidFromBundle);
        } else {
            if (oauthInfo.containsKey("uid")) {
                loginInfo.put("uid", oauthInfo.get("uid").toString());
            } else if (oauthInfo.containsKey("unionid")) {
                loginInfo.put("uid", oauthInfo.get("unionid").toString());
            }
        }

        int gender = User.GENDER_UNKNOWN;
        if (oauthInfo.containsKey("sex")) {
            gender = (Integer) oauthInfo.get("sex");
        } else if (oauthInfo.containsKey("gender")) {
            Object genderObj = oauthInfo.get("gender");
            if (genderObj instanceof Integer) {
                gender = (Integer) genderObj;
            } else if (genderObj instanceof String) {
                String genderString = (String) genderObj;
                if ("\u7537".equals(genderString)) {
                    gender = User.GENDER_MALE;
                } else if ("\u5973".equals(genderString)) {
                    gender = User.GENDER_FEMALE;
                }
            }
        }
        loginInfo.put("gender", gender);

        if (oauthInfo.containsKey("headimgurl")) {
            loginInfo.put("avatar", oauthInfo.get("headimgurl"));
        } else if (oauthInfo.containsKey("profile_image_url")) {
            loginInfo.put("avatar", oauthInfo.get("profile_image_url"));
        }

        return loginInfo;
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
