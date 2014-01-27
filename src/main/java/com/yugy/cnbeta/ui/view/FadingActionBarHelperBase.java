/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yugy.cnbeta.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.yugy.cnbeta.R;

public abstract class FadingActionBarHelperBase {
    private Drawable mActionBarBackgroundDrawable;
    private FrameLayout mHeaderContainer;
    private int mActionBarBackgroundResId;
    private int mHeaderLayoutResId;
    private View mHeaderView;
    private int mHeaderOverlayLayoutResId;
    private View mHeaderOverlayView;
    private int mContentLayoutResId;
    private View mContentView;
    private LayoutInflater mInflater;
    private boolean mLightActionBar;
    private boolean mTransparentActionBar = true;
    private boolean mUseParallax = true;
    private int mLastDampedScroll;
    private int mLastHeaderHeight = -1;
    private ViewGroup mContentContainer;
    private boolean mFirstGlobalLayoutPerformed;
    private FrameLayout mMarginView;

    public final <T extends FadingActionBarHelperBase> T actionBarBackground(int drawableResId) {
        mActionBarBackgroundResId = drawableResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T actionBarBackground(Drawable drawable) {
        mActionBarBackgroundDrawable = drawable;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerLayout(int layoutResId) {
        mHeaderLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerView(View view) {
        mHeaderView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerOverlayLayout(int layoutResId) {
        mHeaderOverlayLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T headerOverlayView(View view) {
        mHeaderOverlayView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T contentLayout(int layoutResId) {
        mContentLayoutResId = layoutResId;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T  contentView(View view) {
        mContentView = view;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T lightActionBar(boolean value) {
        mLightActionBar = value;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T  parallax(boolean value) {
        mUseParallax = value;
        return (T)this;
    }

    public final <T extends FadingActionBarHelperBase> T  actionBarTransparent(boolean value) {
        mTransparentActionBar = value;
        return (T)this;
    }

    public final View createView(Context context) {
        return createView(LayoutInflater.from(context));
    }

    public final View createView(LayoutInflater inflater) {
        //
        // Prepare everything

        mInflater = inflater;
        if (mContentView == null) {
            mContentView = inflater.inflate(mContentLayoutResId, null);
        }
        if (mHeaderView == null) {
            mHeaderView = inflater.inflate(mHeaderLayoutResId, null, false);
        }

        //
        // See if we are in a ListView or ScrollView scenario

        View root = createScrollView();

        if (mHeaderOverlayView == null && mHeaderOverlayLayoutResId != 0) {
            mHeaderOverlayView = inflater.inflate(mHeaderOverlayLayoutResId, mMarginView, false);
        }
        if (mHeaderOverlayView != null) {
            mMarginView.addView(mHeaderOverlayView);
        }

        // Use measured height here as an estimate of the header height, later on after the layout is complete 
        // we'll use the actual height
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.MATCH_PARENT, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(LayoutParams.WRAP_CONTENT, MeasureSpec.EXACTLY);
        mHeaderView.measure(widthMeasureSpec, heightMeasureSpec);
        updateHeaderHeight(mHeaderView.getMeasuredHeight());

        root.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int headerHeight = mHeaderContainer.getHeight();
                if (!mFirstGlobalLayoutPerformed && headerHeight != 0) {
                    updateHeaderHeight(headerHeight);
                    mFirstGlobalLayoutPerformed = true;
                }
            }
        });
        return root;
    }

    public void initActionBar(Activity activity) {
        if (mActionBarBackgroundDrawable == null) {
            mActionBarBackgroundDrawable = activity.getResources().getDrawable(mActionBarBackgroundResId);
        }
        setActionBarBackgroundDrawable(mActionBarBackgroundDrawable);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        if(mTransparentActionBar){
            mActionBarBackgroundDrawable.setAlpha(0);
        }
    }

    protected abstract int getActionBarHeight();
    protected abstract boolean isActionBarNull();
    protected abstract void setActionBarBackgroundDrawable(Drawable drawable);

    private Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            setActionBarBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    private View createScrollView() {
        ViewGroup scrollViewContainer = (ViewGroup) mInflater.inflate(R.layout.fab__scrollview_container, null);

        NotifyingScrollView scrollView = (NotifyingScrollView) scrollViewContainer.findViewById(R.id.fab__scroll_view);
        scrollView.setOnScrollChangedListener(mOnScrollChangedListener);

        mContentContainer = (ViewGroup) scrollViewContainer.findViewById(R.id.fab__container);
        mContentContainer.addView(mContentView);
        mHeaderContainer = (FrameLayout) scrollViewContainer.findViewById(R.id.fab__header_container);
        initializeGradient(mHeaderContainer);
        mHeaderContainer.addView(mHeaderView, 0);
        mMarginView = (FrameLayout) mContentContainer.findViewById(R.id.fab__content_top_margin);

        return scrollViewContainer;
    }

    private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            onNewScroll(t);
        }
    };

    private void onNewScroll(int scrollPosition) {
        if (isActionBarNull()) {
            return;
        }

        int currentHeaderHeight = mHeaderContainer.getHeight();
        if (currentHeaderHeight != mLastHeaderHeight) {
            updateHeaderHeight(currentHeaderHeight);
        }

        int headerHeight = currentHeaderHeight - getActionBarHeight();
        float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
        int newAlpha = (int) (ratio * 255);
        if(mTransparentActionBar){
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }

        addParallaxEffect(scrollPosition);
    }

    private void addParallaxEffect(int scrollPosition) {
        float damping = mUseParallax ? 0.5f : 1.0f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mHeaderContainer.offsetTopAndBottom(offset);

        if (mFirstGlobalLayoutPerformed) {
            mLastDampedScroll = dampedScroll;
        }
    }

    private void updateHeaderHeight(int headerHeight) {
        ViewGroup.LayoutParams params = mMarginView.getLayoutParams();
        params.height = headerHeight;
        mMarginView.setLayoutParams(params);
        mLastHeaderHeight = headerHeight;
    }

    private void initializeGradient(ViewGroup headerContainer) {
        View gradientView = headerContainer.findViewById(R.id.fab__gradient);
        int gradient = R.drawable.fab__gradient;
        if (mLightActionBar) {
            gradient = R.drawable.fab__gradient_light;
        }
        gradientView.setBackgroundResource(gradient);
    }
}
