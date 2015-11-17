package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecommendStaggeredGridAdapter extends RecyclerView.Adapter<RecommendStaggeredGridAdapter.ViewHolder> {

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

    private Set<Callback> callbacks = new HashSet<>();
    private boolean loading;

    private LayoutInflater mInflater;
    private Post[] posts = new Post[0];
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public RecommendStaggeredGridAdapter(Context context) {
        super();
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecommendStaggeredGridAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.hot_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder myViewHolder, final int position) {
        final Post post = posts[position];
        User user = post.getUser();
        myViewHolder.tvNickname.setUser(user);
        myViewHolder.ivAvatar.setUser(user);
        myViewHolder.tvNoImageContent.setText(post.getPostPages()[0].getText());
        myViewHolder.tvComments.setText(String.valueOf(post.getCommentsCount()));
        myViewHolder.tvViews.setText(String.valueOf(post.getViewsCount()));
        ImageLoader.getInstance().cancelDisplayTask(myViewHolder.ivImage);
        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null || imageUrl.isEmpty()) {
            myViewHolder.ivImage.setImageDrawable(null);
            myViewHolder.ivImage.setVisibility(View.GONE);
            myViewHolder.tvContent.setVisibility(View.GONE);
            myViewHolder.tvNoImageContent.setVisibility(View.VISIBLE);
            myViewHolder.tvNoImageContent.setText(post.getPostPages()[0].getText());
        } else {
            myViewHolder.tvContent.setVisibility(View.VISIBLE);
            myViewHolder.tvContent.setText(post.getPostPages()[0].getText());
            if (myViewHolder.tvContent.getText().toString().trim().isEmpty()) {
                myViewHolder.tvContent.setVisibility(View.GONE);
            }
            myViewHolder.tvNoImageContent.setVisibility(View.GONE);
            myViewHolder.ivImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(imageUrl, myViewHolder.ivImage, DISPLAY_IMAGE_OPTIONS);
        }

        if (mOnItemClickListener != null) {
            myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(myViewHolder.itemView, posts[position]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return posts.length;
    }

    public void loadRecommendations() {
        if (loading) {
            return;
        }
        loading = true;
        notifyLoadingStatusChanged();
        RestClient.getInstance().getRecommendedPosts().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(mContext, null))
                .always(new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        notifyLoadingStatusChanged();
                    }
                });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private void notifyLoadingStatusChanged() {
        for (Callback callback : callbacks) {
            callback.onLoadingStatusChanged(loading);
        }
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

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public interface Callback {
        void onLoadingStatusChanged(boolean loading);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Post post);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_nickname)
        UserNicknameTextView tvNickname;

        @Bind(R.id.tv_content)
        TextView tvContent;

        @Bind(R.id.tv_no_image_content)
        TextView tvNoImageContent;

        @Bind(R.id.tv_comments)
        TextView tvComments;

        @Bind(R.id.tv_views)
        TextView tvViews;

        @Bind(R.id.iv_image)
        ImageView ivImage;

        @Bind(R.id.iv_avatar)
        UserAvatarImageView ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

