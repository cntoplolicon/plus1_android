package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by shw on 2015/9/14.
 */
public class MessageAdapter extends BaseAdapter {

    private int[] image = new int[]{R.drawable.open};
    private String[] userName = new String[]{"王王", "二楞", "嘿嘿"};
    private LayoutInflater mInflater;

    public MessageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userName.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.message_list_item, null);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.tv_user);
            viewHolder.open = (ImageView) convertView.findViewById(R.id.iv_open);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userName.setText(userName[position]);
        viewHolder.open.setImageResource(image[0]);
        return convertView;
    }

    private static class ViewHolder {
        private TextView userName;
        private ImageView open;
    }
}
