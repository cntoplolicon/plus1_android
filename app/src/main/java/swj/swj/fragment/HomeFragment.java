package swj.swj.fragment;

import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.activity.UserHomeActivity;
import swj.swj.adapter.HomePageListItemViewsAdapter;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.view.HomePageLayout;

public class HomeFragment extends BaseFragment {

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

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        adapter = HomePageListItemViewsAdapter.getInstance();
        slidingView.setAdapter(adapter);

        slidingView.setCallback(new LayoutCallbacks());
        adapter.setCallback(new AdapterCallbacks());

        changeViewsByAdapterState(adapter.getState());
    }

    private class LayoutCallbacks implements HomePageLayout.Callback {

        @Override
        public void onViewAdded(View view) {
            view.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), CardDetailsActivity.class));
            TextView tvUser = (TextView) view.findViewById(R.id.tv_user);
            tvUser.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), UserHomeActivity.class));
        }

        @Override
        public void onViewReleased(View view) {

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