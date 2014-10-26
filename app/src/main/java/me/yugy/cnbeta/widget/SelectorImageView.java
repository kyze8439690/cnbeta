package me.yugy.cnbeta.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

import me.yugy.cnbeta.R;

/**
 * Created by yugy on 14/10/25.
 */
public class SelectorImageView extends ImageView{
    public SelectorImageView(Context context) {
        this(context, null);
    }

    public SelectorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private Drawable mSelectorDrawable;

    public SelectorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        mSelectorDrawable = getResources().getDrawable(R.drawable.selector);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSelectorDrawable.setBounds(0, 0, w, h);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        mSelectorDrawable.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        mSelectorDrawable.draw(canvas);
    }
}
