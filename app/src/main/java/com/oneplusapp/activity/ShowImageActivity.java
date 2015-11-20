package com.oneplusapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.DownloadImageTask;
import com.oneplusapp.view.MenuDialog;

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
                MenuDialog.showConfirmDialog(ShowImageActivity.this, R.string.tv_save_storage, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DownloadImageTask(getApplicationContext()).execute(resString);
                        Toast.makeText(getApplicationContext(), R.string.downloading_image, Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }
        });
    }

}
