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
import com.nostra13.universalimageloader.core.assist.ImageSize;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.BitmapUtil;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.common.ThrowableDeferredAsyncTask;


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
                });
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        finish();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        tvPublish.setEnabled(false);
        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        final ImageSize bitmapSize = new ImageSize(bitmap.getWidth(), bitmap.getHeight());
        ThrowableDeferredAsyncTask<Void, Void, byte[]> compressTask = new ThrowableDeferredAsyncTask<Void, Void, byte[]>() {
            @Override
            protected byte[] doInBackground(Void... params) {
                return BitmapUtil.compressBitmap(bitmap, BitmapUtil.DEFAULT_QUALITY);
            }
        };

        promise = compressTask.promise().done(new DoneCallback<byte[]>() {
            @Override
            public void onDone(byte[] result) {
                bitmap.recycle();
            }
        }).then(new DonePipe<byte[], JSONObject, VolleyError, Void>() {
            @Override
            public Promise<JSONObject, VolleyError, Void> pipeDone(byte[] imageData) {
                ByteArrayBody imageBody = new ByteArrayBody(imageData, ContentType.create("image/jpeg"), "image.jpg");
                String text = editText.getText().toString();
                return RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{imageBody},
                        new Integer[]{bitmapSize.getWidth()}, new Integer[]{bitmapSize.getHeight()});
            }
        }).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject response) {
                Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
            }
        }).fail(new JsonErrorListener(getApplicationContext(), null) {
            @Override
            public void onFail(VolleyError error) {
                super.onFail(error);
                Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
            }
        });
        compressTask.execute(new Void[]{});

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("publish_class", PublishActivity.class);
        startActivity(intent);
        finish();
    }
}
