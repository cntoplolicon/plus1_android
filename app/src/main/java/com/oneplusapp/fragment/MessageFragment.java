package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.adapter.MessageAdapter;
import com.oneplusapp.common.CommonMethods;
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
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MessageFragment extends Fragment {
    private PushNotificationService.Callback callback = new NotificationChangedCallback();
    private MessageAdapter messageAdapter;
    private List<Notification> notifications;


    @Bind(R.id.tv_no_message)
    TextView tvNoMessage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ButterKnife.bind(this, view);

        notifications = Notification.getMyNotifications(User.current.getId());
        messageAdapter = new MessageAdapter(getActivity(), notifications);
        syncNotificationUsers();
        ListView lvListView = (ListView) view.findViewById(R.id.lv_list_view);
        lvListView.setAdapter(messageAdapter);
        if (notifications.isEmpty()) {
            tvNoMessage.setVisibility(View.VISIBLE);
        } else {
            tvNoMessage.setVisibility(View.GONE);
        }
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = (Notification) view.getTag();
                if (notification.getType().equals(Notification.TYPE_COMMENT)) {
                    Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                    intent.putExtra("notification", notification);
                    startActivity(intent);
                }
            }
        });

        PushNotificationService.getInstance().registerCallback(callback);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PushNotificationService.getInstance().unregisterCallback(callback);
    }

    private void syncNotificationUsers() {

        final HashMap<Integer, User> userHashMap = new HashMap<>();
        final HashMap<Notification, Comment> commentNotificationHashMap = new HashMap<>();

        Set<Integer> userIds = new HashSet<>();

        for (Notification notification : notifications) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            commentNotificationHashMap.put(notification, comment);
            userIds.add(comment.getUser().getId());
        }

        if (!userIds.isEmpty()) {
            RestClient.getInstance().getNotificationUsersInfo(userIds).done(new DoneCallback<JSONArray>() {
                @Override
                public void onDone(JSONArray result) {
                    User[] notificationUsers = CommonMethods.createDefaultGson().fromJson(result.toString(), User[].class);
                    for (User notificationUser : notificationUsers) {
                        userHashMap.put(notificationUser.getId(), notificationUser);
                    }
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Notification notification : notifications) {
                            Comment comment = commentNotificationHashMap.get(notification);
                            comment.setUser(userHashMap.get(comment.getUser().getId()));
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
            messageAdapter.insert(notification, 0);
            messageAdapter.notifyDataSetChanged();
        }
    }
}
