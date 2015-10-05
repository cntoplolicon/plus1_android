package swj.swj.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.RestClient;

/**
 * Created by syb on 2015/9/12.
 */
public class PublishActivity extends Activity {

    @Bind(R.id.iv_image)
    ImageView imageView;

    @Bind(R.id.et_text)
    EditText editText;

    String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);

        imageFilePath = getIntent().getStringExtra("imagePath");
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
        imageView.setImageBitmap(bitmap);
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        finish();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        String text = editText.getText().toString();
        FileBody imageBody = new FileBody(new File(imageFilePath));
        RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{imageBody},
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
        finish();
    }
}