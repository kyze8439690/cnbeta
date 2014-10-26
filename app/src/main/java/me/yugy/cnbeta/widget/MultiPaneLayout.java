package me.yugy.cnbeta.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewHelper;

import me.yugy.cnbeta.utils.MathUtils;

/**
 * Created by yugy on 14/10/19.
 */
@SuppressLint("RtlHardcoded")
public class MultiPaneLayout extends ViewGroup{

    //value from 0 to 1, 0 means left list, 1 means right detail
    private float mSlideOffset = 0f;
    private boolean mInLayout;
    private boolean mFirstLayout;

    private ViewDragHelper mDragHelper;

    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.layout_gravity
    };

    public MultiPaneLayout(Context context) {
        this(context, null);
    }

    public MultiPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiPaneLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // So that we can catch the back button
        setFocusableInTouchMode(true);

        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);

        mDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
                    widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
            final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
                    heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
            child.measure(contentWidthSpec, contentHeightSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        final int width = r - l;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if(lp.gravity == Gravity.RIGHT) { //right detail view
                child.layout(
                        width + lp.leftMargin - (int)(mSlideOffset * width),
                        lp.topMargin,
                        width + lp.leftMargin + child.getMeasuredWidth() - (int)(mSlideOffset * width),
                        lp.topMargin + child.getMeasuredHeight());
            }else{
                child.layout(
                        lp.leftMargin,
                        lp.topMargin,
                        lp.leftMargin + child.getMeasuredWidth(),
                        lp.topMargin + child.getMeasuredHeight()
                );
            }
        }
        mInLayout = false;
        mFirstLayout = false;
    }

    @Override
    protected boolean drawChild(@NonNull Canvas canvas, @NonNull View child, long drawingTime) {
        return !isDetailView(child) && mSlideOffset == 1f || super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    public void computeScroll() {
        if(mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private class DragHelperCallback extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View view, int i) {
            return ((LayoutParams)view.getLayoutParams()).gravity == Gravity.RIGHT;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return (int) MathUtils.clamp(left, 0, getWidth());
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            final int childWidth = changedView.getWidth();
            mSlideOffset = (float) (childWidth - left) / childWidth;
            changedView.setVisibility(mSlideOffset == 0f ? INVISIBLE : VISIBLE);
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();

            int left = xvel < 0 || xvel == 0 && mSlideOffset > 0.5f ? 0 : childWidth;
            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }
    }

    private boolean isDetailView(View view){
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        return lp.gravity == Gravity.RIGHT;
    }

    private View findDetailView(){
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            if(isDetailView(view)){
                return view;
            }
        }
        return null;
    }

    public void openDetailView(){
        final View detailView = findDetailView();
        mDragHelper.smoothSlideViewTo(detailView, 0, detailView.getTop());
        invalidate();
    }

    public void openLeftView(){
        final View detailView = findDetailView();
        mDragHelper.smoothSlideViewTo(detailView, detailView.getWidth(), detailView.getTop());
        invalidate();
    }

    public static class LayoutParams extends MarginLayoutParams{

        public int gravity = Gravity.NO_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
            this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(int width, int height, int gravity) {
            this(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams
                ? new LayoutParams((LayoutParams) p)
                : p instanceof ViewGroup.MarginLayoutParams
                ? new LayoutParams((MarginLayoutParams) p)
                : new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mSlideOffset == 1f) {
            KeyEventCompat.startTracking(event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mSlideOffset == 1f) {
            openLeftView();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
