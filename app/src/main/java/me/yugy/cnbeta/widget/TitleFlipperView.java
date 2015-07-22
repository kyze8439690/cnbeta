package me.yugy.cnbeta.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import me.yugy.cnbeta.R;

public class TitleFlipperView extends ViewFlipper {

    private AnimationSet mSlideInLeftAnimation;
    private AnimationSet mSlideInRightAnimation;
    private AnimationSet mSlideOutLeftAnimation;
    private AnimationSet mSlideOutRightAnimation;

    public TitleFlipperView(Context context) {
        this(context, null);
    }

    public TitleFlipperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnimations();
    }

    public void setViewPager(final ViewPager pager) {
        if (pager == null) {
            throw new IllegalStateException("ViewPager is null.");
        }
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager haven't have a pager.");
        }
        int count = pager.getAdapter().getCount();
        for (int i = 0; i < count; i++) {
            TextView text = new TextView(getContext());
            text.setTextAppearance(getContext(), R.style.Base_TextAppearance_AppCompat_Widget_ActionBar_Title_Inverse);
            text.setText(pager.getAdapter().getPageTitle(i));
            addView(text);
        }
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            private int mCurrentPagerIndex = pager.getCurrentItem();

            @Override
            public void onPageSelected(int position) {
                if (position > mCurrentPagerIndex) {
                    setInAnimation(mSlideInRightAnimation);
                    setOutAnimation(mSlideOutLeftAnimation);
                } else {
                    setInAnimation(mSlideInLeftAnimation);
                    setOutAnimation(mSlideOutRightAnimation);
                }
                setDisplayedChild(position);
                mCurrentPagerIndex = position;
            }
        });
        setDisplayedChild(pager.getCurrentItem());
    }

    private void initAnimations() {
        mSlideInLeftAnimation = new AnimationSet(true);
        mSlideInRightAnimation = new AnimationSet(true);
        mSlideOutLeftAnimation = new AnimationSet(true);
        mSlideOutRightAnimation = new AnimationSet(true);

        mSlideInLeftAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_left));
        mSlideInLeftAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_in));
        mSlideInRightAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
        mSlideInRightAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_in));
        mSlideOutLeftAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
        mSlideOutLeftAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_out));
        mSlideOutRightAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right));
        mSlideOutRightAnimation.addAnimation(
                AnimationUtils.loadAnimation(getContext(), R.anim.abc_fade_out));
    }

}
