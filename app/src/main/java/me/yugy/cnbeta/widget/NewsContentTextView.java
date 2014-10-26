package me.yugy.cnbeta.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yugy on 2014/8/31.
 */
public class NewsContentTextView extends TextView{

    private Handler mHandler;

    public NewsContentTextView(Context context) {
        this(context, null);
    }

    public NewsContentTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsContentTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mHandler = new Handler();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        handleAnimationDrawable(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        handleAnimationDrawable(false);
    }

    public void handleAnimationDrawable(boolean isPlay) {
        CharSequence text = getText();
        if (text instanceof Spanned) {
            Spanned span = (Spanned) text;
            ImageSpan[] spans = span.getSpans(0, span.length() - 1,
                    ImageSpan.class);
            for (ImageSpan s : spans) {
                Drawable d = s.getDrawable();
                if (d instanceof CircularProgressDrawable) {
                    CircularProgressDrawable circularProgressDrawable = (CircularProgressDrawable) d;
                    if (isPlay) {
                        circularProgressDrawable.setCallback(this);
                        circularProgressDrawable.start();
                    } else {
                        circularProgressDrawable.stop();
                        circularProgressDrawable.setCallback(null);
                    }
                }
            }
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who != null && what != null) {
            mHandler.postAtTime(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who != null && what != null) {
            mHandler.removeCallbacks(what);
        }
    }
}
