package com.yugy.cnbeta.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsCommentModel;

/**
 * Created by yugy on 14-1-8.
 */
public class CommentListItem extends RelativeLayout{
    public CommentListItem(Context context) {
        super(context);
        init();
    }

    public CommentListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CommentListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mName;
    private TextView mContent;
    private TextView mSupport;
    private TextView mAgainst;
    private RelativeTimeTextView mTime;

    private void init(){
        inflate(getContext(), R.layout.view_commentlistitem, this);
        mName = (TextView) findViewById(R.id.commentlist_item_name);
        mContent = (TextView) findViewById(R.id.commentlist_item_content);
        mSupport = (TextView) findViewById(R.id.commentlist_item_support);
        mAgainst = (TextView) findViewById(R.id.commentlist_item_against);
        mTime = (RelativeTimeTextView) findViewById(R.id.commentlist_item_time);
    }

    public void parse(NewsCommentModel model){
        mName.setText(model.name);
        mContent.setText(model.comment);
        mSupport.setText("支持(" + model.supportCount + ")");
        mAgainst.setText("反对(" + model.againstCount + ")");
        mTime.setReferenceTime(model.time);
    }
}
