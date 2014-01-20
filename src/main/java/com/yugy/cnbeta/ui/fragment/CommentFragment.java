package com.yugy.cnbeta.ui.fragment;

import android.app.ActionBar;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsCommentModel;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.adapter.CommentListAdapter;
import com.yugy.cnbeta.ui.view.AppMsg;
import com.yugy.cnbeta.ui.view.RefreshActionItem;
import com.yugy.cnbeta.utils.DebugUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;

import static android.app.ActionBar.OnNavigationListener;
import static com.yugy.cnbeta.ui.view.RefreshActionItem.RefreshActionListener;

/**
 * Created by yugy on 14-1-8.
 */
public class CommentFragment extends ListFragment implements RefreshActionListener, OnNavigationListener{

    private RefreshActionItem mRefreshActionItem;
    private String mArticleId;
    private CommentListAdapter mCommentListAdapter;
    private ArrayAdapter<String> mSpinnerAdapter;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        getListView().setBackgroundColor(Color.WHITE);
        getListView().setDividerHeight(1);

        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.comment_array));

        getActivity().getActionBar().setListNavigationCallbacks(mSpinnerAdapter, this);

        mArticleId = getArguments().getString("id", "3");
        mCommentListAdapter = new CommentListAdapter(getActivity());
        getData();

    }

    private void getData(){
        Cnbeta.getNewsComment(getActivity(), mArticleId,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    DebugUtils.log(jsonArray);
                    if(jsonArray.length() == 0){
                        AppMsg.makeText(getActivity(), "目前还没有评论", AppMsg.STYLE_INFO).show();
                    }
                    try {
                        mCommentListAdapter.getModels().clear();
                        for(int i = 0; i < jsonArray.length(); i++){
                            NewsCommentModel model = new NewsCommentModel();
                            model.parse(jsonArray.getJSONObject(i));
                            mCommentListAdapter.getModels().add(model);
                        }
                        setListAdapter(mCommentListAdapter);
                        getActivity().getActionBar().setSelectedNavigationItem(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        AppMsg.makeText(getActivity(), "评论数据解析失败", AppMsg.STYLE_ALERT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        AppMsg.makeText(getActivity(), "评论时间解析失败", AppMsg.STYLE_ALERT).show();
                    }
                    if(mRefreshActionItem != null){
                        mRefreshActionItem.setRefreshing(false);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if(mRefreshActionItem != null){
                        mRefreshActionItem.setRefreshing(false);
                    }
                    AppMsg.makeText(getActivity(), "获取评论失败, 请稍后重试", AppMsg.STYLE_ALERT).show();
                }
            }
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.comment, menu);
        MenuItem refreshItem = menu.findItem(R.id.comment_action_refresh);
        mRefreshActionItem = (RefreshActionItem) refreshItem.getActionView();
        mRefreshActionItem.setMenuItem(refreshItem);
        mRefreshActionItem.setRefreshActionListener(this);
        mRefreshActionItem.setRefreshing(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
                if(NavUtils.shouldUpRecreateTask(getActivity(), upIntent)){
                    TaskStackBuilder.create(getActivity()).addNextIntentWithParentStack(upIntent).startActivities();
                }else{
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(getActivity(), upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        mRefreshActionItem.setRefreshing(true);
        getData();
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition){
            case 0:
                mCommentListAdapter.setType(CommentListAdapter.TYPE_TIME);
                return true;
            case 1:
                mCommentListAdapter.setType(CommentListAdapter.TYPE_HOT);
                return true;
            default:
                return false;
        }
    }
}
