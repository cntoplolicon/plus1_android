package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import swj.swj.R;
import swj.swj.bean.CardDetailsItemBean;

/**
 * Created by shw on 2015/9/14.
 */
public class CardDetailsAdapter extends BaseAdapter {
    private List<CardDetailsItemBean> mList;
    private LayoutInflater mInflater;

    public CardDetailsAdapter(Context context, List<CardDetailsItemBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoldeder viewHoldeder;
        if (convertView == null) {
            viewHoldeder = new ViewHoldeder();
            convertView = mInflater.inflate(R.layout.card_details_comment_item, null);
            viewHoldeder.imageView = (ImageView) convertView.findViewById(R.id.imc_image);
            viewHoldeder.userName = (TextView) convertView.findViewById(R.id.tv_user);
            viewHoldeder.context = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHoldeder);
        } else {
            viewHoldeder = (ViewHoldeder) convertView.getTag();
        }
        CardDetailsItemBean bean = mList.get(position);
        viewHoldeder.imageView.setImageResource(bean.getImageView());
        viewHoldeder.userName.setText(bean.getUserName());
        viewHoldeder.context.setText(bean.getContent());
        return convertView;
    }

    static  class ViewHoldeder {
        public ImageView imageView;
        public TextView userName;
        public TextView context;
    }
}
