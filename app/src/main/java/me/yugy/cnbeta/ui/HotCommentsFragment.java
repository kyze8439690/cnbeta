package me.yugy.cnbeta.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.HotCommentsAdapter;
import me.yugy.cnbeta.dao.dbinfo.HotCommentsDBInfo;
import me.yugy.cnbeta.dao.datahelper.HotCommentsDataHelper;
import me.yugy.cnbeta.model.HotComment;
import me.yugy.cnbeta.vendor.CnBeta;

/**
 * Created by yugy on 2014/9/6.
 */
public class HotCommentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private HotCommentsAdapter mAdapter;
    private HotCommentsDataHelper mDataHelper;

    @InjectView(R.id.list) RecyclerView mRecyclerView;
    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout mRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new HotCommentsAdapter(this);
        mDataHelper = new HotCommentsDataHelper();
        getActivity().getSupportLoaderManager().initLoader(1, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hotcomments, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
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
            refresh();
        }
    }

    public void onNewsSelect(int sid){

        ArticleActivity.launch(getActivity(), sid, ArticleFragment.NEWS_TYPE_HOT_COMMENT);

    }

    private void refresh() {
        if(!mRefreshLayout.isRefreshing()){
            mRefreshLayout.setRefreshing(true);
        }
        CnBeta.getHotComments(getActivity(), 1, new Response.Listener<HotComment[]>() {
            @Override
            public void onResponse(final HotComment[] response) {
                updateData(response);
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

    private void updateData(HotComment[] response) {
        if (response.length > 0) {
            mDataHelper.bulkInsert(response);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return mDataHelper.getCursorLoader(HotCommentsDBInfo.SID + " DESC");
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
