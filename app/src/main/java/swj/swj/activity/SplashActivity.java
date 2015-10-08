package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class SplashActivity extends AppCompatActivity {

    private void goToActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalUserInfo userInfo = LocalUserInfo.getInstance();
        String userJson = userInfo.getUserInfo(User.CURRENT_USER_KEY);
        Log.d("user_json", userJson);

        if (!userJson.isEmpty()) {
            User.updateCurrentUser(userJson);
            RestClient.getInstance().loadImageServerUrl().done(
                    new DoneCallback<JSONArray>() {
                        @Override
                        public void onDone(JSONArray response) {
                            SnsApplication.setImageServerUrl(response.optString(0));
                        }
                    }).fail(new JsonErrorListener(getApplicationContext(), null));
            goToActivity(HomeActivity.class);
        } else {
            goToActivity(LoginActivity.class);
        }
    }
}
