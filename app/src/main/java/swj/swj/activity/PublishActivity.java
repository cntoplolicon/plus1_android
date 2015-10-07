package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import swj.swj.common.JsonErrorListener;
import swj.swj.common.PictureUtil;
import swj.swj.common.RestClient;

/**
 * Created by syb on 2015/9/12.
 */
public class PublishActivity extends Activity {

    private Bitmap bitmap;
    private String imageFilePath;

    @Bind(R.id.iv_image)
    ImageView imageView;

    @Bind(R.id.et_text)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);

        imageFilePath = getIntent().getStringExtra("imagePath");
        Bitmap bitmap = PictureUtil.getSmallBitmap(imageFilePath);
        bitmap = getScaledBitmap(bitmap);
        imageView.setImageBitmap(bitmap);
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
                }, new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                    }
                });
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        int height = (int) (bitmap.getHeight() * (1000.0 / bitmap.getWidth()));
        return Bitmap.createScaledBitmap(bitmap, 1000, height, true);
    }

    //  image rotation
    private Bitmap getRotateImage(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setRotate(90, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
