package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
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
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.activity.HomeActivity;
import com.oneplusapp.adapter.InfectionsAdapter;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Infection;
import com.oneplusapp.model.PostView;
import com.oneplusapp.view.HomePageLayout;

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
    HomePageLayout slidingView;
    @Bind(R.id.fragments_container)
    View fragmentsContainerView;

    private InfectionsAdapter adapter;
    private LoadNewInfectionsTimer timer = new LoadNewInfectionsTimer(LOADING_INTERVAL * 20);
    private AdapterCallbacks adapterCallback = new AdapterCallbacks();

    private void changeViewsByAdapterState(int state) {
        switch (state) {
            case InfectionsAdapter.STATE_CLEARED:
                clearedView.bringToFront();
                break;
            case InfectionsAdapter.STATE_LOADING:
                loadingView.bringToFront();
                break;
            case InfectionsAdapter.STATE_NORMAL:
                slidingView.bringToFront();
                break;
            default:
                throw new IllegalArgumentException("unknown state " + state);
        }
        fragmentsContainerView.invalidate();
        fragmentsContainerView.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        slidingView.setCallback(new LayoutCallbacks());
        adapter = new InfectionsAdapter(getActivity());
        adapter.registerCallback(adapterCallback);
        slidingView.setAdapter(adapter);
        changeViewsByAdapterState(adapter.getState());
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
        adapter.unregisterCallback(adapterCallback);
        timer.cancel();
    }

    @OnClick(R.id.tv_hot)
    public void skipRecommend() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        RadioButton radioButton = (RadioButton) homeActivity.findViewById(R.id.rb_recommendation);
        radioButton.setChecked(true);
        homeActivity.switchTab(R.id.rb_recommendation);
    }

    private class LayoutCallbacks implements HomePageLayout.Callback {

        @Override
        public void onViewAdded(View view) {
            final Infection infection = (Infection) view.getTag();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                    intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(infection.getPost()));
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onViewReleased(View view, int offset) {
            Infection infection = (Infection) view.getTag();
            int result = offset > 0 ? PostView.POST_VIEW_SKIP : PostView.POST_VIEW_SPREAD;
            RestClient.getInstance().newPostView(infection.getId(), result)
                    .fail(new JsonErrorListener(getActivity(), null));
            adapter.checkRemainingInfectionsAndUpdate();
        }
    }

    private class LoadNewInfectionsTimer extends CountDownTimer {

        public LoadNewInfectionsTimer(long millisInFuture) {
            super(millisInFuture, (long) LOADING_INTERVAL);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (adapter.getState() == InfectionsAdapter.STATE_CLEARED) {
                adapter.loadInfections();
            }
        }

        @Override
        public void onFinish() {
            start();
        }
    }

    private class AdapterCallbacks implements InfectionsAdapter.Callback {

        @Override
        public void onStateChanged(int oldState, int newState) {
            if (newState == InfectionsAdapter.STATE_NORMAL) {
                slidingView.syncContentViews();
            }
            changeViewsByAdapterState(newState);
        }
    }
}