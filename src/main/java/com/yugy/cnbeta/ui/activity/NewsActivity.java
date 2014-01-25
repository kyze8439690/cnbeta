package com.yugy.cnbeta.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.fragment.NewsFragment;
import com.yugy.cnbeta.ui.listener.OnCommentButtonClickListener;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by yugy on 14-1-7.
 */
public class NewsActivity extends SwipeBackActivity implements OnCommentButtonClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBase.onCreate(this);
        setContentView(R.layout.activity_news);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState == null){
            NewsFragment newsFragment = new NewsFragment();
            newsFragment.setArguments(getIntent().getBundleExtra("bundle"));
            getFragmentManager().beginTransaction().add(R.id.news_container, newsFragment).commit();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityBase.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivityBase.onPause(this);
    }

    @Override
    protected void onDestroy() {
        ActivityBase.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_out);
    }

    @Override
    public void OnCommentClick(Bundle bundle) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in, 0);
    }
}
