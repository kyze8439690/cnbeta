package me.yugy.cnbeta.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by yugy on 2014/9/4.
 */
public class FloatingActionButton extends ImageButton {
    public FloatingActionButton(Context context) {
        super(context);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private static final int DURATION = 200;

    private boolean mHide;

    public void hide() {
        hide(true);
    }

    public void hide(boolean animate) {
        leHide(true, animate);
    }

    public void show() {
        show(true);
    }

    public void show(boolean animate) {
        leHide(false, animate);
    }

    private void leHide(final boolean hide, final  boolean animated) {
        leHide(hide, animated, false);
    }

    private void leHide(final boolean hide, final  boolean animated, final boolean deferred) {
        if (mHide != hide || deferred) {
            mHide = hide;
            final int height = getHeight();
            if (height == 0 && !deferred) {
                // Dang it, haven't been drawn before, defer! defer!
                final ViewTreeObserver vto = getViewTreeObserver();
                if (vto.isAlive()) {
                    vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            // Sometimes is not the same we used to know
                            final ViewTreeObserver currentVto = getViewTreeObserver();
                            if (currentVto.isAlive()) {
                                currentVto.removeOnPreDrawListener(this);
                            }
                            leHide(hide, animated, true);
                            return true;
                        }
                    });
                    return;
                }
            }
            int marginBottom = 0;
            final ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
            }
            final int translationY = mHide ? height + marginBottom : 0;
            if (animated) {
                ViewPropertyAnimator.animate(this)
                        .setInterpolator(mInterpolator)
                        .setDuration(DURATION)
                        .translationY(translationY);
            } else {
                ViewHelper.setTranslationY(this, translationY);
            }
        }
    }
}
