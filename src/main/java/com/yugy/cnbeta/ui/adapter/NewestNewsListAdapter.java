package com.yugy.cnbeta.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.ui.fragment.NewestNewsFragment;
import com.yugy.cnbeta.ui.view.NewsListItem;

import java.util.ArrayList;

/**
 * Created by yugy on 14-1-6.
 */
public class NewestNewsListAdapter extends BaseAdapter{

    private ArrayList<NewsListModel> mModels;
    private final Context mContext;
    private NewestNewsFragment mNewsFragment;

    public NewestNewsListAdapter(NewestNewsFragment fragment, ArrayList<NewsListModel> models) {
        mContext = fragment.getActivity();
        mNewsFragment = fragment;
        mModels = models;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public NewsListModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsListItem item = (NewsListItem) convertView;
        if(item == null){
            item = new NewsListItem(mContext);
        }
        item.parse(getItem(position));
        if(position == getCount() - 1){
            //loadmore
            mNewsFragment.loadNextPage();
        }
        return item;
    }

    public ArrayList<NewsListModel> getModels() {
        return mModels;
    }

    public void setModels(ArrayList<NewsListModel> models) {
        mModels = models;
    }
}
