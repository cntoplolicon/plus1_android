package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.oneplusapp.R;
import com.oneplusapp.activity.EventRecommendActivity;
import com.oneplusapp.adapter.RecommendationsAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecommendFragment extends Fragment {

    @Bind(R.id.lv_list_view)
    ListView lvListView;
    @Bind(R.id.fl_loading_layout)
    View loadingView;
    @Bind(R.id.fl_empty_layout)
    View emptyView;

    private RecommendationsAdapter adapter;

    private void changeViewsByAdapterState() {
        boolean loading = adapter.isLoading();
        boolean isEmpty = adapter.isEmpty();
        if (!isEmpty) {
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
        adapter = new RecommendationsAdapter(getActivity());
        adapter.registerCallback(new RecommendationsAdapter.LoadingStatusObserver() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                changeViewsByAdapterState();
            }
        });
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), EventRecommendActivity.class);
                intent.putExtra("event_id", (int) id);
                startActivity(intent);
            }
        });

        lvListView.setAdapter(adapter);
        adapter.loadEvents();
        changeViewsByAdapterState();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadEvents();
    }

}