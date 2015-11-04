package com.oneplusapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneplusapp.R;

/**
 * Created by cntoplolicon on 9/25/15.
 */
public class ActionBarLayout extends RelativeLayout {

    private boolean hideBackArrow;
    private String pageTitle;
    private TextView tvPageTitle;
    private ImageView ivBack;

    public ActionBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ActionBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.action_bar_layout, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ActionBarLayout, 0, 0);
        try {
            pageTitle = a.getString(R.styleable.ActionBarLayout_page_title);
            hideBackArrow = a.getBoolean(R.styleable.ActionBarLayout_hide_back_arrow, false);
        } finally {
            a.recycle();
        }

        pageTitle = pageTitle == null ? "" : pageTitle;
        tvPageTitle = (TextView) findViewById(R.id.tv_page_title);
        tvPageTitle.setText(pageTitle);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        if (!hideBackArrow) {
            ivBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) getContext()).finish();
                }
            });
        }
        ivBack.setVisibility(hideBackArrow ? View.GONE : View.VISIBLE);
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle == null ? "" : pageTitle;
        tvPageTitle.setText(pageTitle);
    }

    public boolean isHideBackArrow() {
        return hideBackArrow;
    }

    public void setHideBackArrow(boolean hideBackArrow) {
        this.hideBackArrow = hideBackArrow;
        ivBack.setVisibility(hideBackArrow ? View.GONE : View.VISIBLE);
    }
}
