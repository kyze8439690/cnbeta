package com.yugy.cnbeta.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.TopTenNewsModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.NewsActivity;
import com.yugy.cnbeta.ui.adapter.TopTenNewsListAdapter;
import com.yugy.cnbeta.ui.listener.ListViewScrollObserver;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * Created by yugy on 14-1-9.
 */
public class TopTenNewsFragment extends ListFragment{

    private ListViewScrollObserver mListViewScrollObserver;
    private TopTenNewsListAdapter mAdapter;

    public TopTenNewsFragment(){
        mListViewScrollObserver = new ListViewScrollObserver();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(mListViewScrollObserver);
        getListView().setBackgroundColor(Color.WHITE);
        getListView().setOverScrollMode(OVER_SCROLL_NEVER);
        getListView().setPadding(0, ScreenUtils.dp(getActivity(), 48), 0, 0);
        getListView().setClipToPadding(false);
        getListView().setDividerHeight(1);

        getData();
    }

    public void setOnScrollUpAndDownListener(ListViewScrollObserver.OnListViewScrollListener listener){
        mListViewScrollObserver.setOnScrollUpAndDownListener(listener);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("data", mAdapter.getModels().get(position));
        intent.putExtra("top10", true);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, 0);
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
            }
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
