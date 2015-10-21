package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.io.File;

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
public class PublishActivity extends Activity {

    private String imageFilePath;
    private ImageSize imageSize;
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
        BitmapUtil.ImageFileInfo imageFileInfo = BitmapUtil.prepareBitmapForUploading(this, uri);
        File compressedImageFile = imageFileInfo.getFile();
        imageSize = imageFileInfo.getImageSize();
        imageFilePath = compressedImageFile.getAbsolutePath();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).build();
        ImageLoader.getInstance().displayImage(Uri.fromFile(compressedImageFile).toString(), imageView, options);
    }

    @OnClick(R.id.tv_delete)
    public void delete() {
        finish();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        String text = editText.getText().toString();
        FileBody imageBody = new FileBody(new File(imageFilePath));
        promise = RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{imageBody}, new Integer[]{imageSize.getWidth()}, new Integer[]{imageSize.getHeight()}).done(
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
        finish();
    }


}
