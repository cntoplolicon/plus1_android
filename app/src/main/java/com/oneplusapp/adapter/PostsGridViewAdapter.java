/**
 * Created by cntoplolicon on 10/7/15.
 */
package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.model.Post;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by jiewei on 9/14/15.
 */
public class PostsGridViewAdapter extends ArrayAdapter<Post> {

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

    protected Set<Callback> callbacks = new HashSet<>();
    protected boolean loading;

    public PostsGridViewAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(getContext()).inflate(R.layout.user_post_grid_view_item, null);
        }
        Post post = getItem(position);
        TextView tvText = (TextView) gridView.findViewById(R.id.tv_text);
        tvText.setText(post.getPostPages()[0].getText());
        TextView tvComments = (TextView) gridView.findViewById(R.id.tv_comments);
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        TextView tvViews = (TextView) gridView.findViewById(R.id.tv_views);
        tvViews.setText(String.valueOf(post.getViewsCount()));

        ImageView imageView = (ImageView) gridView.findViewById(R.id.iv_image);
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageDrawable(null);
            tvText.setVisibility(View.VISIBLE);
        } else {
            tvText.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(imageUrl, imageView, DISPLAY_IMAGE_OPTIONS);
        }
        return gridView;
    }

    protected void notifyLoadingStatusChanged() {
        for (Callback callback : callbacks) {
            callback.onLoadingStatusChanged(loading);
        }
    }

    public void updateAll(Post[] posts) {
        clear();
        addAll(posts);
    }

    public void registerCallback(Callback callback) {
        callbacks.add(callback);
    }

    public void unregisterCallback(Callback callback) {
        callbacks.remove(callback);
    }

    public boolean isLoading() {
        return loading;
    }

    public interface Callback {
        void onLoadingStatusChanged(boolean loading);
    }
}
