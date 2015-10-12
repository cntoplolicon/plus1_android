package swj.swj.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import swj.swj.R;
import swj.swj.fragment.FriendFragment;
import swj.swj.fragment.HomeFragment;
import swj.swj.fragment.MessageFragment;
import swj.swj.fragment.MySelfFragment;
import swj.swj.fragment.PublishFragment;


public class HomeActivity extends Activity {
    @Bind(R.id.tv_page_title)
    TextView tvTitle;
    @Bind(R.id.iv_settings)
    ImageView ivSettings;
    @Bind(R.id.spb)
    SmoothProgressBar spb;

    public Promise promise;

    private static final Map<Integer, HomeActivityFragment> fragments = new HashMap<>();


    static {
        fragments.put(R.id.rb_home, new HomeActivityFragment(HomeFragment.class, R.string.home_tab));
        fragments.put(R.id.rb_friends, new HomeActivityFragment(FriendFragment.class, R.string.friends_tab));
        fragments.put(R.id.rb_publish, new HomeActivityFragment(PublishFragment.class, R.string.publish_title));
        fragments.put(R.id.rb_message, new HomeActivityFragment(MessageFragment.class, R.string.message_tab));
        fragments.put(R.id.rb_myself, new HomeActivityFragment(MySelfFragment.class, R.string.myself_tab));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        switchTab(R.id.rb_home);
        if (getIntent().getSerializableExtra("publish_class") == PublishActivity.class) {
            loadProgressBar(PublishActivity.getPromise());
        }
        if (getIntent().getSerializableExtra("publish_class") == AddTextActivity.class) {
            loadProgressBar(AddTextActivity.getPromise());
        }
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

    @OnClick({R.id.rb_home, R.id.rb_friends, R.id.rb_message, R.id.rb_publish, R.id.rb_myself})
    public void onRadioTabClicked(View view) {
        switchTab(view.getId());
    }

    @OnClick(R.id.iv_settings)
    public void onPersonalSettingsViewClicked() {
        startActivity(new Intent(this, PersonalSettingsActivity.class));
    }

    private static class HomeActivityFragment {
        private Class<?> fragment;
        private int titleTextResource;

        public HomeActivityFragment(Class<?> fragment, int titleTextResource) {
            this.fragment = fragment;
            this.titleTextResource = titleTextResource;
        }
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
}
