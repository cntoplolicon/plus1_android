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
        Comment[] comments = new Comment[getCount()];
        for (int i = 0; i < comments.length; i++) {
            comments[i] = getItem(i);
        }
        Arrays.sort(comments, new CommentComparator());

        Map<Integer, List<Comment>> nodeToChildrenMap = new HashMap<>();
        for (Comment comment: comments) {
            nodeToChildrenMap.put(comment.getId(), new ArrayList<Comment>());
        }

        for (Comment comment: comments) {
            if (comment.getReplyToId() != 0 ) {
                nodeToChildrenMap.get(comment.getReplyToId()).add(comment);
            }
        }
        clear();
        for (Comment comment: comments) {
            if (comment.getReplyToId() == 0) {
                depthFirstSearch(nodeToChildrenMap, comment);
            }
        }
    }

    private void depthFirstSearch(Map<Integer, List<Comment>> nodeToChildrenMap, Comment comment) {
        add(comment);
        for (Comment reply: nodeToChildrenMap.get(comment.getId())) {
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

    private static class ViewHolder {
        private ImageView imageView;
        private TextView userName;
        private TextView context;
    }

}
