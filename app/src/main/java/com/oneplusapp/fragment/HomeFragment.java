package com.oneplusapp.fragment;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.oneplusapp.view.DraggableStackView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    private static final int LOADING_INTERVAL = 15000;

    private static Bitmap bitmapSkip;
    private static Bitmap bitmapSpread;

    @Bind(R.id.iv_skip)
    ImageView ivSkip;
    @Bind(R.id.iv_spread)
    ImageView ivSpread;
    @Bind(R.id.loading_layout)
    View loadingView;
    @Bind(R.id.cleared_layout)
    View clearedView;
    @Bind(R.id.sliding_layout)
    DraggableStackView stackView;

    private InfectionsAdapter adapter;
    private LoadNewInfectionsTimer timer = new LoadNewInfectionsTimer(LOADING_INTERVAL * 20);

    private void updateFrontView() {
        View frontView = stackView;
        if (adapter.getCount() == 0) {
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
                updateFrontView();
            }
        });
        updateFrontView();

        stackView.setOnViewReleasedListener(new StackedViewReleasedListener());
        stackView.setAdapter(adapter);

        timer.start();
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).build();
        if (bitmapSkip == null) {
            bitmapSkip = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.skip, displayOptions);
        }
        ivSkip.setImageBitmap(bitmapSkip);
        if (bitmapSpread == null) {
            bitmapSpread = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.spread, displayOptions);
        }
        ivSpread.setImageBitmap(bitmapSpread);

        return view;
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
        homeActivity.switchTab(R.id.rb_recommendation);
    }

    private class StackedViewReleasedListener implements DraggableStackView.OnViewReleasedListener {

        @Override
        public void onViewReleased(View view, int offset) {
            Infection infection = (Infection) view.getTag();
            int result = offset > 0 ? PostView.POST_VIEW_SKIP : PostView.POST_VIEW_SPREAD;
            RestClient.getInstance().newPostView(infection.getId(), result)
                    .fail(new JsonErrorListener(getActivity(), null));
            adapter.checkRemainingInfectionsAndLoad();
        }

        @Override
        public void onReleasedViewSettled(View view, int offset) {
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
            if (!adapter.isLoading() && adapter.getCount() == 0) {
                adapter.loadInfections();
            }
        }

        @Override
        public void onFinish() {
            start();
        }
    }
}