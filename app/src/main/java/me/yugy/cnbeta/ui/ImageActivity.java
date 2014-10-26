package me.yugy.cnbeta.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.widget.CircularProgressDrawable;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yugy on 14/10/25.
 */
public class ImageActivity extends ActionBarActivity{

    public static void launch(Context context, ImageDetail imageDetail){
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("image_detail", imageDetail);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(0, 0);
    }

    private static final Interpolator sDecelerator = new DecelerateInterpolator();
    private static final Interpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIMATION_DURATION = 300;
    private static final DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(false)
            .cacheOnDisk(true)
            .build();

    private ImageDetail mImageDetail;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private ColorDrawable mBackground = new ColorDrawable(Color.BLACK);
    private PhotoViewAttacher mAttacher;

    @InjectView(R.id.image_layout) View mRootLayout;
    @InjectView(R.id.image) ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageDetail = getIntent().getParcelableExtra("image_detail");
        ImageLoader.getInstance().displayImage(mImageDetail.imageUrl, mImage, sOptions, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mAttacher = new PhotoViewAttacher(mImage);
                mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float v, float v2) {
                        finish();
                    }
                });
            }
        });
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            mRootLayout.setBackgroundColor(Color.BLACK);
        }else {
            mRootLayout.setBackground(mBackground);
        }

        if(savedInstanceState == null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ViewTreeObserver observer = mImage.getViewTreeObserver();
                observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        mImage.getViewTreeObserver().removeOnPreDrawListener(this);
                        int[] screenLocation = new int[2];
                        mImage.getLocationOnScreen(screenLocation);
                        mLeftDelta = mImageDetail.left - screenLocation[0];
                        mTopDelta = mImageDetail.top - screenLocation[1];
                        mWidthScale = (float) mImageDetail.width / mImage.getWidth();
                        mHeightScale = (float) mImageDetail.height / mImage.getHeight();

                        ViewHelper.setPivotX(mImage, 0);
                        ViewHelper.setPivotY(mImage, 0);
                        ViewHelper.setScaleX(mImage, mWidthScale);
                        ViewHelper.setScaleY(mImage, mHeightScale);
                        ViewHelper.setTranslationX(mImage, mLeftDelta);
                        ViewHelper.setTranslationY(mImage, mTopDelta);

                        ViewPropertyAnimator.animate(mImage).setDuration(ANIMATION_DURATION).
                                scaleX(1).scaleY(1).
                                translationX(0).translationY(0).
                                setInterpolator(sDecelerator).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mImage.setLayoutParams(new FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            }
                        });

                        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
                        bgAnim.setDuration(ANIMATION_DURATION);
                        bgAnim.start();

                        return true;
                    }
                });
            }else{
                mImage.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    @OnClick(R.id.image_layout)
    void onClickOutside(){
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image, menu);
        return true;
    }

    private boolean mIsShareLoading = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                mIsShareLoading = true;
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setIndeterminate(true);
                dialog.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this).color(Color.WHITE).build());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mIsShareLoading = false;
                    }
                });
                dialog.setMessage("Loading...");
                dialog.show();
                ImageLoader.getInstance().loadImage(mImageDetail.imageUrl, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if(mIsShareLoading) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(ImageLoader.getInstance().getDiskCache().get(imageUri)));
                            shareIntent.putExtra(Intent.EXTRA_TEXT, mImageDetail.title + " " + mImageDetail.newsUrl);
                            shareIntent.setType("*/*");
                            startActivity(Intent.createChooser(shareIntent, getText(R.string.share)));
                            dialog.dismiss();
                        }
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            mImage.setLayoutParams(lp);
            mAttacher.setScale(1f, false);
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 255, 0);
            bgAnim.setDuration(ANIMATION_DURATION);
            bgAnim.start();
            ViewPropertyAnimator.animate(mImage).setDuration(ANIMATION_DURATION)
                    .scaleX(mWidthScale).scaleY(mHeightScale)
                    .translationX(mLeftDelta).translationY(mTopDelta)
                    .setInterpolator(sAccelerator)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ImageActivity.super.finish();
                        }
                    });
        }else{
            super.finish();
        }
    }

    public static class ImageDetail implements Parcelable {
        public int left;
        public int top;
        public int width;
        public int height;
        public String imageUrl;
        public String title;
        public String newsUrl;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.left);
            dest.writeInt(this.top);
            dest.writeInt(this.width);
            dest.writeInt(this.height);
            dest.writeString(this.imageUrl);
            dest.writeString(this.title);
            dest.writeString(this.newsUrl);
        }

        public ImageDetail() {
        }

        private ImageDetail(Parcel in) {
            this.left = in.readInt();
            this.top = in.readInt();
            this.width = in.readInt();
            this.height = in.readInt();
            this.imageUrl = in.readString();
            this.title = in.readString();
            this.newsUrl = in.readString();
        }

        public static final Parcelable.Creator<ImageDetail> CREATOR = new Parcelable.Creator<ImageDetail>() {
            public ImageDetail createFromParcel(Parcel source) {
                return new ImageDetail(source);
            }

            public ImageDetail[] newArray(int size) {
                return new ImageDetail[size];
            }
        };
    }

}
