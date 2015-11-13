package com.oneplusapp.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.common.UpdateChecker;
import com.oneplusapp.fragment.HomeFragment;
import com.oneplusapp.fragment.MessageFragment;
import com.oneplusapp.fragment.MySelfFragment;
import com.oneplusapp.fragment.PublishFragment;
import com.oneplusapp.fragment.RecommendFragment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class HomeActivity extends BaseActivity {
    private PushNotificationService.Callback callback = new NotificationChanged();

    @Bind(R.id.tv_page_title)
    TextView tvTitle;
    @Bind(R.id.iv_settings)
    ImageView ivSettings;
    @Bind(R.id.spb)
    SmoothProgressBar spb;
    @Bind(R.id.rg_group)
    RadioGroup radioGroup;
    @Bind(R.id.fl_red_point)
    FrameLayout flRedPoint;

    private static final Map<Integer, HomeActivityFragment> fragments = new HashMap<>();
    private static boolean appUpdateNotified = false;
    private static boolean doubleBackToExitPressedOnce = false;

    static {
        fragments.put(R.id.rb_home, new HomeActivityFragment(HomeFragment.class, R.string.home_tab));
        fragments.put(R.id.rb_recommendation, new HomeActivityFragment(RecommendFragment.class, R.string.recommend_tab));
        fragments.put(R.id.rb_publish, new HomeActivityFragment(PublishFragment.class, R.string.publish_title));
        fragments.put(R.id.rb_message, new HomeActivityFragment(MessageFragment.class, R.string.message_tab));
        fragments.put(R.id.rb_myself, new HomeActivityFragment(MySelfFragment.class, R.string.myself_tab));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (User.current == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            switchTab(R.id.rb_home);
        }
        if (getIntent().getSerializableExtra("publish_class") == PublishActivity.class) {
            loadProgressBar(PublishActivity.getPromise());
        }
        if (getIntent().getSerializableExtra("publish_class") == AddTextActivity.class) {
            loadProgressBar(AddTextActivity.getPromise());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (R.id.rb_message == checkedId) {
                    flRedPoint.setVisibility(View.GONE);
                }
                switchTab(checkedId);
            }
        });

        PushNotificationService.getInstance().registerCallback(callback);

        if (!appUpdateNotified) {
            UpdateChecker.getInstance().loadLatestAppRelease(this).done(new DoneCallback<UpdateChecker.AppRelease>() {
                @Override
                public void onDone(UpdateChecker.AppRelease appRelease) {
                    if (UpdateChecker.getInstance().getCurrentVersionCode(HomeActivity.this) < appRelease.versionCode) {
                        UpdateChecker.getInstance().showUpdateNotification(HomeActivity.this, appRelease);
                        appUpdateNotified = true;
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushNotificationService.getInstance().unregisterCallback(callback);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void switchTab(int radioButtonId) {
        try {
            HomeActivityFragment fragment = fragments.get(radioButtonId);
            getFragmentManager().beginTransaction().replace(R.id.fl, (Fragment) fragment.fragment.newInstance()).commit();
            tvTitle.setText(fragment.titleTextResource);
            ivSettings.setVisibility(radioButtonId == R.id.rb_myself ? View.VISIBLE : View.INVISIBLE);
        } catch (IllegalAccessException e) {
            Log.e(HomeActivity.class.getName(), "failed initializing fragment", e);
        } catch (InstantiationException e) {
            Log.e(HomeActivity.class.getName(), "failed initializing fragment", e);
        }
    }

    @OnClick(R.id.iv_settings)
    public void onPersonalSettingsViewClicked() {
        startActivity(new Intent(this, PersonalSettingsActivity.class));
    }

    public void loadProgressBar(Promise<JSONObject, VolleyError, Void> promise) {
        if (promise != null) {
            spb.setVisibility(View.VISIBLE);
            promise.always(new AlwaysCallback<JSONObject, VolleyError>() {
                @Override
                public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                    spb.setVisibility(View.GONE);
                }
            });
        }
    }

    private static class HomeActivityFragment {
        private Class<?> fragment;
        private int titleTextResource;

        public HomeActivityFragment(Class<?> fragment, int titleTextResource) {
            this.fragment = fragment;
            this.titleTextResource = titleTextResource;
        }
    }

    private class NotificationChanged implements PushNotificationService.Callback {
        @Override
        public void onNotificationReceived(Notification notification) {
            if (radioGroup.getCheckedRadioButtonId() != R.id.rb_message) {
                flRedPoint.setVisibility(View.VISIBLE);
            }
        }
    }
}
