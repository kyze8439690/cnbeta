package me.yugy.cnbeta.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import me.yugy.cnbeta.R;
import me.yugy.cnbeta.fragment.ArticleFragment;

/**
 * Created by yugy on 14/10/25.
 */
public class ArticleActivity extends ActionBarActivity{

    public static void launch(Context context, int sid, int type){
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra("sid", sid);
        intent.putExtra("type", type);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.abc_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        if(savedInstanceState == null){
            int sid = getIntent().getIntExtra("sid", 0);
            int type = getIntent().getIntExtra("type", 0);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ArticleFragment.newInstance(sid, type))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.slide_out_right);
    }
}
