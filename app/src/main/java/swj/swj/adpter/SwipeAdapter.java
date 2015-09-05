package swj.swj.adpter;

/**
 * Created by syb on 2015/9/5.
 */

import android.view.View;
import android.view.ViewGroup;

public abstract class SwipeAdapter {

    private int count = 0;

    public SwipeAdapter() {
        super();
    }

    public abstract int getCount();

    public abstract Object getItem(int position);

    public abstract long getItemId(int position);

    public abstract View getView(int position, View convertView, ViewGroup parent);

}