package com.oneplusapp.activity;

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
import com.oneplusapp.BuildConfig;
import com.oneplusapp.R;
import com.oneplusapp.adapter.CommentsAdapter;
import com.oneplusapp.application.SnsApplication;
import com.oneplusapp.common.BookmarkService;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.PushNotificationService;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Comment;
import com.oneplusapp.model.Notification;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private ListView lvListView;

    private int postId = 0;
    private Post post;
    private CommentsAdapter commentsAdapter;
    private Comment replyTarget;
    private Comment notifiedComment;

    private PushNotificationService.Callback notificationCallback = new PushNotificationService.Callback() {
        @Override
        public void onNotificationReceived(Notification notification) {
            if (!notification.getType().equals(Notification.TYPE_COMMENT)) {
                return;
            }
            Comment comment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            if (comment.getPostId() == postId) {
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
        if (postJson != null) {
            post = CommonMethods.createDefaultGson().fromJson(postJson, Post.class);
            postId = post.getId();
            updatePostInfo();
        }

        Notification notification = getIntent().getParcelableExtra("notification");
        if (notification != null && notification.getType().equals(PushNotificationService.TYPE_COMMENT)) {
            notifiedComment = CommonMethods.createDefaultGson().fromJson(notification.getContent(), Comment.class);
            postId = notifiedComment.getPostId();
            if (!BuildConfig.DEBUG) {
                NotificationManager notifyManager = (NotificationManager) getSystemService(Application.NOTIFICATION_SERVICE);
                notifyManager.cancelAll();
            }
        }
        if (postId > 0) {
            loadPost(postId);
        }
        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                replyTarget = (Comment) view.getTag();
                if (replyTarget.getUser().getId() != User.current.getId()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    etNewComment.setHint(String.format(getResources().getString(R.string.reply_to_comment_format), replyTarget.getUser().getNickname()));
                } else {
                    replyTarget = null;
                    etNewComment.setHint(R.string.publish_comment);
                }
            }
        });

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
                replyTarget = null;
                hideInput(headerView);
                etNewComment.setHint(R.string.publish_comment);
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
        if (post.getUser().getAvatar() != null) {
            ImageLoader.getInstance().displayImage(post.getUser().getAvatar(), ivAvatar);
        }
        tvNickname.setCompoundDrawablesWithIntrinsicBounds(0, 0, genderIcon, 0);
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        tvViews.setText(String.valueOf(post.getViewsCount()));
        String createdAtFormat = getResources().getString(R.string.post_created_at);
        int daysAgo = Days.daysBetween(post.getCreatedAt().toLocalDate(),
                DateTime.now().toLocalDate()).getDays();
        tvTime.setText(String.format(createdAtFormat, daysAgo));
        final String imageUrl = post.getPostPages()[0].getImage();

        ImageLoader.getInstance().cancelDisplayTask(ivImage);
        if (imageUrl == null || imageUrl.isEmpty()) {
            ivImage.setVisibility(View.GONE);
            tvContent.setTextSize(getResources().getDimension(R.dimen.no_image_text_size_card_details));
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .build();
            ImageLoader.getInstance().displayImage(imageUrl, ivImage, options);
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CardDetailsActivity.this, ShowImageActivity.class);
                    intent.putExtra("image_url", imageUrl);
                    startActivity(intent);
                }
            });
        }
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(post.getUser()));
                startActivity(intent);
            }
        });
        syncBookmarkInfo();
        loadComments();
    }

    private void loadComments() {
        commentsAdapter = new CommentsAdapter(this, post);
        commentsAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                tvComments.setText(String.valueOf(commentsAdapter.getCount()));
            }
        });
        commentsAdapter.setOnViewClickedListener(new OnViewClickedListener());
        lvListView.setAdapter(commentsAdapter);
        focusComment(notifiedComment);
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
                                Toast.makeText(CardDetailsActivity.this, R.string.bookmark_added, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CardDetailsActivity.this, R.string.bookmark_removed, Toast.LENGTH_SHORT).show();
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

    private void addCommentToListView(Comment comment) {
        commentsAdapter.add(comment);
        commentsAdapter.sortComments();
        commentsAdapter.notifyDataSetChanged();
        focusComment(comment);
    }

    private void syncBookmarkInfo() {
        if (BookmarkService.getInstance().isBookmarked(post)) {
            ivBookmark.setImageResource(R.drawable.icon_bookmark_checked);
        } else {
            ivBookmark.setImageResource(R.drawable.icon_bookmark);
        }
    }

    private class OnViewClickedListener implements CommentsAdapter.ViewClickedListener {

        @Override
        public void onViewClick(View view, int position) {
            if (view.getId() == R.id.tv_nickname || view.getId() == R.id.iv_avatar) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(commentsAdapter.getItem(position).getUser()));
                hideInput(view);
                startActivity(intent);
            } else if (view.getId() == R.id.tv_reply_target) {
                Intent intent = new Intent(CardDetailsActivity.this, UserHomeActivity.class);
                int replyTargetId = commentsAdapter.getItem(position).getReplyToId();
                intent.putExtra("user_json", CommonMethods.createDefaultGson().toJson(commentsAdapter.getCommentById(replyTargetId).getUser()));
                hideInput(view);
                startActivity(intent);
            }
        }
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
}
