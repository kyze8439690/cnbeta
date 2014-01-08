package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yugy.cnbeta.R;

/**
 * Created by yugy on 14-1-8.
 */
public class SelectorImageView extends ImageView{
    public SelectorImageView(Context context) {
        super(context);
        init();
    }

    public SelectorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectorImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private Drawable mForegroundSelector;

    private void init(){
        mForegroundSelector = getResources().getDrawable(R.drawable.list_selector_holo);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mForegroundSelector.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mForegroundSelector.setBounds(0, 0, w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mForegroundSelector.draw(canvas);
    }
}

