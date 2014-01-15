package com.yugy.cnbeta.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.HotCommentModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.NewsActivity;
import com.yugy.cnbeta.ui.adapter.CardsAnimationAdapter;
import com.yugy.cnbeta.ui.adapter.HotCommentListAdapter;
import com.yugy.cnbeta.ui.listener.ListViewScrollObserver;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by yugy on 14-1-9.
 */
public class HotCommentListFragment extends ListFragment implements MainFragmentBase{

    private String mFromCommentId = "0";

    private PullToRefreshLayout mPullToRefreshLayout;
    private ListViewScrollObserver mListViewScrollObserver;
    private CardsAnimationAdapter mCardsAnimationAdapter;
    private HotCommentListAdapter mAdapter;

    private boolean mDataLoaded = false;
    private boolean mLoading = false;

    public HotCommentListFragment(){
        mListViewScrollObserver = new ListViewScrollObserver();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .setup(mPullToRefreshLayout);

        getListView().setOnScrollListener(mListViewScrollObserver);
        getListView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        getListView().setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        int padding = ScreenUtils.dp(getActivity(), 8);
        getListView().setPadding(padding, ScreenUtils.dp(getActivity(), 56), padding, padding);
        getListView().setClipToPadding(false);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        getListView().setDividerHeight(padding);
    }

    public void setOnScrollUpAndDownListener(ListViewScrollObserver.OnListViewScrollListener listener){
        mListViewScrollObserver.setOnScrollUpAndDownListener(listener);
    }

    public void setPullToRefreshing(){
        mPullToRefreshLayout.setRefreshing(true);
    }

    public void getData(){
        setEmptyText("正在加载...");
        Cnbeta.getHotComment(getActivity(), mFromCommentId,
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
                    mPullToRefreshLayout.setRefreshComplete();
                }
            }
        );
    }

    private ArrayList<HotCommentModel> getModels(JSONArray jsonArray) throws JSONException{
        ArrayList<HotCommentModel> models = new ArrayList<HotCommentModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            HotCommentModel model = new HotCommentModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
            if(i == jsonArray.length() - 1){
                mFromCommentId = model.id;
            }
        }
        return models;
    }

    @Override
    public void loadData() {
        if(!mDataLoaded && !mLoading){
            mLoading = true;
            mPullToRefreshLayout.setRefreshing(false);
            mPullToRefreshLayout.setRefreshing(true);
            mAdapter = new HotCommentListAdapter(this);
            mCardsAnimationAdapter = new CardsAnimationAdapter(mAdapter);
            mCardsAnimationAdapter.setAbsListView(getListView());
            getData();
        }
    }

    private class ParseTask extends AsyncTask<JSONArray, Void, ArrayList<HotCommentModel>>{

        @Override
        protected ArrayList<HotCommentModel> doInBackground(JSONArray... params) {
            try {
                return getModels(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<HotCommentModel> hotCommentModels) {
            if(hotCommentModels == null){
                AppMsg.makeText(getActivity(), "数据解析失败", AppMsg.STYLE_ALERT).show();
            }else if(mAdapter.getModels().size() == 0){
                mAdapter.getModels().addAll(hotCommentModels);
                setListAdapter(mCardsAnimationAdapter);
                mDataLoaded = true;
            }else{
                mAdapter.getModels().addAll(hotCommentModels);
                mCardsAnimationAdapter.notifyDataSetChanged();
                mDataLoaded = true;
            }
            mLoading = false;
            mPullToRefreshLayout.setRefreshComplete();
            super.onPostExecute(hotCommentModels);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("data", mAdapter.getModels().get(position));
        intent.putExtra("hotComment", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, 0);
    }
}
