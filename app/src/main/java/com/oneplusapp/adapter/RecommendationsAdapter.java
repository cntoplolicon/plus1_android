package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;
import com.oneplusapp.view.DetachableImageView;
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

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private static Post[] posts = new Post[]{};

    private Set<LoadingStatusObserver> loadingStatusObservers = new HashSet<>();
    private boolean loading;

    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public RecommendationsAdapter(Context context) {
        super();
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.recommended_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Post post = posts[position];
        User user = post.getUser();

        viewHolder.tvNickname.setUser(user);
        viewHolder.ivAvatar.setUser(user);
        viewHolder.tvNoImageContent.setText(post.getPostPages()[0].getText());
        viewHolder.tvContent.setText(post.getPostPages()[0].getText());
        viewHolder.tvComments.setText(String.valueOf(post.getCommentsCount()));
        viewHolder.tvViews.setText(String.valueOf(post.getViewsCount()));

        final String imageUrl = post.getPostPages()[0].getImage();
        if (TextUtils.isEmpty(imageUrl)) {
            viewHolder.ivImage.setVisibility(View.GONE);
            viewHolder.tvContent.setVisibility(View.GONE);
            viewHolder.tvNoImageContent.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivImage.setVisibility(View.VISIBLE);
            viewHolder.tvContent.setVisibility(View.VISIBLE);
            viewHolder.tvNoImageContent.setVisibility(View.GONE);
            if (viewHolder.tvContent.getText().toString().trim().isEmpty()) {
                viewHolder.tvContent.setVisibility(View.GONE);
            }
        }

        Drawable loadingDrawable = CommonMethods.createLoadingDrawable(mContext,
                post.getPostPages()[0].getImageWidth(), post.getPostPages()[0].getImageHeight());
        final DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                .showImageOnLoading(loadingDrawable)
                .showImageOnFail(R.drawable.image_load_fail)
                .build();
        if (viewHolder.ivImage.isAttachedToWindow()) {
            ImageLoader.getInstance().displayImage(imageUrl, viewHolder.ivImage, options);
        } else {
            viewHolder.ivImage.setImageDrawable(loadingDrawable);
            viewHolder.ivImage.post(new Runnable() {
                @Override
                public void run() {
                    ImageLoader.getInstance().displayImage(imageUrl, viewHolder.ivImage, options);
                }
            });
        }

        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, post);
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
        for (LoadingStatusObserver loadingStatusObserver : loadingStatusObservers) {
            loadingStatusObserver.onLoadingStatusChanged(loading);
        }
    }

    public void registerCallback(LoadingStatusObserver observer) {
        loadingStatusObservers.add(observer);
    }

    public void unregisterCallback(LoadingStatusObserver observer) {
        loadingStatusObservers.remove(observer);
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public interface LoadingStatusObserver {
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
        DetachableImageView ivImage;

        @Bind(R.id.iv_avatar)
        UserAvatarImageView ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

