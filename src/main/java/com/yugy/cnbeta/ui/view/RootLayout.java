package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yugy.cnbeta.R;

public class RootLayout extends FrameLayout {

    private View mHeaderContainer;

    public RootLayout (Context context) {
        super(context);
    }

    public RootLayout (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RootLayout (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        //at first find headerViewContainer and listViewBackground
        if (mHeaderContainer == null)
            mHeaderContainer = findViewById(R.id.fab__header_container);

        //if there's no headerViewContainer then fallback to standard FrameLayout
        if (mHeaderContainer == null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }

        //get last header and listViewBackground position
        int headerTopPrevious = mHeaderContainer.getTop();

        //relayout
        super.onLayout(changed, left, top, right, bottom);

        //revert header top position
        int headerTopCurrent = mHeaderContainer.getTop();
        if (headerTopCurrent != headerTopPrevious) {
            mHeaderContainer.offsetTopAndBottom(headerTopPrevious - headerTopCurrent);
        }

    }

}