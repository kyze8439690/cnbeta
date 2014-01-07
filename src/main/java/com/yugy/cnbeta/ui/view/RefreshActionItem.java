package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yugy.cnbeta.R;

public class RefreshActionItem extends FrameLayout implements View.OnClickListener{
    private ImageView mRefreshButton;
    private ProgressBar mProgressIndicatorIndeterminate;
    private RefreshActionListener mRefreshButtonListener;
    private MenuItem mMenuItem;
    private boolean mRefreshing = false;

    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        updateChildrenVisibility();
    }

    public interface RefreshActionListener {
        void onRefreshButtonClick(RefreshActionItem sender);
    }

    public RefreshActionItem(Context context){
        super(context);
        init();
    }

    public RefreshActionItem(Context context, AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public RefreshActionItem(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.view_refresh_action_item, this);
        mRefreshButton = (ImageView) findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(this);
        mProgressIndicatorIndeterminate = (ProgressBar) findViewById(R.id.indeterminate_progress_indicator);
        updateChildrenVisibility();
    }

    public void setRefreshActionListener(RefreshActionListener listener) {
        this.mRefreshButtonListener = listener;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.mMenuItem = menuItem;
        if (menuItem.getIcon() != null) {
            mRefreshButton.setImageDrawable(mMenuItem.getIcon());
        }
    }

    private void updateChildrenVisibility() {
        if (!mRefreshing) {
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressIndicatorIndeterminate.setVisibility(View.GONE);
        }else{
            mRefreshButton.setVisibility(View.GONE);
            mProgressIndicatorIndeterminate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (mRefreshButtonListener != null) {
            mRefreshButtonListener.onRefreshButtonClick(this);
        }
    }
}
