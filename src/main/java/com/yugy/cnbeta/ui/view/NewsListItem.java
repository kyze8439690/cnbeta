package com.yugy.cnbeta.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsListModel;

/**
 * Created by yugy on 14-1-6.
 */
public class NewsListItem extends RelativeLayout{
    public NewsListItem(Context context) {
        super(context);
        init();
    }

    public NewsListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewsListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private TextView mSummary;
    private TextView mCommentCount;
    private TextView mTime;
    private boolean mShrinked = true;

    private void init(){
        inflate(getContext(), R.layout.view_newslistitem, this);
        mTitle = (TextView) findViewById(R.id.newslist_item_title);
        mSummary = (TextView) findViewById(R.id.newslist_item_summary);
        mCommentCount = (TextView) findViewById(R.id.newslist_item_comment_count);
        mTime = (TextView) findViewById(R.id.newslist_item_time);
    }

    public void parse(NewsListModel data){
        mTitle.setText(data.title);
        mSummary.setText(data.summary);
        mCommentCount.setText(data.commentCount);
        mTime.setText(data.time);
        if(!mShrinked){
            mSummary.setMaxLines(3);
            mShrinked = false;
        }
    }

    public void toggleMaxLine(){
        if(mShrinked){
            //expand
            ObjectAnimator expandAnimator = ObjectAnimator.ofInt(mSummary,
                    "maxLines", 10);
            expandAnimator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            expandAnimator.start();
        }else{
            ObjectAnimator shrinkAnimator = ObjectAnimator.ofInt(mSummary,
                    "maxLines", 4);
            shrinkAnimator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
            shrinkAnimator.start();
        }
        mShrinked = !mShrinked;
    }
}
