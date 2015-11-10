package com.oneplusapp.activity;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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
import com.oneplusapp.BuildConfig;
import com.oneplusapp.R;
import com.oneplusapp.adapter.CommentsAdapter;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;
import com.oneplusapp.view.UserAvatarImageView;
import com.oneplusapp.view.UserNicknameTextView;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardDetailsActivity extends BaseActivity {

    @Bind(R.id.iv_avatar)
    UserAvatarImageView ivAvatar;
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
    @Bind(R.id.tv_nickname)
    UserNicknameTextView tvNickname;

    private ListView lvListView;

    private Post post;
    private CommentsAdapter commentsAdapter;
    private Comment replyTarget;
    private Comment notifiedComment;
    private boolean postImageDisplayed = false;

    private PushNotificationService.Callback notificationCallback = new PushNotificationService.Callback() {
        @Override
        public void onNotificationReceived(Notification notification) {
            if (!notification.getType().equals(Notification.TYPE_COMMENT)) {
                return;
            }
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            if (post != null && post.getId() == comment.getPostId()) {
                addCommentToListView(comment);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        initListView();
        ButterKnife.bind(this);

        etNewComment.requestFocus();
        String postJson = getIntent().getStringExtra("post_json");
        int postId = 0;
        if (postJson != null) {
            post = CommonMethods.createDefaultGson().fromJson(postJson, Post.class);
            postId = post.getId();
            updatePostInfo();
        }

        Notification notification = getIntent().getParcelableExtra("notification");
        if (notification != null && notification.getType().equals(PushNotificationService.TYPE_COMMENT)) {
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            postId = comment.getPostId();
            setReplyTarget(comment);
            // no need to focus on the notified comment when activity recreated
            if (savedInstanceState == null) {
                notifiedComment = comment;
            }
            if (!BuildConfig.DEBUG) {
                NotificationManager notifyManager = (NotificationManager) getSystemService(Application.NOTIFICATION_SERVICE);
                notifyManager.cancelAll();
            }
        }
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Comment commentClicked = (Comment) view.getTag();
                if (commentClicked.getUser().getId() != User.current.getId()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    setReplyTarget(commentClicked);
                } else {
                    resetReply();
                }
            }
        });

        commentsAdapter = new CommentsAdapter(this);
        lvListView.setAdapter(commentsAdapter);

        if (postId > 0) {
            loadPost(postId);
        }

        PushNotificationService.getInstance().registerCallback(notificationCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PushNotificationService.getInstance().unregisterCallback(notificationCallback);
    }

    private void initListView() {
        lvListView = (ListView) findViewById(R.id.lv_list_view);
        lvListView.setDividerHeight(0);
        final View headerView = LayoutInflater.from(this).inflate(R.layout.card_details_header, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetReply();
                hideInput(headerView);
            }
        });
        lvListView.addHeaderView(headerView, null, false);
    }

    private void loadPost(int postId) {
        RestClient.getInstance().getPost(postId).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                post = CommonMethods.createDefaultGson().fromJson(result.toString(), Post.class);
                updatePostInfo();

                commentsAdapter.clear();
                commentsAdapter.addAll(post.getComments());
                commentsAdapter.sortComments();
                commentsAdapter.notifyDataSetChanged();

                focusComment(notifiedComment);
            }
        }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

    private void updatePostInfo() {
        tvContent.setText(post.getPostPages()[0].getText());
        User user = post.getUser();
        tvNickname.setUser(user);
        ivAvatar.setUser(user);
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        tvViews.setText(String.valueOf(post.getViewsCount()));
        String createdAtFormat = getResources().getString(R.string.post_created_at);
        int daysAgo = Days.daysBetween(post.getCreatedAt().toLocalDate(),
                DateTime.now().toLocalDate()).getDays();
        tvTime.setText(String.format(createdAtFormat, daysAgo));

        final String imageUrl = post.getPostPages()[0].getImage();
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailsActivity.this, ShowImageActivity.class);
                intent.putExtra("image_url", imageUrl);
                startActivity(intent);
            }
        });
        displayPostImage(imageUrl);

        int bookmarkResource = post.isBookmarked() ? R.drawable.icon_bookmark_checked : R.drawable.icon_bookmark;
        ivBookmark.setImageResource(bookmarkResource);
    }

    private void displayPostImage(final String imageUrl) {
        if (postImageDisplayed) {
            return;
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            ivImage.setVisibility(View.GONE);
            tvContent.setTextSize(getResources().getDimension(R.dimen.no_image_text_size_card_details));
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(createLoadingDrawable())
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, ivImage, options);
        }
        postImageDisplayed = true;
    }

    @OnClick(R.id.iv_bookmark)
    public void onBookmarkClicked(View view) {
        view.setEnabled(false);
        DoneCallback<JSONObject> onDone = new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject result) {
                post = CommonMethods.createDefaultGson().fromJson(result.toString(), Post.class);
                updatePostInfo();
                int toastResource = post.isBookmarked() ? R.string.bookmark_added : R.string.bookmark_removed;
                Toast.makeText(CardDetailsActivity.this, toastResource, Toast.LENGTH_SHORT).show();
            }
        };
        FailCallback<VolleyError> onFail = new JsonErrorListener(getApplicationContext(), null) {
            @Override
            public void onFail(VolleyError error) {
                super.onFail(error);
                updatePostInfo();
            }
        };
        if (!post.isBookmarked()) {
            ivBookmark.setImageResource(R.drawable.icon_bookmark_checked);
            RestClient.getInstance().createBookmark(post.getId()).done(onDone).fail(onFail)
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view));
        } else {
            ivBookmark.setImageResource(R.drawable.icon_bookmark);
            RestClient.getInstance().removeBookmark(post.getId()).done(onDone).fail(onFail)
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view));
        }
    }

    @OnClick(R.id.btn_send_comment)
    public void onSendCommentClicked(View view) {
        if (etNewComment.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.comment_text_required, Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), R.string.comment_success, Toast.LENGTH_LONG).show();
                        resetReply();
                        Comment newComment = CommonMethods.createDefaultGson().fromJson(result.toString(), Comment.class);
                        addCommentToListView(newComment);

                        post = newComment.getPost();
                        updatePostInfo();
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

    private void resetReply() {
        replyTarget = null;
        etNewComment.setText("");
        etNewComment.setHint(R.string.publish_comment);
    }

    private void setReplyTarget(Comment comment) {
        replyTarget = comment;
        etNewComment.setText("");
        etNewComment.setHint(String.format(getResources().getString(R.string.reply_to_comment_format), replyTarget.getUser().getNickname()));

    }

    private void addCommentToListView(Comment comment) {
        commentsAdapter.add(comment);
        commentsAdapter.sortComments();
        commentsAdapter.notifyDataSetChanged();
        focusComment(comment);
    }

    private void hideInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void focusComment(final Comment comment) {
        if (comment == null) {
            return;
        }
        lvListView.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < commentsAdapter.getCount(); i++) {
                    if (commentsAdapter.getItem(i).getId() == comment.getId()) {
                        lvListView.setSelection(i + 1);
                        commentsAdapter.setSelectItem(i);
                        commentsAdapter.notifyDataSetInvalidated();

                        return;
                    }
                }
            }
        });
    }

    private Drawable createLoadingDrawable() {
        if (post.getPostPages()[0].getImageHeight() == null || post.getPostPages()[0].getImageWidth() == null) {
            return getResources().getDrawable(R.color.home_title_color);
        }

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(R.drawable.shape_loading);
        gradientDrawable.setSize(post.getPostPages()[0].getImageWidth(), post.getPostPages()[0].getImageHeight());
        return gradientDrawable;
    }
}

