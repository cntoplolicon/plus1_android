package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.RestClient;

/**
 * Created by syb on 2015/9/12.
 */
public class PublishActivity extends Activity {

    @Bind(R.id.iv_image)
    ImageView imageView;

    @Bind(R.id.et_text)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate");
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.getAction().equals("getCamera")) {
            String photoPath = intent.getStringExtra("photoPath");
            Bitmap myGallery = BitmapFactory.decodeFile(photoPath);
            imageView.setImageBitmap(myGallery);
        } else if (intent.getAction().equals("getGallery")) {
            String picturePath = intent.getStringExtra("picturePath");
            Bitmap myGallery = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap((myGallery));
        }
    }

    @OnClick(R.id.iv_publish_back)
    public void back() {
        finish();
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        finish();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        String text = editText.getText().toString();
        ByteArrayBody imageBody = getImageBody();
        RestClient.getInstance().post(new String[]{text}, new AbstractContentBody[]{imageBody},
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private ByteArrayBody getImageBody() {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null) {
            return null;
        }
        Bitmap avatarBitmap = ((BitmapDrawable) drawable).getBitmap();
        try {
            byte[] avatarData = CommonMethods.bitmap2ByteArray(avatarBitmap);
            return new ByteArrayBody(avatarData, ContentType.create("image/png"), "image.png");
        } catch (IOException e) {
            Log.d(RegisterStepThree.class.getName(), "failed getting avatar data", e);
            return null;
        }
    }
}