package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yugy on 14-1-8.
 */
public class PicViewPager extends ViewPager {
    public PicViewPager(Context context) {
        super(context);
        init();
    }

    public PicViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        setPageTransformer(true, new StackPageTransformer());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

}
