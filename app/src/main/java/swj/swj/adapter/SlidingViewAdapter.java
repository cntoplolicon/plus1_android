package swj.swj.adapter;

/**
 * Created by syb on 2015/9/5.
 */

import android.view.View;

public abstract class SlidingViewAdapter {

    public abstract int getCount();

    //public abstract Object getItem(int position);

    //public abstract long getItemId(int position);

    public abstract View getView(int position, View convertView);

    public int getTypeCount() {
        return 1;
    }

    public int getPositionType(int position) {
        return 1;
    }

}