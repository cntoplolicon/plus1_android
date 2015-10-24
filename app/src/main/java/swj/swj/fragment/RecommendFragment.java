package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.RecommendGridViewAdapter;
import swj.swj.common.CommonMethods;


public class RecommendFragment extends Fragment {

    @Bind(R.id.grid_view_concentration)
    GridView gridView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        ButterKnife.bind(this, view);

        RecommendGridViewAdapter adapter = new RecommendGridViewAdapter(getActivity());
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), CardDetailsActivity.class);
                intent.putExtra("post_json", CommonMethods.createDefaultGson().toJson(parent.getAdapter().getItem(position)));
                startActivity(intent);
            }
        });
        gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));

        return view;
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RecommendScreen");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RecommendScreen");
    }
}
