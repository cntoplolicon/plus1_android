package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.MessageAdapter;
import swj.swj.model.Notification;


public class MessageFragment extends Fragment {

    private List<Notification> notifications;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        notifications = Notification.getAll();
        Collections.reverse(notifications);
        final MessageAdapter messageAdapter = new MessageAdapter(this.getActivity(), notifications);
        ListView lvListView = (ListView) view.findViewById(R.id.lv_listView);
        lvListView.setAdapter(messageAdapter);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("notification", messageAdapter.getItem(position));
                startActivity(intent);
            }
        });
        return view;
    }

}
