package me.yugy.cnbeta.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.CommentsAdapter;
import me.yugy.cnbeta.adapter.CommentsPagerAdapter;
import me.yugy.cnbeta.model.Comment;
import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.model.NewsContent;
import me.yugy.cnbeta.network.Volley;
import me.yugy.cnbeta.network.CnBeta;
import me.yugy.cnbeta.widget.PagerSlidingTabStrip;

/**
 * Created by gzyanghui on 2014/9/5.
 */
public class CommentsActivity extends ActionBarActivity{

    public static void launch(Context context, int sid){
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra("sid", sid);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.abc_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int sid = getIntent().getIntExtra("sid", 0);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, CommentsFragment.newInstance(sid))
                    .commit();
        }
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
