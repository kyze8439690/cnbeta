package com.yugy.cnbeta.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;

import com.yugy.cnbeta.R;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.fragment.CommentFragment;

/**
 * Created by yugy on 14-1-8.
 */
public class CommentActivity extends SwipeBackActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        CommentFragment commentFragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("id", getIntent().getStringExtra("id"));
        commentFragment.setArguments(args);
        getFragmentManager().beginTransaction().add(R.id.comment_container, commentFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancelRequests(this);
        super.onDestroy();
    }

}
