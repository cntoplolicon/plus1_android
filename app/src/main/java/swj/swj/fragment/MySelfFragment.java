package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.activity.PersonalProfileActivity;
import swj.swj.adapter.UserBookmarksGridViewAdapter;
import swj.swj.adapter.UserPostsGridViewAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.BookmarkService;
import swj.swj.common.CommonMethods;
import swj.swj.model.User;
import swj.swj.view.HeaderGridView;


public class MySelfFragment extends Fragment {

    private View headerView;
    private HeaderGridView gridView;
    private UserPostsGridViewAdapter postsAdapter;
    private UserBookmarksGridViewAdapter bookmarksAdapter;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        gridView = (HeaderGridView) view.findViewById(R.id.grid_view_authored_posts);
        headerView = inflater.inflate(R.layout.fragment_myself_header, null);
        gridView.addHeaderView(headerView, null, false);

        postsAdapter = new UserPostsGridViewAdapter(getActivity(), User.current.getId());
        bookmarksAdapter = new UserBookmarksGridViewAdapter(getActivity());
        BookmarkService.getInstance().setCallback(new BookmarkChangedCallback());
        gridView.setAdapter(postsAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });

        ButterKnife.bind(this, headerView);
        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                gridView.setAdapter(checkedId == R.id.tv_myself_publish ? postsAdapter : bookmarksAdapter);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        postsAdapater.notifyDataSetChanged();
        bookmarksAdapater.notifyDataSetChanged();
        showCurrentUserInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BookmarkService.getInstance().setCallback(null);
    }

    private void showCurrentUserInfo() {
        tvNickname.setText(User.current.getNickname());
        CommonMethods.chooseNicknameColorViaGender(tvNickname, User.current, getActivity().getBaseContext());
        tvBiography.setText(User.current.getBiography());
        if (User.current.getGender() == User.GENDER_UNKNOWN) {
            ivGender.setVisibility(View.INVISIBLE);
        } else {
            ivGender.setVisibility(View.VISIBLE);
            int resource = User.current.getGender() == User.GENDER_FEMALE ?
                    R.drawable.icon_woman : R.drawable.icon_man;
            ivGender.setImageResource(resource);
        }
        String avatarUrl = User.current.getAvatar();
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + avatarUrl, ivAvatar);
        }
        ivAvatar.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), PersonalProfileActivity.class));
    }

    private class BookmarkChangedCallback implements BookmarkService.Callback {

        @Override
        public void onBookmarkChanged() {
            bookmarksAdapter.updateContent(BookmarkService.getInstance().getBookmarkedPosts());
        }
    }
}
