/**
 * Created by cntoplolicon on 10/7/15.
 */
package swj.swj.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.model.Post;

/**
 * Created by jiewei on 9/14/15.
 */
public class PostsGridViewAdapter extends BaseAdapter {

    private static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder().cloneFrom(SnsApplication.DEFAULT_DISPLAY_OPTION)
                    .showImageOnLoading(R.color.home_title_color)
                    .showImageOnFail(R.drawable.image_load_fail)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();

    protected Context context;
    protected Post[] posts = new Post[]{};

    public PostsGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return posts.length;
    }

    @Override
    public Object getItem(int position) {
        return posts[position];
    }

    @Override
    public long getItemId(int position) {
        return posts[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView = convertView;
        if (gridView == null) {
            gridView = LayoutInflater.from(context).inflate(R.layout.user_post_grid_view_item, null);
        }

        Post post = posts[position];
        TextView tvText = (TextView) gridView.findViewById(R.id.tv_text);
        tvText.setText(post.getPostPages()[0].getText());
        TextView tvComments = (TextView) gridView.findViewById(R.id.tv_comments);
        tvComments.setText(String.valueOf(post.getCommentsCount()));
        TextView tvViews = (TextView) gridView.findViewById(R.id.tv_views);
        tvViews.setText(String.valueOf(post.getViewsCount()));

        ImageView imageView = (ImageView) gridView.findViewById(R.id.iv_image);
        ImageLoader.getInstance().cancelDisplayTask(imageView);
        String imageUrl = post.getPostPages()[0].getImage();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageDrawable(null);
            tvText.setVisibility(View.VISIBLE);
        } else {
            tvText.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(imageUrl, imageView, DISPLAY_IMAGE_OPTIONS);
        }

        return gridView;
    }
}
