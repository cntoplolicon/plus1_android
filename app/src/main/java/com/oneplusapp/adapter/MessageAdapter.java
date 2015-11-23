package com.oneplusapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageAdapter extends ArrayAdapter<Notification> {

    private LayoutInflater mInflater;

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
        viewHolder.tvMessage.setText(comment.getReplyToId() == 0 ? R.string.message_card : R.string.message_comment);
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.iv_avatar)
        UserAvatarImageView ivAvatar;
        @Bind(R.id.tv_nickname)
        UserNicknameTextView tvNickname;
        @Bind(R.id.tv_message)
        TextView tvMessage;
    }
}
