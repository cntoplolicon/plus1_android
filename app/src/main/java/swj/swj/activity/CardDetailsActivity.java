package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.adapter.CardDetailsAdapter;
import swj.swj.application.SnsApplication;
import swj.swj.common.BookmarkService;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;
import swj.swj.common.RestClient;
import swj.swj.model.Comment;
import swj.swj.model.Post;

public class CardDetailsActivity extends Activity {

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

    private Post post;
    private CardDetailsAdapter cardDetailsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        initData();

        ButterKnife.bind(this);

        updatePostInfo();
        syncBookmarkInfo();
    }

    private void updatePostInfo() {
        String postJson = getIntent().getStringExtra("post_json");
        post = CommonMethods.createDefaultGson().fromJson(postJson, Post.class);

        tvContent.setText(post.getPostPages()[0].getText());
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        tvViews.setText(String.valueOf(post.getViewsCount()));

        String createdAtFormat = getResources().getString(R.string.post_created_at);
        int daysAgo = Days.daysBetween(post.getCreatedAt().toLocalDate(),
                DateTime.now().toLocalDate()).getDays();
        tvTime.setText(String.format(createdAtFormat, daysAgo));

        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null) {
            ivImage.setVisibility(View.GONE);
        } else {
            ivImage.setImageResource(R.drawable.loading);
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + imageUrl, ivImage);
        }
    }

    private void initData() {
        String postJson = getIntent().getStringExtra("post_json");
        post = CommonMethods.createDefaultGson().fromJson(postJson, Post.class);

        ListView lvListView = (ListView) findViewById(R.id.lv_listview);
        lvListView.setDividerHeight(0);
        View headerView = LayoutInflater.from(this).inflate(R.layout.card_details_header, null);
        lvListView.addHeaderView(headerView, null, false);
        cardDetailsAdapter = new CardDetailsAdapter(this, post);
        lvListView.setAdapter(cardDetailsAdapter);
    }

    @OnClick(R.id.iv_bookmark)
    public void onBookmarkClicked(View view) {
        view.setEnabled(false);
        if (!BookmarkService.getInstance().isBookmarked(post)) {
            ivBookmark.setImageResource(R.drawable.settings);
            RestClient.getInstance().createBookmark(post.getId())
                    .fail(new JsonErrorListener(getApplicationContext(), null))
                    .always(new ResetViewClickable<JSONObject, VolleyError>(view) {
                        @Override
                        public void onAlways(Promise.State state, JSONObject resolved, VolleyError rejected) {
                            super.onAlways(state, resolved, rejected);
                            if (state == Promise.State.RESOLVED) {
                                Toast.makeText(CardDetailsActivity.this, getResources().getString(R.string.bookmard_added), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CardDetailsActivity.this, getResources().getString(R.string.bookmard_removed), Toast.LENGTH_SHORT).show();
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
        view.setEnabled(false);
        RestClient.getInstance().newComment(etNewComment.getText().toString(), -1, post.getId())
                .done(new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject result) {
                        etNewComment.setText("");
                        Toast.makeText(getApplicationContext(), R.string.comment_success, Toast.LENGTH_LONG).show();
                        Comment newComment = CommonMethods.createDefaultGson().fromJson(result.toString(), Comment.class);
                        cardDetailsAdapter.add(newComment);
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
            ivBookmark.setImageResource(R.drawable.settings);
        } else {
            ivBookmark.setImageResource(R.drawable.icon_bookmark);
        }
    }
}
