package me.yugy.cnbeta.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.app.common.core.BaseFragment;
import me.yugy.app.common.view.DividerItemDecoration;
import me.yugy.app.common.view.PauseOnScrollListener2;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.activity.ArticleActivity;
import me.yugy.cnbeta.adapter.AllNewsAdapter;
import me.yugy.cnbeta.dao.dbinfo.AllNewsDBInfo;
import me.yugy.cnbeta.dao.datahelper.AllNewsDataHelper;
import me.yugy.cnbeta.model.News;
import me.yugy.cnbeta.network.CnBeta;

/**
 * Created by yugy on 14/10/19.
 */
public class AllNewsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @InjectView(R.id.list) RecyclerView mRecyclerView;

    private AllNewsAdapter mAdapter;
    private AllNewsDataHelper mDataHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new AllNewsAdapter(this);
        mDataHelper = new AllNewsDataHelper();
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(
                        ResourcesCompat.getDrawable(
                                getResources(), R.drawable.news_list_divider, getActivity().getTheme())));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new PauseOnScrollListener2(ImageLoader.getInstance(), true, true));
        mRefreshLayout.setColorSchemeResources(R.color.all_news_color);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
     public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mDataHelper.getCount() == 0){
//            ((MainActivity)getActivity()).mRefreshButton.setRefresh(true);
            refresh();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onNewsSelect(int sid){

        ArticleActivity.launch(getActivity(), sid, ArticleFragment.NEWS_TYPE_ALL_NEWS);

    }

    private void refresh() {
        if(!mRefreshLayout.isRefreshing()){
            mRefreshLayout.setRefreshing(true);
        }
        CnBeta.getNews(getActivity(), CnBeta.TYPE_ALL, 0, new Response.Listener<News[]>() {
            @Override
            public void onResponse(final News[] response) {
                if (response.length > 0) {
                    mDataHelper.bulkInsert(response);
                }
                mRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getCause() instanceof TimeoutError){
                    Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }

                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return mDataHelper.getCursorLoader(AllNewsDBInfo.SID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
}
