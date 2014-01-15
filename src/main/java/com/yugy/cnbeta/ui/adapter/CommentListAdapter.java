package com.yugy.cnbeta.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.cnbeta.model.NewsCommentModel;
import com.yugy.cnbeta.ui.view.CommentListItem;
import com.yugy.cnbeta.utils.MessageUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yugy on 14-1-8.
 */
public class CommentListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<NewsCommentModel> mModels;

    public static final int TYPE_TIME = 0;
    public static final int TYPE_HOT = 1;

    private int mType= TYPE_TIME;

    public CommentListAdapter(Context context){
        mContext = context;
        mModels = new ArrayList<NewsCommentModel>();
    }

    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("发表于 yyyy-MM-dd HH:mm:ss");

    public void setType(int type){
        if(mType != type){
            mType = type;
            switch (type){
                case TYPE_TIME:
                    Collections.sort(mModels, new Comparator<NewsCommentModel>() {
                        @Override
                        public int compare(NewsCommentModel lhs, NewsCommentModel rhs) {
                            return new Long(lhs.time).compareTo(rhs.time);
                        }
                    });
                    break;
                case TYPE_HOT:
                    Collections.sort(mModels, new Comparator<NewsCommentModel>() {
                        @Override
                        public int compare(NewsCommentModel lhs, NewsCommentModel rhs) {
                            Integer left = Integer.valueOf(lhs.againstCount) + Integer.valueOf(lhs.supportCount);
                            Integer right = Integer.valueOf(rhs.againstCount) + Integer.valueOf(rhs.supportCount);
                            return right.compareTo(left);
                        }
                    });
                    break;
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public NewsCommentModel getItem(int position) {

        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentListItem item = (CommentListItem) convertView;
        if(item == null){
            item = new CommentListItem(mContext);
        }
        item.parse(getItem(position));
        return item;
    }

    public ArrayList<NewsCommentModel> getModels() {
        return mModels;
    }
}
