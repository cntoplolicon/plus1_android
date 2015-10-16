package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Comment;
import swj.swj.model.Post;

/**
 * Created by shw on 2015/9/14.
 */
public class CardDetailsAdapter extends ArrayAdapter<Comment> {
    private LayoutInflater mInflater;

    public CardDetailsAdapter(Context context, Post post) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        if (post.getComments() == null) {
            RestClient.getInstance().getPostComments(post.getId()).done(new DoneCallback<JSONArray>() {
                @Override
                public void onDone(JSONArray response) {
                    Comment[] comments = CommonMethods.createDefaultGson().fromJson(response.toString(), Comment[].class);
                    addAll(comments);
                    sortComments();
                    notifyDataSetChanged();
                }
            }).fail(new JsonErrorListener(context, null));
        } else {
            addAll(post.getComments());
            sortComments();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.card_details_comment_item, null);
        }
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.bind(viewHolder, view);
        viewHolder.ivAvatar.setImageResource(R.drawable.default_useravatar);
        viewHolder.tvNickname.setText(comment.getUser().getNickname());
        if (comment.getReplyToId() == 0) {
            viewHolder.tvContent.setText(comment.getContent());
        } else {
            Comment repliedComment = getCommentById(comment.getReplyToId());
            viewHolder.tvContent.setText(String.format(view.getResources().getString(R.string.reply_to_user_format), repliedComment.getUser().getNickname(), comment.getContent()));
        }
        view.setTag(comment);

        return view;
    }

    private Comment getCommentById(int id) {
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

    public static class ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_nickname)
        TextView tvNickname;
        @Bind(R.id.tv_content)
        TextView tvContent;
    }

}
