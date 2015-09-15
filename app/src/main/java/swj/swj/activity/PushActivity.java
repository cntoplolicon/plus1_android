package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import swj.swj.R;

/**
 * Created by syb on 2015/9/12.
 */
public class PushActivity extends Activity {
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SecondActivity", "进入第二个界面");
        setContentView(R.layout.activity_push);
        image = (ImageView) findViewById(R.id.iv_image);
        Intent intent = getIntent();

        if (intent.getAction() == "getCamara") {

            String fileName = intent.getStringExtra("fileName");
            Bitmap myCamera = BitmapFactory.decodeFile(fileName);
            image.setImageBitmap(myCamera);

        } else if (intent.getAction() == "getGallery") {

            String picPath = intent.getStringExtra("picPath");
            Bitmap myGallery = BitmapFactory.decodeFile(picPath);
            image.setImageBitmap(myGallery);

        }

    }

}