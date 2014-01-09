package com.yugy.cnbeta.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yugy.cnbeta.model.HotCommentModel;
import com.yugy.cnbeta.ui.fragment.HotCommentListFragment;
import com.yugy.cnbeta.ui.view.HotCommentListItem;

import java.util.ArrayList;

/**
 * Created by yugy on 14-1-9.
 */
public class HotCommentListAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<HotCommentModel> mModels;
    private HotCommentListFragment mHotCommentListFragment;

    public HotCommentListAdapter(HotCommentListFragment fragment){
        mContext = fragment.getActivity();
        mModels = new ArrayList<HotCommentModel>();
        mHotCommentListFragment = fragment;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public HotCommentModel getItem(int position) {
        return mModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HotCommentListItem item = (HotCommentListItem) convertView;
        if(item == null){
            item = new HotCommentListItem(mContext);
        }
        item.parse(mModels.get(position));
        if(position == getCount() - 1){
            //loadmore
            mHotCommentListFragment.getData();
        }
        return item;
    }

    public ArrayList<HotCommentModel> getModels() {
        return mModels;
    }
}
