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
public class UserPostsGridViewAdapter extends PostsGridViewAdapter {

    public UserPostsGridViewAdapter(Context context, int userId) {
        super(context);
        RestClient.getInstance().getUserPosts(userId, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                notifyDataSetChanged();
            }
        }, new JsonErrorListener(context, null));
    }
}
