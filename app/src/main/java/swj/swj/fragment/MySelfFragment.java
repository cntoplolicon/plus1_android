package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.UserBookmarksGridViewAdapter;
import swj.swj.adapter.UserPostsGridViewAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.CommonMethods;
import swj.swj.model.User;
import swj.swj.view.HeaderGridView;


public class MySelfFragment extends Fragment {

    private View headerView;
    private HeaderGridView gridView;
    private UserPostsGridViewAdapter postsAdapater;
    private UserBookmarksGridViewAdapter bookmarksAdapater;
    private int currentTab = R.id.tv_myself_publish;

    @Bind(R.id.tv_myself_publish)
    TextView tvMyPublish;

    @Bind(R.id.tv_myself_collect)
    TextView tvMyCollection;

    @Bind(R.id.tv_biography)
    TextView tvBiography;

    @Bind(R.id.tv_nickname)
    TextView tvNickname;

    @Bind(R.id.iv_gender)
    ImageView ivGender;

    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);

        gridView = (HeaderGridView) view.findViewById(R.id.grid_view_authored_posts);
        headerView = inflater.inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView);

        postsAdapater = new UserPostsGridViewAdapter(getActivity(), User.current.getId());
        bookmarksAdapater = new UserBookmarksGridViewAdapter(getActivity());
        gridView.setAdapter(postsAdapater);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });

        ButterKnife.bind(this, headerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showCurrentUserInfo();
    }

    private void showCurrentUserInfo() {
        tvNickname.setText(User.current.getNickname());
        tvBiography.setText(User.current.getBiography());
        if (User.current.getGender() == User.GENDER_UNKNOWN) {
            ivGender.setVisibility(View.INVISIBLE);
        } else {
            ivGender.setVisibility(View.VISIBLE);
            int resource = User.current.getGender() == User.GENDER_FEMALE ?
                    R.drawable.icon_woman : R.drawable.icon_man;
            ivGender.setImageResource(resource);
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + User.current.getAvatar(), ivAvatar, options);
    }

    @OnClick({R.id.tv_myself_publish, R.id.tv_myself_collect})
    public void onTabClicked(TextView view) {
        view.setBackgroundResource(R.drawable.personal_tv_border_left_pressed);
        TextView theOtherTab = view.getId() == R.id.tv_myself_collect ? tvMyPublish : tvMyCollection;
        theOtherTab.setBackgroundResource(R.drawable.personal_tv_border_left_unpressed);
        if (view.getId() != currentTab) {
            currentTab = view.getId();
            gridView.setAdapter(view.getId() == R.id.tv_myself_publish ? postsAdapater : bookmarksAdapater);
        }
    }
}
