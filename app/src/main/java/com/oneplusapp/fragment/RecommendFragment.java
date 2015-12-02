package com.oneplusapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.adapter.RecommendationsAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Event;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecommendFragment extends Fragment {

    private boolean loading = false;

    @Bind(R.id.lv_list_view)
    ListView lvListView;
    @Bind(R.id.fl_loading_layout)
    View loadingView;
    @Bind(R.id.fl_empty_layout)
    View emptyView;

    private RecommendationsAdapter adapter;

    private void changeViewsByAdapterState() {
        if (adapter.getCount() != 0) {
            lvListView.bringToFront();
        } else if (loading) {
            loadingView.bringToFront();
        } else {
            emptyView.bringToFront();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        ButterKnife.bind(this, view);

        lvListView.setDividerHeight(0);
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event eventClicked = (Event) view.getTag();
                Toast.makeText(getActivity(), eventClicked.getLogo(), Toast.LENGTH_LONG).show();
            }
        });

        adapter = new RecommendationsAdapter(getActivity());
        lvListView.setAdapter(adapter);
        loadEvents();
        changeViewsByAdapterState();

        return view;
    }

    private void loadEvents() {
        if (loading) {
            return;
        }
        loading = true;
        RestClient.getInstance().getAllEvents().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray result) {
                        final Event[] tmpEvents = CommonMethods.createDefaultGson().fromJson(result.toString(), Event[].class);
                        adapter.clear();
                        adapter.addAll(tmpEvents);
                        adapter.notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(getActivity(), null))
                .always(new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        changeViewsByAdapterState();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvents();
    }

}