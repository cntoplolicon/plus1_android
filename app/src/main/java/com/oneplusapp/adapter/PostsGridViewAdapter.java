package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        if (convertView != null) {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_post_grid_view_item, parent, false);
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            ButterKnife.bind(viewHolder, view);
        }

        Post post = getItem(position);

        viewHolder.tvText.setText(post.getPostPages()[0].getText());
        viewHolder.tvComments.setText(String.valueOf(post.getCommentsCount()));
        viewHolder.tvViews.setText(String.valueOf(post.getViewsCount()));

        final String imageUrl = post.getPostPages()[0].getImage();
        int textVisibility = TextUtils.isEmpty(imageUrl) ? View.VISIBLE : View.GONE;
        viewHolder.tvText.setVisibility(textVisibility);

        final ImageView imageView = viewHolder.ivImage;
        imageView.post(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().displayImage(imageUrl, imageView, DISPLAY_IMAGE_OPTIONS);
            }
        });

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
