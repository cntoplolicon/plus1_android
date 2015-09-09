package swj.swj.fragment;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.adpter.SlidingViewAdapter;
import swj.swj.view.MySlidingView;


public class HomeFragment extends BaseFragment {

    private MySlidingView mSlidingView;

    int[] image = {R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc, R.drawable.abc};

    @Override
    public View initView() {

        View root = View.inflate(mActivity, R.layout.fragment_home, null);

        mSlidingView = (MySlidingView) root.findViewById(R.id.sl_swipelayout);

        MyAdapter mAdpter = new MyAdapter();

        mSlidingView.setAdapter(mAdpter);

        return root;
    }

    @Override
    public void initData() {
        super.initData();
    }

    class MyAdapter extends SlidingViewAdapter {

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public View getView(int position, View convertView) {

            Log.i("getView", position + "");

            if (convertView == null) {
                convertView = View.inflate(getMyActivity(), R.layout.home_list_item, null);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.iv_image.setImageResource(image[position]);

            return convertView;
        }
    }

    public static class ViewHolder {

        public TextView tv_user;
        public ImageView iv_image;

        public ViewHolder(View convertView) {
            this.tv_user = (TextView) convertView.findViewById(R.id.tv_user);
            this.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
        }

    }
}