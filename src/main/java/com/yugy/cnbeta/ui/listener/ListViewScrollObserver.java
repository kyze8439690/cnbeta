package com.yugy.cnbeta.ui.listener;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by yugy on 14-1-7.
 */
public class ListViewScrollObserver implements AbsListView.OnScrollListener{

    private OnListViewScrollListener mListener;
    private int mLastFirstVisibleItem;
    private int mLastTop;
    private int mScrollPosition;
    private int mLastHeight;

    public interface OnListViewScrollListener {
        public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact);
        public void onScrollIdle();
    }

    public void setOnScrollUpAndDownListener(OnListViewScrollListener listener){
        mListener = listener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(mListener != null && scrollState == SCROLL_STATE_IDLE){
            mListener.onScrollIdle();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        View firstChild = view.getChildAt(0);
        if(firstChild == null){
            return;
        }
        int top = firstChild.getTop();
        int height = firstChild.getHeight();
        int delta;
        int skipped = 0;
        if(mLastFirstVisibleItem == firstVisibleItem){
            delta = mLastTop - top;
        }else if(firstVisibleItem > mLastFirstVisibleItem){
            skipped = firstVisibleItem - mLastFirstVisibleItem - 1;
            delta = skipped * height + mLastHeight + mLastTop - top;
        }else{
            skipped= mLastFirstVisibleItem - firstVisibleItem - 1;
            delta = skipped * -height + mLastTop - (height + top);
        }
        boolean exact = skipped > 0;
        mScrollPosition += -delta;
        if(mListener != null){
            mListener.onScrollUpDownChanged(-delta, mScrollPosition, exact);
        }
        mLastFirstVisibleItem = firstVisibleItem;
        mLastTop = top;
        mLastHeight = firstChild.getHeight();
    }
}
