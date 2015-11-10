package com.oneplusapp.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.oneplusapp.R;
import com.oneplusapp.activity.CardDetailsActivity;
import com.oneplusapp.activity.PersonalProfileActivity;
import com.oneplusapp.activity.ShowImageActivity;
import com.oneplusapp.adapter.PostsGridViewAdapter;
import com.oneplusapp.adapter.UserBookmarksGridViewAdapter;
import com.oneplusapp.adapter.UserPostsGridViewAdapter;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.model.User;
import com.oneplusapp.view.HeaderGridView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MySelfFragment extends Fragment {

    private View headerView;
    private UserPostsGridViewAdapter postsAdapter;
    private UserBookmarksGridViewAdapter bookmarksAdapter;
    private User user;
    private HeaderGridView gridView;
    private ImageView ivLoading;
    private TextView tvContentEmpty;

    @Bind(R.id.tv_biography)
    TextView tvBiography;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_gender)
    ImageView ivGender;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.rg_group)
    RadioGroup radioGroup;

    private void changeViewsByAdapterState() {
        PostsGridViewAdapter adapter = radioGroup.getCheckedRadioButtonId() == R.id.tv_myself_publish ? postsAdapter : bookmarksAdapter;
        boolean loading = adapter.isLoading();
        boolean isEmpty = adapter.isEmpty();
        if (!isEmpty) {
            ivLoading.setVisibility(View.GONE);
            tvContentEmpty.setVisibility(View.GONE);
        } else if (loading) {
            tvContentEmpty.setVisibility(View.GONE);
            ivLoading.setVisibility(View.VISIBLE);
        } else {
            ivLoading.setVisibility(View.GONE);
            tvContentEmpty.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        gridView = (HeaderGridView) view.findViewById(R.id.grid_view_authored_posts);
        ivLoading = (ImageView) view.findViewById(R.id.iv_loading);
        tvContentEmpty = (TextView) view.findViewById(R.id.tv_content_empty);
        tvContentEmpty.setText(R.string.home_no_publish);
        headerView = inflater.inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView, null, false);
        ButterKnife.bind(this, headerView);
        user = User.current;
        String userJson = getActivity().getIntent().getStringExtra("user_json");
        if (userJson != null) {
            user = CommonMethods.createDefaultGson().fromJson(userJson, User.class);
        }
        if (user.getId() == User.current.getId()) {
            ivAvatar.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), PersonalProfileActivity.class));
            bookmarksAdapter = new UserBookmarksGridViewAdapter(getActivity());
            bookmarksAdapter.registerDataSetObserver(new CustomDataSetObserver());
            bookmarksAdapter.registerCallback(new CustomPostGridViewCallback());
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.tv_myself_publish) {
                        gridView.setAdapter(postsAdapter);
                        tvContentEmpty.setText(R.string.home_no_publish);
                    } else {
                        gridView.setAdapter(bookmarksAdapter);
                        tvContentEmpty.setText(R.string.home_no_bookmard);
                    }
                    changeViewsByAdapterState();
                }
            });
        } else {
            radioGroup.setVisibility(View.GONE);
            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                    intent.putExtra("image_url", user.getAvatar());
                    startActivity(intent);
                }
            });
        }
        postsAdapter = new UserPostsGridViewAdapter(getActivity(), user.getId());
        gridView.setAdapter(postsAdapter);
        postsAdapter.registerDataSetObserver(new CustomDataSetObserver());
        postsAdapter.registerCallback(new CustomPostGridViewCallback());
        changeViewsByAdapterState();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });

        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user.getId() == User.current.getId()) {
            user = User.current;
        }
        showCurrentUserInfo();
        gridView.invalidateViews();
        if (bookmarksAdapter != null) {
            bookmarksAdapter.loadBookmarks();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void showCurrentUserInfo() {
        tvNickname.setText(user.getNickname());
        CommonMethods.chooseNicknameColorViaGender(tvNickname, user, getActivity().getBaseContext());
        tvBiography.setText(user.getBiography());
        if (user.getGender() == User.GENDER_UNKNOWN) {
            ivGender.setVisibility(View.INVISIBLE);
        } else {
            ivGender.setVisibility(View.VISIBLE);
            int resource = user.getGender() == User.GENDER_FEMALE ?
                    R.drawable.icon_woman : R.drawable.icon_man;
            ivGender.setImageResource(resource);
        }
        if (user.getAvatar() != null) {
            ImageLoader.getInstance().displayImage(user.getAvatar(), ivAvatar);
        }
    }

    private class CustomDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            changeViewsByAdapterState();
        }
    }

    private class CustomPostGridViewCallback implements PostsGridViewAdapter.Callback {

        @Override
        public void onLoadingStatusChanged(boolean loading) {
            changeViewsByAdapterState();
        }
    }
}