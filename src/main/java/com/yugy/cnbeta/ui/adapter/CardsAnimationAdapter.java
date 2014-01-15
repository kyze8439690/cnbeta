package com.yugy.cnbeta.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.cnbeta.Application;

/**
 * Created by yugy on 14-1-14.
 */
public class CardsAnimationAdapter extends NotAlphaAnimationAdapter {

    private float mTranslationY = 150;

    private float mRotationX = 8;

    private long mDuration = 400;

    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    protected long getAnimationDelayMillis() {
        return 30;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mDuration;
    }

    @Override
    public Animator[] getAnimators(ViewGroup viewGroup, View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationY", mTranslationY, 0),
                ObjectAnimator.ofFloat(view, "RotationX", mRotationX, 0)
        };
    }

}

