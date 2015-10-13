package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.List;

import swj.swj.R;
import swj.swj.bean.CardDetailsItemBean;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Comment;
import swj.swj.model.Post;

/**
 * Created by shw on 2015/9/14.
 */
public class CardDetailsAdapter extends BaseAdapter {
    private List<CardDetailsItemBean> mList;
    private LayoutInflater mInflater;
    private Comment[] comments = new Comment[]{};

    public CardDetailsAdapter(Context context, Post post, List<CardDetailsItemBean> list) {
        mList = list;
        RestClient.getInstance().getPostComments(post.getId()).done(new DoneCallback<JSONArray>() {
            @Override
            public void onDone(JSONArray response) {
                comments = CommonMethods.createDefaultGson().fromJson(response.toString(), Comment[].class);
                notifyDataSetChanged();
                for (Comment comment : comments) {
                    mList.add(new CardDetailsItemBean(R.drawable.default_useravatar, comment.getUser().getNickname(), comment.getContent()));
                }
            }
        }).fail(new JsonErrorListener(context, null));
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
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

        CardDetailsItemBean bean = mList.get(position);
        viewHolder.imageView.setImageResource(bean.getImageView());
        viewHolder.userName.setText(bean.getUserName());
        viewHolder.context.setText(bean.getContent());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView imageView;
        private TextView userName;
        private TextView context;
    }
}
