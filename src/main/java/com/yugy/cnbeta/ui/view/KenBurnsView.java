package com.yugy.cnbeta.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yugy.cnbeta.utils.DebugUtils;

import java.util.Random;

/**
 * Created by f.laurent on 21/11/13.
 */
public class KenBurnsView extends FrameLayout {

    private final Handler mHandler;
    private String[] mImageUrls;
    private ImageView[] mImageViews;
    private int mActiveImageIndex = 0;

    private final Random random = new Random();
    private int mSwapMs = 10000;
    private int mFadeInOutMs = 400;

    private float maxScaleFactor = 1.5F;
    private float minScaleFactor = 1.2F;

    private Runnable mSwapImageRunnable = new Runnable() {
        @Override
        public void run() {
            if(mImageViews != null){
                swapImage();
            }
            mHandler.postDelayed(mSwapImageRunnable, mSwapMs - mFadeInOutMs*2);
        }
    };

    public KenBurnsView(Context context) {
        this(context, null);
    }

    public KenBurnsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KenBurnsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHandler = new Handler();
    }

    public void setResourceIds(String[] imageUrls) {
        mImageUrls = imageUrls;
        fillImageViews();
    }

    private void swapImage() {
        DebugUtils.log("KenBurnsView: swapImage active=" + mActiveImageIndex);

        int inactiveIndex = mActiveImageIndex;
        mActiveImageIndex = (1 + mActiveImageIndex) % mImageViews.length;
        DebugUtils.log("KenBurnsView: new active=" + mActiveImageIndex);

        final ImageView activeImageView = mImageViews[mActiveImageIndex];
        activeImageView.setAlpha(0.0f);
        ImageView inactiveImageView = mImageViews[inactiveIndex];

        animate(activeImageView);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(mFadeInOutMs);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(inactiveImageView, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(activeImageView, "alpha", 0.0f, 1.0f)
        );
        animatorSet.start();
    }

    private void start(View view, long duration, float fromScale, float toScale, float fromTranslationX, float fromTranslationY, float toTranslationX, float toTranslationY) {
        view.setScaleX(fromScale);
        view.setScaleY(fromScale);
        view.setTranslationX(fromTranslationX);
        view.setTranslationY(fromTranslationY);
        ViewPropertyAnimator propertyAnimator = view.animate().translationX(toTranslationX).translationY(toTranslationY).scaleX(toScale).scaleY(toScale).setDuration(duration);
        propertyAnimator.start();
        DebugUtils.log("KenBurnsView: starting Ken Burns animation " + propertyAnimator);
    }

    private float pickScale() {
        return this.minScaleFactor + this.random.nextFloat() * (this.maxScaleFactor - this.minScaleFactor);
    }

    private float pickTranslation(int value, float ratio) {
        return value * (ratio - 1.0f) * (this.random.nextFloat() - 0.5f);
    }

    public void animate(View view) {
        float fromScale = pickScale();
        float toScale = pickScale();
        float fromTranslationX = pickTranslation(view.getWidth(), fromScale);
        float fromTranslationY = pickTranslation(view.getHeight(), fromScale);
        float toTranslationX = pickTranslation(view.getWidth(), toScale);
        float toTranslationY = pickTranslation(view.getHeight(), toScale);
        start(view, this.mSwapMs, fromScale, toScale, fromTranslationX, fromTranslationY, toTranslationX, toTranslationY);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacks(mSwapImageRunnable);
    }

    private void startKenBurnsAnimation() {
        mHandler.post(mSwapImageRunnable);
    }

    private void fillImageViews() {
        mImageViews = new ImageView[mImageUrls.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(imageView);
            mImageViews[i] = imageView;
            ImageLoader.getInstance().displayImage(mImageUrls[i], mImageViews[i]);
        }
        startKenBurnsAnimation();
    }
}
