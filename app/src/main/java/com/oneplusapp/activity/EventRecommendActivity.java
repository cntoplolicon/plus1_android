package com.oneplusapp.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.adapter.RecommendedEventAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.PauseOnScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EventRecommendActivity extends BaseActivity {

    private static final int ITEM_HORIZONTAL_SPACING = 4;
    private static final int ITEM_VERTICAL_SPACING = 7;

    @Bind(R.id.id_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.tv_content_empty)
    TextView tvContentEmpty;
    @Bind(R.id.iv_loading_recommends)
    ImageView ivLoadingRecommends;

    private RecommendedEventAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_recommend);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        int eventId = intent.getIntExtra("event_id", 0);

        recyclerView.addItemDecoration(new SpacesItemDecoration(dip2px(ITEM_HORIZONTAL_SPACING), dip2px(ITEM_VERTICAL_SPACING)));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(null);
        recyclerView.addOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        adapter = new RecommendedEventAdapter(this, eventId);
        adapter.loadRecommendations();
        adapter.setHasStableIds(true);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                changeViewsByAdapterState();
            }
        });
        adapter.registerLoadingStatusObserver(new RecommendedEventAdapter.LoadingStatusObserver() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                changeViewsByAdapterState();
            }
        });
        adapter.setOnItemClickListener(new RecommendedEventAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(EventRecommendActivity.this, CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(adapter.getItem(position)));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        changeViewsByAdapterState();
    }

    private void changeViewsByAdapterState() {
        boolean loading = adapter.isLoading();
        boolean isEmpty = adapter.isEmpty();

        tvContentEmpty.setVisibility(View.GONE);
        ivLoadingRecommends.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        if (!isEmpty) {
            recyclerView.setVisibility(View.VISIBLE);
        } else if (loading) {
            ivLoadingRecommends.setVisibility(View.VISIBLE);
        } else {
            tvContentEmpty.setVisibility(View.VISIBLE);
        }
    }

    private int dip2px(float dipValue) {
        float scale = getResources().getDisplayMetrics().density;
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
