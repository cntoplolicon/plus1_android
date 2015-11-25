package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.adapter.MessageAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.PauseOnScrollListener;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageFragment extends Fragment {
    private PushNotificationService.Callback callback = new NotificationChangedCallback();
    private MessageAdapter messageAdapter;

    @Bind(R.id.tv_no_message)
    TextView tvNoMessage;
    @Bind(R.id.rv_recycler_view)
    RecyclerView rvRecyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ButterKnife.bind(this, view);

        List<Notification> notifications = Notification.getMyNotifications(User.current.getId());
        syncNotificationUsers(notifications);
        rvRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecyclerView.setItemAnimator(null);
        rvRecyclerView.addOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

//        messageAdapter = new MessageAdapter(getActivity(), notifications);

        PushNotificationService.getInstance().registerCallback(callback);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PushNotificationService.getInstance().unregisterCallback(callback);
    }

    private void syncNotificationUsers(List<Notification> notifications) {
        final Map<Notification, Comment> notification2Comment = new HashMap<>();

        Set<Integer> userIds = new HashSet<>();

        for (Notification notification : notifications) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            notification2Comment.put(notification, comment);
            userIds.add(comment.getUser().getId());
        }

        if (!userIds.isEmpty()) {
            RestClient.getInstance().getNotificationUsersInfo(userIds).done(new DoneCallback<JSONArray>() {
                @Override
                public void onDone(JSONArray result) {
                    User[] users = CommonMethods.createDefaultGson().fromJson(result.toString(), User[].class);
                    Map<Integer, User> id2User = new HashMap<>();
                    for (User user : users) {
                        id2User.put(user.getId(), user);
                    }
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Map.Entry<Notification, Comment> entry : notification2Comment.entrySet()) {
                            Comment comment = entry.getValue();
                            comment.setUser(id2User.get(comment.getUser().getId()));
                            Notification notification = entry.getKey();
                            notification.setContent(CommonMethods.createDefaultGson().toJson(comment));
                            notification.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();

                    } finally {
                        ActiveAndroid.endTransaction();
                    }
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class NotificationChangedCallback implements PushNotificationService.Callback {
        @Override
        public void onNotificationReceived(Notification notification) {
//            messageAdapter.insert(notification, 0);
            messageAdapter.notifyDataSetChanged();
        }
    }
}
