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

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostsGridViewAdapter extends ArrayAdapter<Post> {

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

    protected Set<LoadingStatusObserver> loadingStatusObservers = new HashSet<>();
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
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_post_grid_view_item, parent, false);
        }

        Post post = getItem(position);
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);

        viewHolder.tvText.setText(post.getPostPages()[0].getText());
        viewHolder.tvComments.setText(String.valueOf(post.getCommentsCount()));
        viewHolder.tvViews.setText(String.valueOf(post.getViewsCount()));

        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null) {
            imageUrl = "";
        }
        if (!imageUrl.equals(viewHolder.ivImage.getTag())) {
            if (imageUrl.isEmpty()) {
                viewHolder.ivImage.setImageDrawable(null);
                viewHolder.tvText.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvText.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(imageUrl, viewHolder.ivImage, DISPLAY_IMAGE_OPTIONS);
            }
            viewHolder.ivImage.setTag(imageUrl);
        }

        return view;
    }

    protected void notifyLoadingStatusChanged() {
        for (LoadingStatusObserver observer : loadingStatusObservers) {
            observer.onLoadingStatusChanged(loading);
        }
    }

    public void updateAll(Post[] posts) {
        clear();
        addAll(posts);
    }

    public void registerLoadingStatusObserver(LoadingStatusObserver observer) {
        loadingStatusObservers.add(observer);
    }

    public void unregisterLoadingStatusObserver(LoadingStatusObserver observer) {
        loadingStatusObservers.remove(observer);
    }

    public boolean isLoading() {
        return loading;
    }

    public interface LoadingStatusObserver {
        void onLoadingStatusChanged(boolean loading);
    }

    static class ViewHolder {
        @Bind(R.id.tv_text)
        TextView tvText;
        @Bind(R.id.tv_comments)
        TextView tvComments;
        @Bind(R.id.tv_views)
        TextView tvViews;
        @Bind(R.id.iv_image)
        ImageView ivImage;
    }
}
