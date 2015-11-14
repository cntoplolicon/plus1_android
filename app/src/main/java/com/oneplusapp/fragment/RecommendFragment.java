package com.oneplusapp.fragment;

import android.annotation.TargetApi;
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
import com.oneplusapp.adapter.RecommendStaggeredGridAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.Post;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecommendFragment extends Fragment {

    @Bind(R.id.id_recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.fl_loading_layout)
    View loadingView;
    @Bind(R.id.fl_empty_layout)
    View emptyView;

    private RecommendStaggeredGridAdapter adapter;

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

    @TargetApi(23)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        ButterKnife.bind(this, view);
        adapter = new RecommendStaggeredGridAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                changeViewsByAdapterState();
            }
        });

        adapter.registerCallback(new RecommendStaggeredGridAdapter.Callback() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                changeViewsByAdapterState();
            }
        });
        changeViewsByAdapterState();
        adapter.notifyLoadingStatusChanged();
        adapter.setOnItemClickListener(new RecommendStaggeredGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Post post) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(post));
                startActivity(intent);
            }
        });
        recyclerView.addOnScrollListener(new NewPauseOnScrollListener(ImageLoader.getInstance(), false, true));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.loadRecommendations();
    }

    public class NewPauseOnScrollListener extends RecyclerView.OnScrollListener {

        private ImageLoader imageLoader;

        private final boolean pauseOnScroll;
        private final boolean pauseOnSettling;
        private final RecyclerView.OnScrollListener externalListener;

        public NewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling) {
            this(imageLoader, pauseOnScroll, pauseOnSettling, null);
        }

        public NewPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling,
                                        RecyclerView.OnScrollListener customListener) {
            this.imageLoader = imageLoader;
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnSettling = pauseOnSettling;
            externalListener = customListener;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    imageLoader.resume();
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    if (pauseOnScroll) {
                        imageLoader.pause();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    if (pauseOnSettling) {
                        imageLoader.pause();
                    }
                    break;
            }
            if (externalListener != null) {
                externalListener.onScrollStateChanged(recyclerView, newState);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (externalListener != null) {
                externalListener.onScrolled(recyclerView, dx, dy);
            }
        }
    }
}