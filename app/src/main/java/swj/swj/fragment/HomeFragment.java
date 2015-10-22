package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.activity.UserHomeActivity;
import swj.swj.adapter.InfectionsAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.CommonMethods;
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

    private InfectionsAdapter adapter;

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
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.setCallback(null);
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
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + infection.getPost().getUser().getAvatar(), ivAvatar);
        }

        @Override
        public void onViewReleased(View view, int offset) {
            Infection infection = (Infection) view.getTag();
            int result = offset > 0 ? PostView.POST_VIEW_SKIP : PostView.POST_VIEW_SPREAD;
            RestClient.getInstance().newPostView(infection.getId(), result)
                    .fail(new JsonErrorListener(getActivity(), null));
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