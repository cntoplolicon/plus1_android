package swj.swj.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.DownloadImageTask;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImageActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        final String resString = getIntent().getStringExtra("image_url");
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                .showImageOnLoading(R.color.home_title_color)
                .showImageOnFail(R.drawable.image_load_fail)
                .build();
        if (resString != null) {
            ImageLoader.getInstance().displayImage(resString, ivPhoto, options);
        } else {
            ivPhoto.setImageResource(R.drawable.default_user_avatar);
        }
        photoViewAttacher = new PhotoViewAttacher(ivPhoto);
        photoViewAttacher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(ShowImageActivity.this).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setContentView(R.layout.dialog_image_long_click);
                TextView tvSave = (TextView) window.findViewById(R.id.tv_save_storage);
                TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
                tvSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                        new DownloadImageTask(getApplicationContext()).execute(resString);
                        Toast.makeText(getApplicationContext(), R.string.downloading_image, Toast.LENGTH_LONG).show();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                return false;
            }
        });
    }

}
