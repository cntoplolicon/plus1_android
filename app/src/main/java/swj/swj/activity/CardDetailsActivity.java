package swj.swj.activity;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.BuildConfig;
import swj.swj.R;
import swj.swj.adapter.CardDetailsAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.BookmarkService;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.PushNotificationService;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;
import swj.swj.model.Comment;
import swj.swj.model.Notification;
import swj.swj.model.Post;
import swj.swj.model.User;

public class CardDetailsActivity extends BaseActivity {

    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.iv_image)
    ImageView ivImage;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.tv_comments)
    TextView tvComments;
    @Bind(R.id.tv_views)
    TextView tvViews;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_bookmark)
    ImageView ivBookmark;
    @Bind(R.id.et_new_comment)
    EditText etNewComment;

    ListView lvListView;

    private Post post;
    private CardDetailsAdapter cardDetailsAdapter;
    private Comment replyTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        initListView();
        ButterKnife.bind(this);
        etNewComment.requestFocus();
        String postJson = getIntent().getStringExtra("post_json");
        if (postJson != null) {
            post = CommonMethods.createDefaultGson().fromJson(postJson, Post.class);
            if (post.getPostPages()[0].getImageWidth() != null) {
                CommonMethods.setImageViewSize(ivImage, post.getPostPages()[0].getImageWidth(), post.getPostPages()[0].getImageHeight());
            }
            updatePostInfo();
        }

        Notification notification = getIntent().getParcelableExtra("notification");
        if (notification != null && notification.getType().equals(PushNotificationService.TYPE_COMMENT)) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            if (!BuildConfig.DEBUG) {
                NotificationManager notifyManager = (NotificationManager) getSystemService(Application.NOTIFICATION_SERVICE);
                notifyManager.cancelAll();
            }
            loadPost(comment.getPostId());
        }

        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replyTarget = (Comment) view.getTag();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                etNewComment.setHint(String.format(getResources().getString(R.string.reply_to_comment_format), replyTarget.getUser().getNickname()));
            }
        });

    }

    private void initListView() {
        lvListView = (ListView) findViewById(R.id.lv_listview);
        lvListView.setDividerHeight(0);
        final View headerView = LayoutInflater.from(this).inflate(R.layout.card_details_header, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replyTarget = null;
                hideInput(headerView);
                etNewComment.setHint(getResources().getString(R.string.publish_comment));
            }
        });
        lvListView.addHeaderView(headerView, null, false);
    }

    private void loadPost(int postId) {
        RestClient.getInstance().getPost(postId).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                post = CommonMethods.createDefaultGson().fromJson(result.toString(), Post.class);
                if (post.getPostPages()[0].getImageWidth() != null) {
                    CommonMethods.setImageViewSize(ivImage, post.getPostPages()[0].getImageWidth(), post.getPostPages()[0].getImageHeight());
                }
                updatePostInfo();
            }
        }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

    private void updatePostInfo() {
        tvContent.setText(post.getPostPages()[0].getText());
        tvNickname.setText(post.getUser().getNickname());
        tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(post.getUser()));
                startActivity(intent);
            }
        });
        int genderIcon;
        switch (post.getUser().getGender()) {
            case User.GENDER_FEMALE:
                genderIcon = R.drawable.icon_woman;
                break;
            case User.GENDER_MALE:
                genderIcon = R.drawable.icon_man;
                break;
            default:
                genderIcon = 0;
                break;
        }
        CommonMethods.chooseNicknameColorViaGender(tvNickname, post.getUser(), getBaseContext());
        String avatarUrl = post.getUser().getAvatar();
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + avatarUrl, ivAvatar);
        }
        tvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, genderIcon, 0);
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        tvViews.setText(String.valueOf(post.getViewsCount()));
        String createdAtFormat = getResources().getString(R.string.post_created_at);
        int daysAgo = Days.daysBetween(post.getCreatedAt().toLocalDate(),
                DateTime.now().toLocalDate()).getDays();
        tvTime.setText(String.format(createdAtFormat, daysAgo));
        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null) {
            ivImage.setVisibility(View.GONE);
            tvContent.setTextSize(getResources().getDimension(R.dimen.no_image_text_size_card_details));
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.drawable.image_loading)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + imageUrl, ivImage, options);
        }

        syncBookmarkInfo();
        loadComments();
    }

    private void loadComments() {
        cardDetailsAdapter = new CardDetailsAdapter(this, post);
        cardDetailsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                tvComments.setText(String.valueOf(cardDetailsAdapter.getCount()));
            }
        });
        cardDetailsAdapter.setOnViewClickedListener(new OnViewClickedListener());
        lvListView.setAdapter(cardDetailsAdapter);
    }

    @OnClick(R.id.iv_bookmark)
    public void onBookmarkClicked(View view) {
        view.setEnabled(false);
        if (!BookmarkService.getInstance().isBookmarked(post)) {
            ivBookmark.setImageResource(R.drawable.icon_bookmark_checked);
            RestClient.getInstance().createBookmark(post.getId())
                    .fail(new JsonErrorListener(getApplicationContext(), null))
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view) {
                        @Override
                        public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                            super.onAlways(state, resolved, rejected);
                            if (state == Promise.State.RESOLVED) {
                                Toast.makeText(CardDetailsActivity.this, getResources().getString(R.string.bookmark_added), Toast.LENGTH_SHORT).show();
                                BookmarkService.getInstance().addBookmark(post);
                            }
                            syncBookmarkInfo();
                        }
                    });
        } else {
            ivBookmark.setImageResource(R.drawable.icon_bookmark);
            RestClient.getInstance().removeBookmark(post.getId())
                    .fail(new JsonErrorListener(getApplicationContext(), null))
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view) {
                        @Override
                        public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                            super.onAlways(state, resolved, rejected);
                            if (state == Promise.State.RESOLVED) {
                                Toast.makeText(CardDetailsActivity.this, getResources().getString(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
                                BookmarkService.getInstance().removeBookmark(post);
                            }
                            syncBookmarkInfo();
                        }
                    });
        }

    }

    @OnClick(R.id.btn_send_comment)
    public void onSendCommentClicked(View view) {
        if (etNewComment.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.comment_text_required), Toast.LENGTH_LONG).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        view.setEnabled(false);
        int replyTargetId = replyTarget == null ? -1 : replyTarget.getId();
        RestClient.getInstance().newComment(etNewComment.getText().toString(), replyTargetId, post.getId())
                .done(new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject result) {
                        etNewComment.setText("");
                        Toast.makeText(getApplicationContext(), R.string.comment_success, Toast.LENGTH_LONG).show();
                        Comment newComment = CommonMethods.createDefaultGson().fromJson(result.toString(), Comment.class);
                        cardDetailsAdapter.add(newComment);
                        cardDetailsAdapter.sortComments();
                        cardDetailsAdapter.notifyDataSetChanged();
                    }
                })
                .fail(new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onFail(VolleyError error) {
                        super.onFail(error);
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.comment_failure, Toast.LENGTH_LONG).show();
                    }
                })
                .always(new ResetViewClickable<JSONObject, VolleyError>(view));
    }

    private void syncBookmarkInfo() {
        if (BookmarkService.getInstance().isBookmarked(post)) {
            ivBookmark.setImageResource(R.drawable.icon_bookmark_checked);
        } else {
            ivBookmark.setImageResource(R.drawable.icon_bookmark);
        }
    }

    private class OnViewClickedListener implements CardDetailsAdapter.ViewClickedListener {

        @Override
        public void onViewClick(View view, int position) {
            if (view.getId() == R.id.tv_nickname || view.getId() == R.id.iv_avatar) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(cardDetailsAdapter.getItem(position).getUser()));
                hideInput(view);
                startActivity(intent);
            } else if (view.getId() == R.id.tv_reply_target) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                int replyTargetId = cardDetailsAdapter.getItem(position).getReplyToId();
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(cardDetailsAdapter.getCommentById(replyTargetId).getUser()));
                hideInput(view);
                startActivity(intent);
            }
        }

    }

    private void hideInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
