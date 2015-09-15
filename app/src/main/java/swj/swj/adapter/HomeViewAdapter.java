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
        Log.i("getView", position + "");

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(new ViewHolder(convertView));
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        HomeItemBean bean = mList.get(position);
        viewHolder.iv_image.setImageResource(bean.getHomeImage());
        viewHolder.tv_user.setText(bean.getHomeUser());
        viewHolder.tv_context.setText(bean.getHomeContext());
        viewHolder.tv_message.setText(bean.getHomeMessage());
        viewHolder.tv_see.setText(bean.getHomeseeNumber());

        return convertView;
    }

    public static class ViewHolder {

        public TextView tv_user;
        public TextView tv_context;
        public TextView tv_message;
        public TextView tv_see;
        public ImageView iv_image;


        public ViewHolder(View convertView) {
            this.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
            this.tv_context = (TextView) convertView.findViewById(R.id.tv_content);
            this.tv_message = (TextView) convertView.findViewById(R.id.tv_message);
            this.tv_see = (TextView) convertView.findViewById(R.id.tv_see);
            this.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
        }

    }
}
