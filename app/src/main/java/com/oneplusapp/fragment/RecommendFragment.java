package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.adapter.PostsGridViewAdapter;
import com.oneplusapp.adapter.RecommendGridViewAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.view.HeaderGridView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecommendFragment extends Fragment {

    @Bind(R.id.grid_view_concentration)
    HeaderGridView gridView;
    @Bind(R.id.fl_loading_layout)
    View loadingView;
    @Bind(R.id.fl_empty_layout)
    View emptyView;

    private RecommendGridViewAdapter adapter;

    private void changeViewsByAdapterState() {
        boolean loading = adapter.isLoading();
        boolean isEmpty = adapter.isEmpty();
        if (!isEmpty) {
            gridView.bringToFront();
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

        adapter = new RecommendGridViewAdapter(getActivity());
        gridView.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                changeViewsByAdapterState();
            }
        });
        adapter.registerLoadingStatusObserver(new PostsGridViewAdapter.LoadingStatusObserver() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                changeViewsByAdapterState();
            }
        });
        changeViewsByAdapterState();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });
        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadRecommendations();
    }
}