package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.TopTenNewsModel;

/**
 * Created by yugy on 14-1-9.
 */
public class TopTenNewsListItem extends RelativeLayout{
    public TopTenNewsListItem(Context context) {
        super(context);
        init();
    }

    public TopTenNewsListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopTenNewsListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private TextView mReadCount;
    private TextView mCommentCount;

    private void init(){
        inflate(getContext(), R.layout.view_toptenlistitem, this);
        mTitle = (TextView) findViewById(R.id.topten_title);
        mReadCount = (TextView) findViewById(R.id.topten_read_count);
        mCommentCount = (TextView) findViewById(R.id.topten_comment_count);
    }

    public void parse(TopTenNewsModel model){
        mTitle.setText(model.title);
        mReadCount.setText(model.readCount + "次阅读");
        mCommentCount.setText(model.commentCount + "");
    }
}
