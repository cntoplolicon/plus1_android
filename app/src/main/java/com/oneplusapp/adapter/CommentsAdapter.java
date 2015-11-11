package com.oneplusapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.User;
import com.oneplusapp.view.CustomUserAvatarView;
import com.oneplusapp.view.UserNicknameTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shw on 2015/9/14.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {
    private int selectItem = -1;
    private LayoutInflater mInflater;

    public CommentsAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.card_details_comment_item, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);

        if (comment.getReplyToId() > 0) {
            float scale = getContext().getResources().getDisplayMetrics().density;
            int paddingDpAsPx = (int) (20 * scale + 0.5f);
            view.setPadding(paddingDpAsPx, 0, 0, 0);
        } else {
            view.setPadding(0, 0, 0, 0);
        }
        User tmpUser = comment.getUser();
        viewHolder.tvNickname.setUser(tmpUser);
        viewHolder.ivAvatar.setUser(tmpUser);
        ImageLoader.getInstance().cancelDisplayTask(viewHolder.ivAvatar);
        if (tmpUser.getAvatar() != null) {
            ImageLoader.getInstance().displayImage(tmpUser.getAvatar(), viewHolder.ivAvatar);
        } else {
            viewHolder.ivAvatar.setImageResource(R.drawable.default_user_avatar);
        }
        if (comment.getReplyToId() == 0) {
            viewHolder.tvContent.setText(comment.getContent());
            viewHolder.tvReply.setVisibility(view.GONE);
            viewHolder.tvReplyTarget.setVisibility(view.GONE);
        } else {
            viewHolder.tvReply.setVisibility(view.VISIBLE);
            viewHolder.tvReplyTarget.setVisibility(view.VISIBLE);
            Comment repliedComment = getCommentById(comment.getReplyToId());
            viewHolder.tvContent.setText(comment.getContent());
            viewHolder.tvReplyTarget.setUser(repliedComment.getUser());
        }
        view.setTag(comment);
        if (position == selectItem) {
            view.setBackgroundColor(Color.parseColor("#efefef"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    public Comment getCommentById(int id) {
        for (int i = 0; i < getCount(); i++) {
            Comment comment = getItem(i);
            if (comment.getId() == id) {
                return comment;
            }
        }
        return null;
    }

    public void sortComments() {
        Comment[] comments = new Comment[getCount()];
        for (int i = 0; i < getCount(); i++) {
            comments[i] = getItem(i);
        }

        Arrays.sort(comments, new CommentComparator());

        Map<Integer, List<Comment>> nodeToChildrenMap = new HashMap<>();
        for (Comment comment : comments) {
            nodeToChildrenMap.put(comment.getId(), new ArrayList<Comment>());
        }

        for (Comment comment : comments) {
            if (comment.getReplyToId() != 0) {
                nodeToChildrenMap.get(comment.getReplyToId()).add(comment);
            }
        }
        clear();
        for (Comment comment : comments) {
            if (comment.getReplyToId() == 0) {
                depthFirstSearch(nodeToChildrenMap, comment);
            }
        }
    }

    private void depthFirstSearch(Map<Integer, List<Comment>> nodeToChildrenMap, Comment comment) {
        add(comment);
        for (Comment reply : nodeToChildrenMap.get(comment.getId())) {
            depthFirstSearch(nodeToChildrenMap, reply);
        }
    }

    private static class CommentComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment comment1, Comment comment2) {
            if (comment1.getCreatedAt().equals(comment2.getCreatedAt())) {
                return comment1.getId() - comment2.getId();
            }
            return comment1.getCreatedAt().compareTo(comment2.getCreatedAt());
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_avatar)
        CustomUserAvatarView ivAvatar;
        @Bind(R.id.tv_nickname)
        UserNicknameTextView tvNickname;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_reply)
        TextView tvReply;
        @Bind(R.id.tv_reply_target)
        UserNicknameTextView tvReplyTarget;
    }
}
