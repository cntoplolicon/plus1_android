package swj.swj.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.activity.UserHomeActivity;
import swj.swj.adapter.HomePageListItemViewsAdapter;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Infection;
import swj.swj.model.PostView;
import swj.swj.view.HomePageLayout;

public class HomeFragment extends Fragment {

    @Bind(R.id.loading_layout)
    View loadingView;
    @Bind(R.id.cleared_layout)
    View clearedView;
    @Bind(R.id.sliding_layout)
    HomePageLayout slidingView;

    private HomePageListItemViewsAdapter adapter;

    private void changeViewsByAdapterState(int state) {
        switch (state) {
            case HomePageListItemViewsAdapter.STATE_CLEARED:
                clearedView.bringToFront();
                break;
            case HomePageListItemViewsAdapter.STATE_LOADING:
                loadingView.bringToFront();
                break;
            case HomePageListItemViewsAdapter.STATE_NORMAL:
                slidingView.bringToFront();
                break;
            default:
                throw new IllegalArgumentException("unknown state " + state);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        adapter = HomePageListItemViewsAdapter.getInstance();
        slidingView.setAdapter(adapter);

        slidingView.setCallback(new LayoutCallbacks());
        adapter.setCallback(new AdapterCallbacks());

        changeViewsByAdapterState(adapter.getState());

        return view;
    }

    private class LayoutCallbacks implements HomePageLayout.Callback {

        @Override
        public void onViewAdded(View view) {
            view.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), CardDetailsActivity.class));
            TextView tvUser = (TextView) view.findViewById(R.id.tv_user);
            tvUser.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), UserHomeActivity.class));
        }

        @Override
        public void onViewReleased(View view, int offset) {
            Infection infection = adapter.getInfectionByView(view);
            int result = offset > 0 ? PostView.POST_VIEW_SKIP : PostView.POST_VIEW_SPREAD;
            RestClient.getInstance().newPostView(infection.getId(), result,
                    null, new JsonErrorListener(getActivity(), null));
        }
    }

    private class AdapterCallbacks implements HomePageListItemViewsAdapter.Callback {

        @Override
        public void onStateChanged(int oldState, int newState) {
            if (newState == HomePageListItemViewsAdapter.STATE_NORMAL) {
                slidingView.syncContentViews();
            }
            changeViewsByAdapterState(newState);
        }
    }
}