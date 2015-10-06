package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import swj.swj.R;
import swj.swj.adapter.PersonalGridViewAdapter;

public class UserHomeActivity extends Activity {
    private GridViewWithHeaderAndFooter othersHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        init();
    }

    public void init() {
        othersHome = (GridViewWithHeaderAndFooter) findViewById(R.id.others_home);
        othersHome.addHeaderView(LayoutInflater.from(this).inflate(R.layout.fragment_myself_header, null));
        othersHome.setAdapter(new PersonalGridViewAdapter(this));
    }

    class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return "大家好！";
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(UserHomeActivity.this,
                    R.layout.myself_gridview_item, null);
            ImageView ivItem = (ImageView) view.findViewById(R.id.gridview_item_image);
            TextView tvItem = (TextView) view.findViewById(R.id.tv_forward_count);
            tvItem.setText("传播" + position);
            ivItem.setImageResource(R.drawable.abc);
            return view;
        }
    }
}
