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

import java.util.Arrays;
import java.util.Comparator;

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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.card_details_comment_item, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imc_image);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_user);
            viewHolder.context = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Comment comment = getItem(position);
        viewHolder.imageView.setImageResource(R.drawable.default_useravatar);
        viewHolder.userName.setText(comment.getUser().getNickname());
        viewHolder.context.setText(comment.getContent());
        return convertView;
    }

    public void sortComments() {
        final Comment[] comments = new Comment[getCount()];
        for (int i = 0; i < comments.length; i++) {
            comments[i] = getItem(i);
        }
        Arrays.sort(comments, new Comparator<Comment>() {
            @Override
            public int compare(Comment comment1, Comment comment2) {
                if (findCommentAncestor(comment1, comments).equals(findCommentAncestor(comment2, comments))) {
                    return comment1.getId() - comment2.getId();
                } else {
                    return findCommentAncestor(comment1, comments).getId() - findCommentAncestor(comment2, comments).getId();
                }
            }
        });
        clear();
        addAll(comments);
        notifyDataSetChanged();
    }

    private Comment findCommentAncestor(Comment comment, Comment[] comments) {
        if (comment.getReplyToId() != 0) {
            return findCommentAncestor(findCommentParent(comment, comments), comments);
        }
        return comment;
    }

    private Comment findCommentParent(Comment comment, Comment[] comments) {
        for (Comment tmpComment : comments) {
            if (tmpComment.getId() == comment.getReplyToId()) {
                comment = tmpComment;
            }
        }
        return comment;
    }


    private static class ViewHolder {
        private ImageView imageView;
        private TextView userName;
        private TextView context;
    }

}
