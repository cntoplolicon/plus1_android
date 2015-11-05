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

import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.activity.UserHomeActivity;
import com.oneplusapp.adapter.MessageAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MessageFragment extends Fragment {
    private PushNotificationService.Callback callback = new NotificationChangedCallback();
    private MessageAdapter messageAdapter;

    @Bind(R.id.tv_no_message)
    TextView tvNoMessage;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        ButterKnife.bind(this, view);

        List<Notification> notifications = Notification.getMyNotifications(User.current.getId());
        messageAdapter = new MessageAdapter(getActivity(), notifications);
        messageAdapter.setOnViewClickedListener(new OnViewClickedListener());
        ListView lvListView = (ListView) view.findViewById(R.id.lv_listView);
        lvListView.setAdapter(messageAdapter);
        if (notifications.isEmpty()) {
            tvNoMessage.setVisibility(View.VISIBLE);
        } else {
            tvNoMessage.setVisibility(View.GONE);
        }
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("notification", (Notification) view.getTag());
                startActivity(intent);
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

    private class OnViewClickedListener implements MessageAdapter.ViewClickedListener {

        @Override
        public void onViewClicked(View view, int position) {
            if (view.getId() == R.id.iv_avatar) {
                Intent intent = new Intent(getActivity(), UserHomeActivity.class);
                Comment comment = CommonMethods.createDefaultGson().fromJson(messageAdapter.getItem(position).getContent(), Comment.class);
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(comment.getUser()));
                startActivity(intent);
            }
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
