package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.DensityUtils;
import swj.swj.common.PrefUtils;

public class GuideActivity extends Activity {
    private int[] mImageIds = new int[]{R.drawable.guide_1,
            R.drawable.guide_2, R.drawable.guide_3};
    private ArrayList<ImageView> mImageList;
    private int mPointDis;

    @Bind(R.id.vp_guide)
    ViewPager mViewPager;
    @Bind(R.id.ll_container)
    LinearLayout llContainer;
    @Bind(R.id.iv_red_point)
    ImageView ivRedPoint;
    @Bind(R.id.btn_start)
    Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        ButterKnife.bind(this);

        mImageList = new ArrayList<ImageView>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(getApplicationContext());
            view.setBackgroundResource(mImageIds[i]);
            mImageList.add(view);
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_circle_default);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                params.leftMargin = DensityUtils.dp2px(this, 10);
            }
            point.setLayoutParams(params);
            llContainer.addView(point);
        }
        mViewPager.setAdapter(new GuideAdapter());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == mImageIds.length - 1) {
                    btnStart.setVisibility(View.VISIBLE);
                } else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                int leftMargin = (int) (mPointDis * positionOffset + mPointDis
                        * position);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint
                        .getLayoutParams();
                params.leftMargin = leftMargin;
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPointDis = llContainer.getChildAt(1).getLeft()
                                - llContainer.getChildAt(0).getLeft();

                        ivRedPoint.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        HomeActivity.class));
                PrefUtils.putBoolean("is_guide_showed", true,
                        getApplicationContext());
                finish();
            }
        });
    }

    class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
