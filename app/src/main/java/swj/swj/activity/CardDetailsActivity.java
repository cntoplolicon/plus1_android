package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import swj.swj.R;
import swj.swj.adapter.CardDetailsAdapter;
import swj.swj.bean.CardDetailsItemBean;

public class CardDetailsActivity extends Activity {
    private List<CardDetailsItemBean> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);
        initData();
    }

    public void initData() {
        mList = new ArrayList<CardDetailsItemBean>();
        for (int i = 0; i < 99; i++) {
            mList.add(new CardDetailsItemBean(R.drawable.abc, "用户" + i, "内容" + i));
        }
        ListView lvListView = (ListView) findViewById(R.id.lv_listview);
        lvListView.setDividerHeight(0);
        View heardeViewLayout = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_card_details_head, null);
        lvListView.addHeaderView(heardeViewLayout, null, false);
        CardDetailsAdapter cardDetailsAdapter = new CardDetailsAdapter(this, mList);
        lvListView.setAdapter(cardDetailsAdapter);
    }
}
