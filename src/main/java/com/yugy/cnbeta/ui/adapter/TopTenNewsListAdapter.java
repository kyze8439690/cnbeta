package com.yugy.cnbeta.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.model.TopTenNewsModel;
import com.yugy.cnbeta.ui.view.TopTenNewsListItem;

import java.util.ArrayList;

/**
 * Created by yugy on 14-1-9.
 */
public class TopTenNewsListAdapter extends BaseAdapter{

    private ArrayList<TopTenNewsModel> mModels;
    private final Context mContext;

    public ArrayList<TopTenNewsModel> getModels() {
        return mModels;
    }

    public TopTenNewsListAdapter(Context context, ArrayList<TopTenNewsModel> models){
        mModels = models;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public TopTenNewsModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TopTenNewsListItem item = (TopTenNewsListItem) convertView;
        if(item == null){
            item = new TopTenNewsListItem(mContext);
        }
        item.parse(getItem(position));
        return item;
    }
}
