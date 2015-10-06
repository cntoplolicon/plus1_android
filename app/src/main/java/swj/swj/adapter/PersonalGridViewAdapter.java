package swj.swj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by jiewei on 9/14/15.
 */
public class PersonalGridViewAdapter extends BaseAdapter {

    private Context context;

    public PersonalGridViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 20;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.myself_gridview_item, null);
            TextView textView = (TextView) gridView.findViewById(R.id.tv_forward_count);
            textView.setText("32人转发");
        } else {
            gridView = convertView;
        }

        return gridView;
    }
}
