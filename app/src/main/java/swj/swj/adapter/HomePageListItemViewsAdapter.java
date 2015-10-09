package swj.swj.adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LRUCacheMap;
import swj.swj.common.RestClient;
import swj.swj.model.Infection;
import swj.swj.model.Post;

/**
 * Created by shw on 2015/9/15.
 */
public class HomePageListItemViewsAdapter {

    public static final int STATE_CLEARED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NORMAL = 2;

    private static final int ID_CACHE_CAPACITY = 256;

    private static HomePageListItemViewsAdapter instance;
    private Context context;

    private Map<Infection, View> infections2views = new LinkedHashMap<>();
    private Set<Integer> loadedInfectionIds;
    private int state = STATE_CLEARED;
    private boolean loading = false;
    private Callback callback;

    public static HomePageListItemViewsAdapter getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new HomePageListItemViewsAdapter(context);
    }

    private HomePageListItemViewsAdapter(Context context) {
        this.context = context;
        loadedInfectionIds = Collections.newSetFromMap(new LRUCacheMap<Integer, Boolean>(ID_CACHE_CAPACITY));
    }

    public void reset() {
        loadedInfectionIds.clear();
        infections2views.clear();
        state = STATE_CLEARED;
        loading = false;
        callback = null;
    }

    private void updateState() {
        int oldState = state;
        if (infections2views.isEmpty()) {
            state = loading ? STATE_LOADING : STATE_CLEARED;
        } else {
            state = STATE_NORMAL;
        }
        if (oldState != state && callback != null) {
            callback.onStateChanged(oldState, state);
        }
    }

    private View createView(Infection infection) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_list_item, null);
        HomePageItemViews itemViews = new HomePageItemViews();
        ButterKnife.bind(itemViews, view);

        Post post = infection.getPost();
        itemViews.tvUser.setText(post.getUser().getNickname());
        itemViews.tvContent.setText(post.getPostPages()[0].getText());
        itemViews.tvComments.setText(String.valueOf(post.getCommentsCount()));
        itemViews.tvViews.setText(String.valueOf(post.getViewsCount()));
        String imagePath = post.getPostPages()[0].getImage();
        if (imagePath == null || imagePath.isEmpty()) {
            itemViews.ivImage.setVisibility(View.INVISIBLE);
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .build();
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + imagePath,
                    itemViews.ivImage, options);
        }

        return view;
    }

    public View getViewAt(int index) {
        Iterator<Map.Entry<Infection, View>> iterator = infections2views.entrySet().iterator();
        Map.Entry<Infection, View> entry = null;
        while (iterator.hasNext() && index-- >= 0) {
            entry = iterator.next();
        }
        if (index >= 0) {
            loadInfections();
            return null;
        }
        if (entry.getValue() == null) {
            entry.setValue(createView(entry.getKey()));
        }
        return entry.getValue();
    }

    public Infection getInfectionByView(View view) {
        for (Map.Entry<Infection, View> entry : infections2views.entrySet()) {
            if (entry.getValue() == view) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removeView(View view) {
        Infection infection = getInfectionByView(view);
        if (infection != null) {
            infections2views.remove(infection);
            updateState();
        }
    }

    public void loadInfections() {
        if (loading) {
            return;
        }
        RestClient.getInstance().getActiveInfections().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        Infection[] infections = CommonMethods.createDefaultGson().fromJson(response.toString(), Infection[].class);
                        for (Infection infection : infections) {
                            if (!loadedInfectionIds.contains(infection.getId())) {
                                loadedInfectionIds.add(infection.getId());
                                infections2views.put(infection, null);
                            }
                        }
                        loading = false;
                        updateState();
                    }
                }).fail(
                new JsonErrorListener(context, null) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        loading = false;
                        updateState();
                    }
                });
        loading = true;
        updateState();
    }

    public int getState() {
        return state;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onStateChanged(int oldState, int newState);
    }

    static class HomePageItemViews {
        @Bind(R.id.tv_user)
        TextView tvUser;

        @Bind(R.id.tv_content)
        TextView tvContent;

        @Bind(R.id.tv_comments)
        TextView tvComments;

        @Bind(R.id.tv_views)
        TextView tvViews;

        @Bind(R.id.iv_image)
        ImageView ivImage;
    }
}
