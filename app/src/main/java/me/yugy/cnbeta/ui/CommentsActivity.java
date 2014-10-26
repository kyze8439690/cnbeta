package me.yugy.cnbeta.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.CommentsAdapter;
import me.yugy.cnbeta.adapter.CommentsPagerAdapter;
import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.model.NewsContent;
import me.yugy.cnbeta.network.Volley;
import me.yugy.cnbeta.vendor.CnBeta;
import me.yugy.cnbeta.widget.PagerSlidingTabStrip;

/**
 * Created by gzyanghui on 2014/9/5.
 */
public class CommentsActivity extends ActionBarActivity{

    public static void launch(Context context, int sid, String sn){
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("sid", sid);
        if(sn != null){
            intent.putExtra("sn", sn);
        }
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.abc_fade_out);
    }

    @InjectView(R.id.pager_tab) PagerSlidingTabStrip mPagerTabStrip;
    @InjectView(R.id.viewpager) ViewPager mViewPager;

    private CommentsPagerAdapter mCommentsPagerAdapter;
    private CommentsFragment mAllCommentsFragment;
    private CommentsFragment mHotCommentsFragment;

    private int mSid;
    private String mSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            mAllCommentsFragment = new  CommentsFragment();
            mHotCommentsFragment = new CommentsFragment();
            loadData();
        }else{
            mAllCommentsFragment = (CommentsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "allComments");
            mHotCommentsFragment = (CommentsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "hotComments");
            loadData();
        }
        mCommentsPagerAdapter = new CommentsPagerAdapter(getSupportFragmentManager(), new CommentsFragment[]{
                mAllCommentsFragment,
                mHotCommentsFragment
        });
        mViewPager.setAdapter(mCommentsPagerAdapter);
        mPagerTabStrip.setViewPager(mViewPager);
    }

    private void loadData(){
        mSid = getIntent().getIntExtra("sid", 0);
        if(getIntent().hasExtra("sn")){
            mSn = getIntent().getStringExtra("sn");
            getData(1);
        }else{
            getSn();
        }
    }

    private void getSn(){
        CnBeta.getNewsContent(this, mSid, new Response.Listener<NewsContent>() {
            @Override
            public void onResponse(NewsContent response) {
                mSn = response.sn;
                getData(1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getCause() instanceof TimeoutError){
                    Toast.makeText(CommentsActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData(int page){
        CnBeta.getNewsComments(this, mSid, page, mSn, new Response.Listener<Comments>() {
            @Override
            public void onResponse(Comments response) {
                mAllCommentsFragment.setComments(response, CommentsAdapter.TYPE_ALL_COMMENTS);
                mHotCommentsFragment.setComments(response, CommentsAdapter.TYPE_HOT_COMMENTS);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.getCause() instanceof TimeoutError){
                    Toast.makeText(CommentsActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "allComments", mAllCommentsFragment);
        getSupportFragmentManager().putFragment(outState, "hotComments", mHotCommentsFragment);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        Volley.getInstance().cancelAll(this);
        super.onDestroy();
    }

}
