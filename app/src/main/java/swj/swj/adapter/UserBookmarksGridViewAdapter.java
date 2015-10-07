package swj.swj.adapter;

import android.content.Context;

import com.android.volley.Response;

import org.json.JSONArray;

import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

/**
 * Created by cntoplolicon on 10/7/15.
 */
public class UserBookmarksGridViewAdapter extends PostsGridViewAdapter {
    public UserBookmarksGridViewAdapter(Context context) {
        super(context);
        RestClient.getInstance().getUserBookmarks(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                notifyDataSetChanged();
            }
        }, new JsonErrorListener(context, null));
    }
}
