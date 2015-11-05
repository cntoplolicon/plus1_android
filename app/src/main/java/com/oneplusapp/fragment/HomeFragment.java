package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.activity.HomeActivity;
import com.oneplusapp.activity.UserHomeActivity;
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

    @Bind(R.id.loading_layout)
    View loadingView;
    @Bind(R.id.cleared_layout)
    View clearedView;
    @Bind(R.id.sliding_layout)
    HomePageLayout slidingView;

    private InfectionsAdapter adapter;
    private LoadNewInfectionsTimer timer = new LoadNewInfectionsTimer(LOADING_INTERVAL * 20);

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        slidingView.setCallback(new LayoutCallbacks());
        adapter = InfectionsAdapter.getInstance();
        adapter.setCallback(new AdapterCallbacks());
        slidingView.setAdapter(adapter);
        changeViewsByAdapterState(adapter.getState());
        timer.start();
        return view;
    }

    @OnClick(R.id.tv_hot)
    public void skipRecommend() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        RadioButton radioButton = (RadioButton) homeActivity.findViewById(R.id.rb_recommendation);
        radioButton.setChecked(true);
        homeActivity.switchTab(R.id.rb_recommendation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.setCallback(null);
        timer.cancel();
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

            TextView tvUser = (TextView) view.findViewById(R.id.tv_nickname);
            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), UserHomeActivity.class);
                    intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(infection.getPost().getUser()));
                    startActivity(intent);
                }
            });

            ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserHomeActivity.class);
                    intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(infection.getPost().getUser()));
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