package swj.swj.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import swj.swj.R;
import swj.swj.adapter.MessageAdapter;


public class MessageFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        MessageAdapter messageAdapter = new MessageAdapter(this.getActivity());
        ListView lvListView = (ListView) view.findViewById(R.id.lv_listview);
        lvListView.setAdapter(messageAdapter);
        return view;
    }

}
