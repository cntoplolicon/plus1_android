package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.common.ActivityHyperlinkClickListener;

/**
 * Created by syb on 2015/9/18.
 */
public class AddTextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        TextView useText = (TextView) findViewById(R.id.tv_use_text);
        useText.setOnClickListener(new ActivityHyperlinkClickListener(this, PublishTextActivity.class));
    }
}
