package swj.swj.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import swj.swj.R;
import swj.swj.bean.HomeItemBean;

/**
 * Created by shw on 2015/9/15.
 */
public class HomeViewAdapter extends SlidingViewAdapter {
    private List<HomeItemBean> mList;
    private LayoutInflater mInflater;

    public HomeViewAdapter(Context context, List<HomeItemBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int position, View convertView) {
        Log.d("getView", position + "");

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(new ViewHolder(convertView));
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HomeItemBean bean = mList.get(position);
        viewHolder.ivImage.setImageResource(bean.getHomeImage());

        viewHolder.tvUser.setText(bean.getHomeUser());
        viewHolder.tvContext.setText(bean.getHomeContext());
        viewHolder.tvMessage.setText(bean.getHomeMessage());
        viewHolder.tvViews.setText(bean.gethomeViews());
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvUser;
        public TextView tvContext;
        public TextView tvMessage;
        public TextView tvViews;
        public ImageView ivImage;

        public ViewHolder(View convertView) {
            this.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
            this.tvContext = (TextView) convertView.findViewById(R.id.tv_content);
            this.tvMessage = (TextView) convertView.findViewById(R.id.tv_message);
            this.tvViews = (TextView) convertView.findViewById(R.id.tv_views);
            this.ivImage = (ImageView) convertView.findViewById(R.id.iv_image);
        }
    }
}
