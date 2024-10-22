package com.oneplusapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int MESSAGE_TYPE_COMMENT = 0;
    private final static int MESSAGE_TYPE_RECOMMEND = 1;

    private List<Notification> notifications = new ArrayList<>();

    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    private CommentMessageViewHolder commentMessageViewHolder;
    private RecommendMessageViewHolder recommendMessageViewHolder;

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();

    public MessageAdapter(Context context, Collection<? extends Notification> notifications) {
        super();
        this.notifications.addAll(notifications);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        switch (notifications.get(position).getType()) {
            case Notification.TYPE_COMMENT:
                return MESSAGE_TYPE_COMMENT;
            case Notification.TYPE_RECOMMEND:
                return MESSAGE_TYPE_RECOMMEND;
            default:
                throw new IllegalStateException("Notification type unknown");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_TYPE_RECOMMEND) {
            View view = mInflater.inflate(R.layout.message_list_recommend_item, parent, false);
            recommendMessageViewHolder = new RecommendMessageViewHolder(view);
            return recommendMessageViewHolder;
        } else {
            View view = mInflater.inflate(R.layout.message_list_comment_item, parent, false);
            commentMessageViewHolder = new CommentMessageViewHolder(view);
            return commentMessageViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Notification notification = notifications.get(position);
        viewHolder.itemView.setTag(notification);
        if (getItemViewType(position) == MESSAGE_TYPE_COMMENT) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            User user = comment.getUser();

            commentMessageViewHolder.ivAvatar.setUser(user);
            commentMessageViewHolder.tvNickname.setUser(user);
            commentMessageViewHolder.tvReplyContent.setText(comment.getContent());
            commentMessageViewHolder.tvPostTime.setText(CommonMethods.createdAtFormat(mContext, comment.getCreatedAt().toLocalDateTime()));

            final Post post = comment.getPost();
            if (post == null) {
                commentMessageViewHolder.ivPostImage.setVisibility(View.GONE);
                commentMessageViewHolder.tvPostText.setVisibility(View.GONE);
            } else {
                String imagePath = post.getPostPages()[0].getImage();
                if (TextUtils.isEmpty(imagePath)) {
                    commentMessageViewHolder.ivPostImage.setVisibility(View.GONE);
                    commentMessageViewHolder.tvPostText.setVisibility(View.VISIBLE);
                    commentMessageViewHolder.tvPostText.setText(post.getPostPages()[0].getText());
                } else {
                    commentMessageViewHolder.ivPostImage.setVisibility(View.VISIBLE);
                    commentMessageViewHolder.tvPostText.setVisibility(View.GONE);
                    ImageLoader.getInstance().displayImage(imagePath, commentMessageViewHolder.ivPostImage, DISPLAY_IMAGE_OPTIONS);
                }
            }
        } else if (getItemViewType(position) == MESSAGE_TYPE_RECOMMEND) {
            Post post = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Post.class);

            String postTime = CommonMethods.createdAtFormat(mContext, notification.getReceiveTime().toLocalDateTime());
            recommendMessageViewHolder.tvRecommendTime.setText(postTime);

            String imagePath = post.getPostPages()[0].getImage();
            if (TextUtils.isEmpty(imagePath)) {
                recommendMessageViewHolder.ivRecommendMessagePostImage.setVisibility(View.GONE);
                recommendMessageViewHolder.tvRecommendMessagePostText.setVisibility(View.VISIBLE);
                recommendMessageViewHolder.tvRecommendMessagePostText.setText(post.getPostPages()[0].getText());
            } else {
                recommendMessageViewHolder.ivRecommendMessagePostImage.setVisibility(View.VISIBLE);
                recommendMessageViewHolder.tvRecommendMessagePostText.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(imagePath, recommendMessageViewHolder.ivRecommendMessagePostImage, DISPLAY_IMAGE_OPTIONS);
            }
        }

        if (mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView, position);
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return notifications.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public static class CommentMessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        UserAvatarImageView ivAvatar;
        @Bind(R.id.tv_nickname)
        UserNicknameTextView tvNickname;
        @Bind(R.id.tv_post_time)
        TextView tvPostTime;
        @Bind(R.id.iv_post_image)
        ImageView ivPostImage;
        @Bind(R.id.tv_post_text)
        TextView tvPostText;
        @Bind(R.id.tv_reply_content)
        TextView tvReplyContent;

        public CommentMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class RecommendMessageViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_recommend_time)
        TextView tvRecommendTime;
        @Bind(R.id.iv_recommend_message_post_image)
        ImageView ivRecommendMessagePostImage;
        @Bind(R.id.tv_recommend_message_post_text)
        TextView tvRecommendMessagePostText;

        public RecommendMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
