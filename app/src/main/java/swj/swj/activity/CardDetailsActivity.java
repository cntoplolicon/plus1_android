package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.adapter.CardDetailsAdapter;
import swj.swj.bean.CardDetailsItemBean;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

public class CardDetailsActivity extends Activity {
    private List<CardDetailsItemBean> mList;

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

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        initData();

        ButterKnife.bind(this);

        updatePostInfo();
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
            ImageLoader.getInstance().displayImage(RestClient.IMAGE_SERVER_URL + imageUrl, ivImage);
        }
    }

    private void initData() {
        mList = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            mList.add(new CardDetailsItemBean(R.drawable.abc, "用户" + i, "内容" + i));
        }
        ListView lvListView = (ListView) findViewById(R.id.lv_listview);
        lvListView.setDividerHeight(0);
        View headerView = LayoutInflater.from(this).inflate(R.layout.card_details_header, null);
        lvListView.addHeaderView(headerView, null, false);
        CardDetailsAdapter cardDetailsAdapter = new CardDetailsAdapter(this, mList);
        lvListView.setAdapter(cardDetailsAdapter);
    }

    @OnClick(R.id.iv_bookmark)
    public void onBookmarkClicked() {
        RestClient.getInstance().createBookmark(post.getId(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ivBookmark.setClickable(true);
                        Toast.makeText(CardDetailsActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    }
                },

                new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ivBookmark.setClickable(true);
                    }
                });
    }
}
