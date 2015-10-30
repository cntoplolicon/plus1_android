package swj.swj.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.activity.CardDetailsActivity;
import swj.swj.adapter.RecommendGridViewAdapter;
import swj.swj.common.CommonMethods;
import swj.swj.view.HeaderGridView;


public class RecommendFragment extends Fragment {

    private View spaceHeader;

    @Bind(R.id.grid_view_concentration)
    HeaderGridView gridView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        ButterKnife.bind(this, view);

        spaceHeader = inflater.inflate(R.layout.list_gridview_space_header, null);
        gridView.addHeaderView(spaceHeader, null, false);
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
}
