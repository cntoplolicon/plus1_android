package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

/**
 * Created by jiewei on 9/14/15.
 */
public class UserPostGridViewAdapter extends BaseAdapter {

    private Context context;
    private Post[] posts = new Post[]{};

    public UserPostGridViewAdapter(Context context, int userId) {
        this.context = context;
        RestClient.getInstance().getUserPosts(userId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                notifyDataSetChanged();
            }
        }, new JsonErrorListener(context, null));
    }

    @Override
    public int getCount() {
        return posts.length;
    }

    @Override
    public Object getItem(int position) {
        return posts[position];
    }

    @Override
    public long getItemId(int position) {
        return posts[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(context).inflate(R.layout.user_post_grid_view_item, null);
        }

        Post post = posts[position];

        TextView tvSpreadsCount = (TextView) gridView.findViewById(R.id.tv_spreads_count);
        String spreadsCountFormat = context.getResources().getString(R.string.spreads_count);
        tvSpreadsCount.setText(String.format(spreadsCountFormat, post.getSpreadsCount()));

        ImageView imageView = (ImageView) gridView.findViewById(R.id.iv_image);
        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null) {
            imageView.setImageDrawable(null);
            imageView.setBackgroundResource(R.color.common_yellow);
        } else {
            imageView.setImageResource(R.drawable.loading);
            ImageLoader.getInstance().displayImage(RestClient.IMAGE_SERVER_URL + imageUrl, imageView);
        }

        return gridView;
    }
}
