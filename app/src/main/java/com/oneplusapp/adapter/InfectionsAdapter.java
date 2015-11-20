package com.oneplusapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.LRUCacheMap;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Infection;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InfectionsAdapter extends BaseAdapter {

    private static final int ID_CACHE_CAPACITY = 4096;
    private static final int[] LOADING_THRESHOLDS = new int[]{20, 5};
    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();

    private static Set<Integer> loadedInfectionIds =
            Collections.newSetFromMap(new LRUCacheMap<Integer, Boolean>(ID_CACHE_CAPACITY));
    private static Map<Integer, InfectionWrapper> id2infections = new LinkedHashMap<>();

    static {
        User.registerUserChangedCallback(new ClearCacheListener());
    }

    private boolean loading = false;
    private Context context;
    private Set<LoadingStatusObserver> loadingStatusObservers = new HashSet<>();

    public InfectionsAdapter(Context context) {
        this.context = context;
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
                        long firstInfectionId = getCount() > 0 ? getItemId(0) : 0;
                        long secondInfectionId = getCount() > 1 ? getItemId(1) : 0;
                        for (Infection infection : infections) {
                            if (infection.getPost().isDeleted() &&
                                    infection.getId() != firstInfectionId && infection.getId() != secondInfectionId) {
                                id2infections.remove(infection.getId());
                                continue;
                            }
                            InfectionWrapper wrapper = id2infections.get(infection.getId());
                            if (wrapper == null) {
                                if (!loadedInfectionIds.contains(infection.getId())) {
                                    loadedInfectionIds.add(infection.getId());
                                    id2infections.put(infection.getId(), new InfectionWrapper(infection));
                                }
                            } else {
                                wrapper.infection = infection;
                            }
                        }
                        notifyDataSetChanged();
                    }
                })
                .fail(new JsonErrorListener(context, null)).always(
                new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        notifyLoadingStateChanged();
                    }
                }
        );

        loading = true;
        notifyLoadingStateChanged();
    }

    public boolean pop() {
        Infection infection = (Infection) getItem(0);
        return infection != null && id2infections.remove(infection.getId()) != null;
    }

    public void checkRemainingInfectionsAndLoad() {
        for (int threshold : LOADING_THRESHOLDS) {
            if (threshold == getCount()) {
                loadInfections();
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return id2infections.size();
    }

    @Override
    public Object getItem(int position) {
        Iterator<Map.Entry<Integer, InfectionWrapper>> iterator = id2infections.entrySet().iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        Map.Entry<Integer, InfectionWrapper> entry = iterator.next();
        for (int i = 0; i < position; i++) {
            if (!iterator.hasNext()) {
                return null;
            }
            entry = iterator.next();
        }
        return entry.getValue().infection;
    }

    @Override
    public long getItemId(int position) {
        Infection infection = (Infection) getItem(position);
        return infection == null ? 0 : infection.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Infection infection = (Infection) getItem(position);
        if (infection == null) {
            return null;
        }

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_list_item, parent, false);
        }
        HomePageItemViews itemViews = new HomePageItemViews();
        ButterKnife.bind(itemViews, view);

        Post post = infection.getPost();
        User user = post.getUser();

        itemViews.tvNickname.setUser(user);
        itemViews.ivAvatar.setUser(user);

        if (post.isDeleted()) {
            itemViews.tvComments.setVisibility(View.GONE);
            itemViews.tvViews.setVisibility(View.GONE);
            itemViews.tvContent.setVisibility(View.GONE);
            itemViews.tvNoImageContent.setVisibility(View.GONE);
            itemViews.ivImage.setVisibility(View.VISIBLE);
            itemViews.ivImage.setImageResource(R.drawable.delete_image);
            itemViews.ivImage.setTag(null);
            view.setOnClickListener(null);
        } else {
            itemViews.tvComments.setVisibility(View.VISIBLE);
            itemViews.tvViews.setVisibility(View.VISIBLE);
            itemViews.tvComments.setText(String.valueOf(post.getCommentsCount()));
            itemViews.tvViews.setText(String.valueOf(post.getViewsCount()));
            String imagePath = post.getPostPages()[0].getImage();
            if (imagePath == null) {
                imagePath = "";
            }
            if (imagePath.isEmpty()) {
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
            }
            if (!imagePath.equals(itemViews.ivImage.getTag())) {
                ImageLoader.getInstance().displayImage(imagePath, itemViews.ivImage, DISPLAY_IMAGE_OPTIONS);
                itemViews.ivImage.setTag(imagePath);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CardDetailsActivity.class);
                    intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(infection.getPost()));
                    context.startActivity(intent);
                }
            });
        }

        view.setTag(infection);

        return view;
    }

    public boolean isLoading() {
        return loading;
    }

    protected void notifyLoadingStateChanged() {
        for (LoadingStatusObserver observer : loadingStatusObservers) {
            observer.onLoadingStatusChanged(loading);
        }
    }

    public void registerLoadingStatusObserver(LoadingStatusObserver observer) {
        loadingStatusObservers.add(observer);
    }

    public void unregisterLoadingStatusObserver(LoadingStatusObserver observer) {
        loadingStatusObservers.remove(observer);
    }

    private static class InfectionWrapper {
        private InfectionWrapper(Infection infection) {
            this.infection = infection;
        }

        private Infection infection;
    }

    public interface LoadingStatusObserver {
        void onLoadingStatusChanged(boolean loading);
    }

    private static class ClearCacheListener implements User.UserChangedCallback {

        @Override
        public void onUserChanged(User oldUser, User newUser) {
            id2infections.clear();
            loadedInfectionIds.clear();
        }
    }

    static class HomePageItemViews {
        @Bind(R.id.tv_nickname)
        UserNicknameTextView tvNickname;

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
        UserAvatarImageView ivAvatar;
    }
}
