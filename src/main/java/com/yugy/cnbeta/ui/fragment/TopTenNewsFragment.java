package com.yugy.cnbeta.ui.fragment;

import android.app.ListFragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.cnbeta.model.TopTenNewsModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.adapter.TopTenNewsListAdapter;
import com.yugy.cnbeta.ui.listener.OnNewsItemClickListener;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.utils.DebugUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by yugy on 14-1-9.
 */
public class TopTenNewsFragment extends ListFragment implements MainNewsFragmentBase, OnRefreshListener {

    private TopTenNewsListAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;
    private OnNewsItemClickListener mOnNewsItemClickListener;

    private boolean mDataLoaded = false;
    private boolean mLoading = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        DebugUtils.log("TopTenNewsFragment onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .listener(this)
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                .setup(mPullToRefreshLayout);

        getListView().setBackgroundColor(Color.WHITE);
        getListView().setOverScrollMode(OVER_SCROLL_NEVER);
        getListView().setClipToPadding(false);
        getListView().setDividerHeight(1);

        mOnNewsItemClickListener = (OnNewsItemClickListener) getActivity();

        loadData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", mAdapter.getModels().get(position));
        bundle.putBoolean("top10", true);
        mOnNewsItemClickListener.onNewsClick(bundle);
    }

    private void getData(){
        setEmptyText("正在加载...");
        Cnbeta.getTopTenList(getActivity(),
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    new ParseTask().execute(jsonArray);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    AppMsg.makeText(getActivity(), "刷新失败", AppMsg.STYLE_ALERT).show();
                    setEmptyText("刷新失败，请尝试重新打开cnβ");
                    volleyError.printStackTrace();
                }
            }
        );
    }

    @Override
    public void loadData() {
        DebugUtils.log("TopTenNewsFragment loadData");
        if(!mDataLoaded && !mLoading){
            mLoading = true;
            mPullToRefreshLayout.setRefreshing(false);
            mPullToRefreshLayout.setRefreshing(true);
            getData();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        mDataLoaded = false;
        loadData();
    }

    private class ParseTask extends AsyncTask<JSONArray, Void, ArrayList<TopTenNewsModel>>{

        @Override
        protected ArrayList<TopTenNewsModel> doInBackground(JSONArray... params) {
            try {
                return getModels(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<TopTenNewsModel> topTenNewsModels) {
            if(topTenNewsModels == null){
                AppMsg.makeText(getActivity(), "数据解析失败", AppMsg.STYLE_ALERT).show();
            }else{
                mAdapter = new TopTenNewsListAdapter(getActivity(), topTenNewsModels);
                setListAdapter(mAdapter);
                mDataLoaded = true;
            }
            mLoading = false;
            mPullToRefreshLayout.setRefreshing(false);
            super.onPostExecute(topTenNewsModels);
        }
    }

    private ArrayList<TopTenNewsModel> getModels(JSONArray jsonArray) throws JSONException {
        ArrayList<TopTenNewsModel> models = new ArrayList<TopTenNewsModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            TopTenNewsModel model = new TopTenNewsModel();
            model.parse(i + 1, jsonArray.getJSONObject(i));
            models.add(model);
        }
        return models;
    }
}
