package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.HotCommentModel;

/**
 * Created by yugy on 14-1-9.
 */
public class HotCommentListItem extends RelativeLayout{
    public HotCommentListItem(Context context) {
        super(context);
        init();
    }

    public HotCommentListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HotCommentListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mComment;
    private TextView mTitle;
    private TextView mCommentCount;

    private void init(){
        inflate(getContext(), R.layout.view_hotcommentlistitem, this);
        mComment = (TextView) findViewById(R.id.hotcomment_comment);
        mTitle = (TextView) findViewById(R.id.hotcomment_title);
        mCommentCount = (TextView) findViewById(R.id.hotcomment_comment_count);
    }

    public void parse(HotCommentModel model){
        mComment.setText("“" + model.comment + "”");
        mTitle.setText(model.articleTitle);
        mCommentCount.setText(model.commentCount);
    }
}
