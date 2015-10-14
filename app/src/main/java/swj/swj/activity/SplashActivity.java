package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.util.SQLiteUtils;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.List;

import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.PushNotificationService;
import swj.swj.common.RestClient;
import swj.swj.model.Notification;
import swj.swj.model.User;

public class SplashActivity extends AppCompatActivity {

    private void goToActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        PushNotificationService.copyNotification(getIntent(), intent);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Notification notification = getIntent().getParcelableExtra("notification");
        if (notification != null && User.current != null
                && notification.getType() == PushNotificationService.TYPE_COMMENT) {
            goToActivity(CardDetailsActivity.class);
            return;
        }

        setContentView(R.layout.activity_splash);
        LocalUserInfo userInfo = LocalUserInfo.getInstance();
        String userJson = userInfo.getUserInfo(User.CURRENT_USER_KEY);
        Log.d("user_json", userJson);

        RestClient.getInstance().loadImageServerUrl().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        SnsApplication.setImageServerUrl(response.optString(0));
                    }

                }).fail(new JsonErrorListener(getApplicationContext(), null));

        if (!userJson.isEmpty()) {
            User.updateCurrentUser(userJson);
            goToActivity(HomeActivity.class);
        } else {
            goToActivity(LoginActivity.class);
        }
    }
}
