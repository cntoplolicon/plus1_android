package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by syb on 2015/9/18.
 */
public class AddTextActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        TextView tv_user_text = (TextView) findViewById(R.id.tv_use_text);
        tv_user_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PublishTextActivity.class));
            }
        });
    }
}
