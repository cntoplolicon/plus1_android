package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.adapter.RecommendationsAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.PauseOnScrollListener;
import com.oneplusapp.model.Post;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecommendFragment extends Fragment {

    @Bind(R.id.id_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.fl_loading_layout)
    View loadingView;
    @Bind(R.id.fl_empty_layout)
    View emptyView;

    private RecommendationsAdapter adapter;

    private void changeViewsByAdapterState() {
        boolean loading = adapter.isLoading();
        boolean isEmpty = adapter.isEmpty();

        if (!isEmpty) {
            recyclerView.bringToFront();
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
        adapter = new RecommendationsAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                changeViewsByAdapterState();
            }
        });

        adapter.registerCallback(new RecommendationsAdapter.LoadingStatusObserver() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                changeViewsByAdapterState();
            }
        });
        changeViewsByAdapterState();
        adapter.setOnItemClickListener(new RecommendationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Post post) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(post));
                startActivity(intent);
            }
        });
        recyclerView.addOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadRecommendations();
    }
}