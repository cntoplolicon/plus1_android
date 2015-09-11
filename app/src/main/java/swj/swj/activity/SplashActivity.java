package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;

public class SplashActivity extends AppCompatActivity {

    private Response.Listener<JSONObject> newTempUserCallback = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {
                LocalUserInfo userInfo = LocalUserInfo.getInstance(getBaseContext());
                int userId = response.getInt("id");
                String accessToken = response.getString("access_token");
                userInfo.setUserInfo(LocalUserInfo.KEY_USER_ID, String.valueOf(userId));
                userInfo.setUserInfo(LocalUserInfo.KEY_ACCESS_TOKEN, accessToken);
                goToHomeActivity();
            } catch (JSONException e) {
                Log.e(SplashActivity.class.getName(), "incorrect response format", e);
            }
        }
    };

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LocalUserInfo userInfo = LocalUserInfo.getInstance(getApplicationContext());
        Log.d(this.getClass().getName(), "user_id: " + userInfo.getUserId());
        Log.d(this.getClass().getName(), "access_token: " + userInfo.getAccessToken());
        if (userInfo.getUserId().isEmpty() || userInfo.getAccessToken().isEmpty()) {
            RestClient.getInstance(getApplicationContext()).newTempUser(newTempUserCallback, null);
        } else {
            goToHomeActivity();
        }
    }
}
