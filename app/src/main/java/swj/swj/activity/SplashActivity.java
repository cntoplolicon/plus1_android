package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.danlew.android.joda.JodaTimeAndroid;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class SplashActivity extends AppCompatActivity {

    private boolean isCreatingTempUser;

    private void goToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        JodaTimeAndroid.init(getApplicationContext());
        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalUserInfo userInfo = LocalUserInfo.getInstance();
        String userJson = userInfo.getUserInfo(User.CURRENT_USER_KEY);
        Log.d("user_json", userJson);

        Response.Listener<JSONObject> onSuccess = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                User.updateCurrentUser(response.toString());
                isCreatingTempUser = false;
                goToHomeActivity();
            }
        };

        Response.ErrorListener onError = new JsonErrorListener(getApplicationContext(), null) {
            @Override
            public void onErrorResponse(VolleyError error) {
                isCreatingTempUser = false;
                super.onErrorResponse(error);
            }
        };
        if (!userJson.isEmpty()) {
            User.updateCurrentUser(userJson);
            goToHomeActivity();
        } else if (!isCreatingTempUser) {
            isCreatingTempUser = true;
            RestClient.getInstance().newTempUser(onSuccess, onError);
        }
    }
}
