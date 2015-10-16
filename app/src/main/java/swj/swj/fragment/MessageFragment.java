package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.MessageAdapter;
import swj.swj.model.Notification;
import swj.swj.model.User;


public class MessageFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        List<Notification> notifications = Notification.getMyNotifications(User.current.getId());
        final MessageAdapter messageAdapter = new MessageAdapter(getActivity(), notifications);
        ListView lvListView = (ListView) view.findViewById(R.id.lv_listView);
        lvListView.setAdapter(messageAdapter);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("notification", (Notification)view.getTag());
                startActivity(intent);
            }
        });
        return view;
    }

}
