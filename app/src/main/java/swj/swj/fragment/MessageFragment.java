package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.MessageAdapter;
import swj.swj.common.PushNotificationService;
import swj.swj.model.Notification;
import swj.swj.model.User;


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

    private class NotificationChangedCallback implements PushNotificationService.Callback {
        @Override
        public void onNotificationReceived(Notification notification) {
            messageAdapter.insert(notification, 0);
            messageAdapter.notifyDataSetChanged();
        }
    }
}
