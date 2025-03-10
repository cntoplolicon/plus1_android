package com.oneplusapp.fragment;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.activity.HomeActivity;
import com.oneplusapp.adapter.InfectionsAdapter;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Infection;
import com.oneplusapp.model.PostView;
import com.oneplusapp.model.User;
import com.oneplusapp.view.DraggableStackView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    private static final int LOADING_INTERVAL = 15000;
    private static final int DRAG_OFFSET_LIMIT_FACTOR = 3;
    private DisplayImageOptions HEADER_FOOTER_DISPLAY_OPTION =
            new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).build();

    private static Bitmap bitmapSkip;
    private static Bitmap bitmapSpread;

    @Bind(R.id.loading_layout)
    View loadingView;
    @Bind(R.id.cleared_layout)
    View clearedView;
    @Bind(R.id.sliding_layout)
    DraggableStackView stackView;

    private InfectionsAdapter adapter;
    private LoadNewInfectionsTimer timer = new LoadNewInfectionsTimer(LOADING_INTERVAL * 20);
    private boolean noData = true;

    private void updateFrontView() {
        View frontView = stackView;
        if (adapter.isEmpty()) {
            frontView = adapter.isLoading() ? loadingView : clearedView;
        }
        frontView.bringToFront();
        ViewGroup viewGroup = (ViewGroup) frontView.getParent();
        viewGroup.invalidate();
        viewGroup.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        adapter = new InfectionsAdapter(getActivity());
        adapter.registerLoadingStatusObserver(new InfectionsAdapter.LoadingStatusObserver() {
            @Override
            public void onLoadingStatusChanged(boolean loading) {
                updateFrontView();
            }
        });
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                if (!noData && adapter.isEmpty()) {
                    adapter.loadInfections();
                } else {
                    updateFrontView();
                }
                noData = adapter.isEmpty();
            }
        });

        noData = adapter.isEmpty();
        updateFrontView();
        initStackView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timer.start();
            }
        }, LOADING_INTERVAL);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.loadInfections();
    }

    private int getDesiredHeight(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        return view.getMeasuredHeight();
    }

    private void initStackView() {
        stackView.setOnViewReleasedListener(new StackedViewReleasedListener());
        stackView.setAdapter(adapter);

        if (bitmapSkip == null) {
            bitmapSkip = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.skip, HEADER_FOOTER_DISPLAY_OPTION);
        }
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.skip_layout, stackView, false);
        ((ImageView) headerView.findViewById(R.id.iv_skip)).setImageBitmap(bitmapSkip);
        stackView.setHeaderView(headerView);

        if (bitmapSpread == null) {
            bitmapSpread = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.spread, HEADER_FOOTER_DISPLAY_OPTION);
        }
        View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.spread_layout, stackView, false);
        ((ImageView) footerView.findViewById(R.id.iv_spread)).setImageBitmap(bitmapSpread);
        stackView.setFooterView(footerView);

        int dragOffsetLimit = 0;
        dragOffsetLimit = Math.max(dragOffsetLimit, getDesiredHeight(headerView.findViewById(R.id.tv_skip)));
        dragOffsetLimit = Math.max(dragOffsetLimit, getDesiredHeight(footerView.findViewById(R.id.tv_spread)));
        stackView.setDragOffsetLimit(dragOffsetLimit * DRAG_OFFSET_LIMIT_FACTOR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
    }

    @OnClick(R.id.tv_hot)
    public void skipRecommend() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        RadioButton radioButton = (RadioButton) homeActivity.findViewById(R.id.rb_recommendation);
        radioButton.setChecked(true);
    }

    private class StackedViewReleasedListener implements DraggableStackView.OnViewReleasedListener {

        @Override
        public void onViewReleased(View view, int position, int offset) {
            Infection infection = (Infection) view.getTag();
            int result = offset > 0 ? PostView.POST_VIEW_SKIP : PostView.POST_VIEW_SPREAD;
            RestClient.getInstance().newPostView(infection.getId(), result)
                    .fail(new JsonErrorListener(getActivity(), null));
            adapter.checkRemainingInfectionsAndLoad();
            adapter.setFirstViewReleasing(true);
        }

        @Override
        public void onReleasedViewSettled(View view, int position, int offset) {
            stackView.resetPosition();
            adapter.setFirstViewReleasing(false);
            adapter.pop();
            adapter.notifyDataSetChanged();
        }
    }

    private class LoadNewInfectionsTimer extends CountDownTimer {

        public LoadNewInfectionsTimer(long millisInFuture) {
            super(millisInFuture, (long) LOADING_INTERVAL);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (!adapter.isLoading() && adapter.getCount() == 0 && User.current != null) {
                adapter.loadInfections();
            }
        }

        @Override
        public void onFinish() {
            start();
        }
    }
}