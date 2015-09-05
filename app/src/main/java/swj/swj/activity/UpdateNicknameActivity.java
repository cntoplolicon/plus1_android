package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.others.LocalUserInfo;

/**
 * Created by jiewei on 9/3/15.
 */
public class UpdateNicknameActivity extends Activity {

    private TextView tv_save, edit_nick;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);

        final String nick = LocalUserInfo.getInstance(UpdateNicknameActivity.this).getUserInfo("nick_name");
        edit_nick = (EditText) findViewById(R.id.et_nick);
        edit_nick.setText(nick);
        tv_save = (TextView) findViewById(R.id.tv_nickname_save);
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newNick = edit_nick.getText().toString().trim();
                if (nick.equals(newNick) || newNick.equals("")) {
                    return;
                }
                temp_updateNick(newNick);
            }
        });

    }

    public void back(View view) {
        finish();
    }

    private void temp_updateNick(final String newNick) {
        LocalUserInfo.getInstance(UpdateNicknameActivity.this).setUserInfo("nick_name", newNick);
        finish();
    }
}
