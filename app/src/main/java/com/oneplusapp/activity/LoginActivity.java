package com.oneplusapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_username)
    EditText usernameInput;
    @Bind(R.id.et_password)
    EditText passwordInput;

    private UMSocialService umSocialService;
    private ProgressDialog authDialog;
    private ProgressDialog platformInfoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        umSocialService = UMServiceFactory.getUMSocialService("com.umeng.login");
        umSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, "1104950070", "qBwAJcdsf58q3PNf");
        UMWXHandler wxHandler = new UMWXHandler(this, "wx8722fc0d2579fb13", "d4624c36b6795d1d99dcf0547af5443d");
        qqSsoHandler.addToSocialSDK();
        wxHandler.addToSocialSDK();
        wxHandler.setRefreshTokenAvailable(false);

        TextView toForgetPwd = (TextView) findViewById(R.id.tv_to_reset_pwd);
        toForgetPwd.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPwdStepOne.class));

        TextView toRegister = (TextView) findViewById(R.id.tv_to_register);
        toRegister.setOnClickListener(new ActivityHyperlinkClickListener(this, RegisterStepOne.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        final ProgressDialog dialog = authDialog;
        // sdk bug: onCancel is not called when the user does not sign in in the wechat client
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 2000);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit(View view) {
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
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
                })).always(new ResetViewClickable<JSONObject, VolleyError>(view));
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

    public void loginViaOauth(SHARE_MEDIA platform) {
        umSocialService.doOauthVerify(this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                if (authDialog != null) {
                    authDialog.dismiss();
                }
                authDialog = new ProgressDialog(LoginActivity.this);
                authDialog.setMessage(getResources().getString(R.string.oauth_authenticate));
                authDialog.show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                authDialog.dismiss();
                Toast.makeText(LoginActivity.this, R.string.oauth_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                authDialog.dismiss();
            }

            @Override
            public void onComplete(final Bundle bundle, final SHARE_MEDIA platform) {
                authDialog.dismiss();
                platformInfoDialog = new ProgressDialog(LoginActivity.this);
                platformInfoDialog.setMessage(getResources().getString(R.string.oauth_get_platform_info));
                platformInfoDialog.show();

                umSocialService.getPlatformInfo(LoginActivity.this, platform, new SocializeListeners.UMDataListener() {

                    @Override
                    public void onStart() {
                        // onStart is not called at all when signing in with wechat
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> oauthInfo) {
                        if (status != 200 && oauthInfo == null) {
                            platformInfoDialog.dismiss();
                            Toast.makeText(LoginActivity.this, R.string.oauth_platform_info_error, Toast.LENGTH_LONG).show();
                            return;
                        }
                        Map<String, Object> signInInfo = getLoginInfoFromOauthInfo(
                                platform.toString(), bundle, oauthInfo);
                        RestClient.getInstance().signInViaOauth(signInInfo).done(
                                new DoneCallback<JSONObject>() {
                                    @Override
                                    public void onDone(JSONObject response) {
                                        signInSuccessfully(response.toString());
                                    }
                                }).fail(new JsonErrorListener(getApplicationContext(), null))
                                .always(new AlwaysCallback<JSONObject, VolleyError>() {
                                    @Override
                                    public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                                        platformInfoDialog.dismiss();
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
        UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
