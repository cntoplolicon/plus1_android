package swj.swj.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.BitmapUtil;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;


/**
 * Created by syb on 2015/9/12.
 */
public class PublishActivity extends BaseActivity {

    private static Promise<JSONObject, VolleyError, Void> promise;

    @Bind(R.id.iv_image)
    ImageView imageView;

    @Bind(R.id.et_text)
    EditText editText;

    @Bind(R.id.tv_publish)
    TextView tvPublish;

    public static Promise<JSONObject, VolleyError, Void> getPromise() {
        return promise;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);

        Uri uri = getIntent().getParcelableExtra("imagePath");
        tvPublish.setEnabled(false);
        BitmapUtil.prepareImageForUploading(this, uri)
                .done(new DoneCallback<Bitmap>() {
                    @Override
                    public void onDone(Bitmap bitmap) {
                        tvPublish.setEnabled(true);
                        imageView.setImageBitmap(bitmap);
                    }
                }).fail(new BitmapUtil.ImageProcessingFailureCallback(this));
    }

    private void exitActivity() {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            imageView.setImageDrawable(null);
            bitmap.recycle();
        }
        finish();
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        exitActivity();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        byte[] imageData = BitmapUtil.compressBitmap(bitmap, BitmapUtil.DEFAULT_QUALITY);
        String text = editText.getText().toString();
        ByteArrayBody imageBody = new ByteArrayBody(imageData, ContentType.create("image/jpeg"), "image.jpg");

        promise = RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{imageBody},
                new Integer[]{bitmap.getWidth()}, new Integer[]{bitmap.getHeight()}).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onFail(VolleyError error) {
                        super.onFail(error);
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                    }
                }).always(new ResetViewClickable<JSONObject, VolleyError>(tvPublish));

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("publish_class", PublishActivity.class);
        startActivity(intent);
        exitActivity();
    }
}
