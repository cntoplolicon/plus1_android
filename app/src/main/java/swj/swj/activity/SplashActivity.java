package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.google.gson.Gson;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class SplashActivity extends AppCompatActivity {

    private Response.Listener<JSONObject> newTempUserCallback = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
                LocalUserInfo userInfo = LocalUserInfo.getInstance();
                String json = response.toString();
                User.current = CommonMethods.createDefaultGson().fromJson(json, User.class);
                userInfo.setUserInfo("user", response.toString());
                goToHomeActivity();
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

        RestClient.initialize(getApplicationContext());
        LocalUserInfo.initialize(getApplicationContext());

        LocalUserInfo userInfo = LocalUserInfo.getInstance();

        String userJson = userInfo.getUserInfo("user");
        Log.d("user_json", userJson);
        if (userJson.isEmpty()) {
            RestClient.getInstance().newTempUser(newTempUserCallback,
                    new JsonErrorListener(getApplicationContext(), null));

        } else {
            User.current = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
            goToHomeActivity();
        }
    }
}
