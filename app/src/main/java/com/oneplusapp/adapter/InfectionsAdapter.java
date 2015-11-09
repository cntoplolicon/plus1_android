package com.oneplusapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.LRUCacheMap;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Infection;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shw on 2015/9/15.
 */
public class InfectionsAdapter {

    public static final int STATE_CLEARED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_NORMAL = 2;
    private static final Set<Integer> INFECTION_THRESHOLDS = new HashSet<>(20, 5);

    private static final int ID_CACHE_CAPACITY = 256;

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();

    private static InfectionsAdapter instance;
    private Context context;

    private Map<Integer, Infection> id2infections = new LinkedHashMap<>();
    private Set<Integer> loadedInfectionIds;
    private int state = STATE_CLEARED;
    private boolean loading = false;
    private Callback callback;


    public static InfectionsAdapter getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new InfectionsAdapter(context);
    }

    private InfectionsAdapter(Context context) {
        this.context = context;
        loadedInfectionIds = Collections.newSetFromMap(new LRUCacheMap<Integer, Boolean>(ID_CACHE_CAPACITY));
    }

    public void reset() {
        loadedInfectionIds.clear();
        id2infections.clear();
        state = STATE_CLEARED;
        loading = false;
        callback = null;
    }

    private void updateState() {
        int oldState = state;
        if (id2infections.isEmpty()) {
            state = loading ? STATE_LOADING : STATE_CLEARED;
        } else {
            state = STATE_NORMAL;
        }
        if (oldState != state && callback != null) {
            callback.onStateChanged(oldState, state);
        }
    }

    private View getView(Infection infection, View convertView) {
        View view = convertView;
        if (view != null && view.getTag() == infection) {
            return view;
        }
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_list_item, null);
        }
        HomePageItemViews itemViews = new HomePageItemViews();
        ButterKnife.bind(itemViews, view);

        Post post = infection.getPost();
        itemViews.tvUser.setText(post.getUser().getNickname());
        CommonMethods.chooseNicknameColorViaGender(itemViews.tvUser, post.getUser(), context);
        int genderIcon = 0;
        if (post.getUser().getGender() != User.GENDER_UNKNOWN) {
            genderIcon = post.getUser().getGender() == User.GENDER_FEMALE ? R.drawable.icon_woman : R.drawable.icon_man;
        }
        itemViews.tvUser.setCompoundDrawablesWithIntrinsicBounds(0, 0, genderIcon, 0);
        itemViews.tvComments.setText(String.valueOf(post.getCommentsCount()));
        itemViews.tvViews.setText(String.valueOf(post.getViewsCount()));
        ImageLoader.getInstance().cancelDisplayTask(itemViews.ivAvatar);
        String avatarUrl = infection.getPost().getUser().getAvatar();
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(avatarUrl, itemViews.ivAvatar);
        } else {
            itemViews.ivAvatar.setImageResource(R.drawable.default_user_avatar);
        }
        String imagePath = post.getPostPages()[0].getImage();
        ImageLoader.getInstance().cancelDisplayTask(itemViews.ivImage);
        if (imagePath == null || imagePath.isEmpty()) {
            itemViews.ivImage.setImageBitmap(null);
            itemViews.ivImage.setVisibility(View.GONE);
            itemViews.tvContent.setVisibility(View.GONE);
            itemViews.tvNoImageContent.setVisibility(View.VISIBLE);
            itemViews.tvNoImageContent.setText(post.getPostPages()[0].getText());
        } else {
            itemViews.tvContent.setVisibility(View.VISIBLE);
            itemViews.tvContent.setText(post.getPostPages()[0].getText());
            itemViews.tvNoImageContent.setVisibility(View.GONE);
            itemViews.ivImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(imagePath, itemViews.ivImage, DISPLAY_IMAGE_OPTIONS);
        }
        view.setTag(infection);
        return view;
    }

    public View getViewAt(int index, View convertView) {
        Iterator<Map.Entry<Integer, Infection>> iterator = id2infections.entrySet().iterator();
        Map.Entry<Integer, Infection> entry = null;
        while (iterator.hasNext() && index-- >= 0) {
            entry = iterator.next();
        }
        if (index >= 0) {
            loadInfections();
            return null;
        }
        return getView(entry.getValue(), convertView);
    }

    public void remove(View view) {
        Infection infection = (Infection) view.getTag();
        id2infections.remove(infection.getId());
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
                                id2infections.put(infection.getId(), infection);
                            }
                        }
                        loading = false;
                        updateState();
                    }
                }).fail(
                new JsonErrorListener(context, null) {
                    @Override
                    public void onFail(VolleyError error) {
                        super.onFail(error);
                        loading = false;
                        updateState();
                    }
                });
        loading = true;
        updateState();
    }

    public void checkRemainingInfectionsAndUpdate() {
        if (INFECTION_THRESHOLDS.contains(id2infections.size())) {
            loadInfections();
        }
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
        @Bind(R.id.tv_nickname)
        TextView tvUser;

        @Bind(R.id.tv_content)
        TextView tvContent;

        @Bind(R.id.tv_no_image_content)
        TextView tvNoImageContent;

        @Bind(R.id.tv_comments)
        TextView tvComments;

        @Bind(R.id.tv_views)
        TextView tvViews;

        @Bind(R.id.iv_image)
        ImageView ivImage;

        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
    }
}