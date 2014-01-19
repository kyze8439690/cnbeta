package com.yugy.cnbeta.ui.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.MainActivity;
import com.yugy.cnbeta.ui.activity.NewsActivity;
import com.yugy.cnbeta.ui.adapter.CardsAnimationAdapter;
import com.yugy.cnbeta.ui.adapter.NewestNewsListAdapter;
import com.yugy.cnbeta.ui.listener.ListViewScrollObserver;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.ui.view.NewsListItem;
import com.yugy.cnbeta.utils.DebugUtils;
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
public class NewestNewsFragment extends ListFragment implements OnRefreshListener, OnItemLongClickListener, MainNewsFragmentBase {

    private static final int ACTION_NONE = 0;
    private static final int ACTION_REFRESH = 1;
    private static final int ACTION_GET_NEXT_PAGE = 2;

    private int mCurrentAction = ACTION_NONE;
    private String mFromArticleId;
    private boolean mDataLoaded = false;
    private boolean mLoading = false;

    private PullToRefreshLayout mPullToRefreshLayout;
    private CardsAnimationAdapter mCardsAnimationAdapter;
    private NewestNewsListAdapter mAdapter;
    private OnFragmentItemClickListener mOnFragmentItemClickListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DebugUtils.log("NewestNewsFragment onViewCreated");

        ViewGroup viewGroup = (ViewGroup) view;
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())
                .listener(this)
                .setup(mPullToRefreshLayout);
        getListView().setOnItemLongClickListener(this);
        getListView().setOverScrollMode(OVER_SCROLL_NEVER);
        getListView().setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        int padding = ScreenUtils.dp(getActivity(), 8);
        getListView().setPadding(padding, padding, padding, padding);
        getListView().setClipToPadding(false);
        getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));
        getListView().setDividerHeight(padding);

        mOnFragmentItemClickListener = (MainActivity) getActivity();

        mCurrentAction = ACTION_REFRESH;
        mFromArticleId = "0";
        loadData();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", mAdapter.getModels().get(position));
        mOnFragmentItemClickListener.onClick(bundle);
//        Intent intent = new Intent(getActivity(), NewsActivity.class);
//        intent.putExtra("bundle",bundle);
//        startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.activity_in, 0);
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
                    new ParseTask().execute(jsonArray);
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

    @Override
    public void loadData() {
        DebugUtils.log("NewestNewsFragment loadData");
        if(!mDataLoaded && !mLoading){
            mLoading = true;
            mPullToRefreshLayout.setRefreshing(true);
            getData();
        }
    }

    private class ParseTask extends AsyncTask<JSONArray, Void, ArrayList<NewsListModel>>{

        @Override
        protected ArrayList<NewsListModel> doInBackground(JSONArray... params) {
            try {
                return getModels(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<NewsListModel> newsListModels) {
            if(newsListModels == null){
                AppMsg.makeText(getActivity(), "数据解析失败", AppMsg.STYLE_ALERT).show();
            }else if(mCurrentAction == ACTION_REFRESH){
                if(mCardsAnimationAdapter == null){
                    mAdapter = new NewestNewsListAdapter(NewestNewsFragment.this, newsListModels);
                    mCardsAnimationAdapter = new CardsAnimationAdapter(mAdapter);
                    mCardsAnimationAdapter.setAbsListView(getListView());
                    setListAdapter(mCardsAnimationAdapter);
                    mDataLoaded = true;
                }else{
                    mAdapter.setModels(newsListModels);
                    mCardsAnimationAdapter.notifyDataSetChanged();
                    mDataLoaded = true;
                }
            }else if(mCurrentAction == ACTION_GET_NEXT_PAGE){
                mAdapter.getModels().addAll(newsListModels);
                mCardsAnimationAdapter.notifyDataSetChanged();
            }
            mCurrentAction = ACTION_NONE;
            mPullToRefreshLayout.setRefreshComplete();
            mLoading = false;
            super.onPostExecute(newsListModels);
        }
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
