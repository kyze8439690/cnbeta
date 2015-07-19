package me.yugy.cnbeta.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import butterknife.ButterKnife;
import me.yugy.app.common.BaseActivity;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.network.Volley;
import me.yugy.cnbeta.fragment.CommentsFragment;

/**
 * Created by gzyanghui on 2014/9/5.
 */
public class CommentsActivity extends BaseActivity {

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
