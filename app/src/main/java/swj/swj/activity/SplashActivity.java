package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;
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
            goToActivity(HomeActivity.class);
        } else {
            goToActivity(LoginActivity.class);
        }
    }
}
