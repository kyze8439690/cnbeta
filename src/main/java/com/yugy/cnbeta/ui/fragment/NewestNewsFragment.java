package com.yugy.cnbeta.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.NewsActivity;
import com.yugy.cnbeta.ui.adapter.NewestNewsListAdapter;
import com.yugy.cnbeta.ui.listener.ListViewScrollObserver;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.ui.view.NewsListItem;
import com.yugy.cnbeta.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import static android.view.View.OVER_SCROLL_NEVER;
import static android.widget.AdapterView.OnItemLongClickListener;

/**
 * Created by yugy on 14-1-6.
 */
public class NewestNewsFragment extends ListFragment implements OnRefreshListener, OnItemLongClickListener{

    private static final int ACTION_NONE = 0;
    private static final int ACTION_REFRESH = 1;
    private static final int ACTION_GET_NEXT_PAGE = 2;

    private int mCurrentAction = ACTION_NONE;
    private String mFromArticleId;

    private PullToRefreshLayout mPullToRefreshLayout;
    private NewestNewsListAdapter mAdapter;
    private ListViewScrollObserver mListViewScrollObserver;

    public NewestNewsFragment(){
        mListViewScrollObserver = new ListViewScrollObserver();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                .listener(this)
                .setup(mPullToRefreshLayout);

        PauseOnScrollListener pauseOnScrollListener = new PauseOnScrollListener(ImageLoader.getInstance(), true, true, mListViewScrollObserver);
        getListView().setOnScrollListener(pauseOnScrollListener);
        getListView().setOnItemLongClickListener(this);
        getListView().setBackgroundColor(Color.WHITE);
        getListView().setOverScrollMode(OVER_SCROLL_NEVER);
        getListView().setPadding(0, ScreenUtils.dp(getActivity(), 48), 0, 0);
        getListView().setClipToPadding(false);
        getListView().setDividerHeight(1);

        mCurrentAction = ACTION_REFRESH;
        mFromArticleId = "0";
        mPullToRefreshLayout.setRefreshing(true);
        getData();
    }

    public void setOnScrollUpAndDownListener(ListViewScrollObserver.OnListViewScrollListener listener){
        mListViewScrollObserver.setOnScrollUpAndDownListener(listener);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        intent.putExtra("data", mAdapter.getModels().get(position));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, 0);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ((NewsListItem) view).toggleMaxLine();
        return true;
    }

    @Override
    public void onRefreshStarted(View view) {
        mCurrentAction = ACTION_REFRESH;
        mFromArticleId = "0";
        getData();
    }

    private void getData(){
        setEmptyText("正在加载...");
        Cnbeta.getNewsList(getActivity(), mFromArticleId,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
//                        DebugUtils.log(jsonArray);
                    try {
                        ArrayList<NewsListModel> models = getModels(jsonArray);
                        if(mCurrentAction == ACTION_REFRESH){
                            if(mAdapter == null){
                                mAdapter = new NewestNewsListAdapter(NewestNewsFragment.this, models);
                                setListAdapter(mAdapter);
                            }else{
                                mAdapter.setModels(models);
                                mAdapter.notifyDataSetChanged();
                            }
                        }else if(mCurrentAction == ACTION_GET_NEXT_PAGE){
                            mAdapter.getModels().addAll(models);
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        AppMsg.makeText(getActivity(), "数据解析失败", AppMsg.STYLE_ALERT).show();
                        e.printStackTrace();
                    } catch (ParseException e) {
                        AppMsg.makeText(getActivity(), "时间解析失败", AppMsg.STYLE_ALERT).show();
                        e.printStackTrace();
                    }
                    mCurrentAction = ACTION_NONE;
                    mPullToRefreshLayout.setRefreshComplete();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    mCurrentAction = ACTION_NONE;
                    mPullToRefreshLayout.setRefreshComplete();
                    AppMsg.makeText(getActivity(), "刷新失败", AppMsg.STYLE_ALERT).show();
                    setEmptyText("刷新失败，请尝试重新打开cnβ");
                    volleyError.printStackTrace();
                }
            }
        );
    }


    private ArrayList<NewsListModel> getModels(JSONArray jsonArray) throws JSONException, ParseException {
        ArrayList<NewsListModel> models = new ArrayList<NewsListModel>();
        for(int i = 0; i < jsonArray.length(); i++){
            NewsListModel model = new NewsListModel();
            model.parse(jsonArray.getJSONObject(i));
            models.add(model);
            if(i == jsonArray.length() - 1){
                mFromArticleId = model.id;
            }
        }
        return models;
    }

    public void loadNextPage(){
        mCurrentAction = ACTION_GET_NEXT_PAGE;
        mPullToRefreshLayout.setRefreshing(true);
        getData();
    }
}
