package com.oneplusapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayAdapter<Notification> {

    private LayoutInflater mInflater;
    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();

    public MessageAdapter(Context context, List<Notification> notifications) {
        super(context, 0, notifications);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.message_list_item, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);
        Notification notification = getItem(position);
        view.setTag(notification);

        Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
        User user = comment.getUser();
        viewHolder.ivAvatar.setUser(user);
        viewHolder.tvNickname.setUser(user);
        viewHolder.tvReplyContent.setText(comment.getContent());
        viewHolder.tvPostTime.setText(CommonMethods.createdAtFormat(getContext(), comment.getCreatedAt().toLocalDateTime()));
        Post post = comment.getPost();
        if (post == null) {
            viewHolder.ivPostImage.setVisibility(View.GONE);
            viewHolder.tvPostText.setVisibility(View.GONE);
        } else {
            String imagePath = post.getPostPages()[0].getImage();
            if (TextUtils.isEmpty(imagePath)) {
                viewHolder.ivPostImage.setVisibility(View.GONE);
                viewHolder.tvPostText.setVisibility(View.VISIBLE);
                viewHolder.tvPostText.setText(post.getPostPages()[0].getText());
            } else {
                viewHolder.tvPostText.setVisibility(View.GONE);
                viewHolder.ivPostImage.setVisibility(View.VISIBLE);
            }
            ImageLoader.getInstance().displayImage(imagePath, viewHolder.ivPostImage, DISPLAY_IMAGE_OPTIONS);
        }
        return view;
    }

    static class ViewHolder {
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
    }
}
