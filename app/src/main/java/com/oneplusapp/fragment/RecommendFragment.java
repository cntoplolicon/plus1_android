package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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

    private static final int ITEM_HORIZONTAL_SPACING = 4;
    private static final int ITEM_VERTICAL_SPACING = 7;

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
        recyclerView.addItemDecoration(new SpacesItemDecoration(dip2px(getActivity(), ITEM_HORIZONTAL_SPACING), dip2px(getActivity(), ITEM_VERTICAL_SPACING)));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(null);
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

    private static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int horizontalSpace;
        private int verticalSpace;

        public SpacesItemDecoration(int horizontalSpace, int verticalSpace) {
            this.horizontalSpace = horizontalSpace;
            this.verticalSpace = verticalSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalSpace;
            outRect.right = horizontalSpace;
            outRect.bottom = verticalSpace;
            outRect.top = verticalSpace;
        }
    }
}