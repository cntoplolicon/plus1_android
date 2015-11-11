package com.oneplusapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;
import com.oneplusapp.view.CustomUserAvatarView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shw on 2015/9/14.
 */
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
            view = mInflater.inflate(R.layout.message_list_item, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);
        Notification notification = getItem(position);
        view.setTag(notification);

        Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
        User tmpUser = comment.getUser();
        viewHolder.ivAvatar.setUser(tmpUser);
        viewHolder.tvNickname.setText(tmpUser.getNickname());
        CommonMethods.chooseNicknameColorViaGender(viewHolder.tvNickname, tmpUser, getContext());
        viewHolder.tvMessage.setText(comment.getReplyToId() == 0 ? R.string.message_card : R.string.message_comment);
        ImageLoader.getInstance().cancelDisplayTask(viewHolder.ivAvatar);
        if (tmpUser.getAvatar() == null) {
            viewHolder.ivAvatar.setImageResource(R.drawable.default_user_avatar);
        } else {
            ImageLoader.getInstance().displayImage(tmpUser.getAvatar(), viewHolder.ivAvatar);
        }
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.iv_avatar)
        CustomUserAvatarView ivAvatar;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.tv_message)
        TextView tvMessage;
    }
}
