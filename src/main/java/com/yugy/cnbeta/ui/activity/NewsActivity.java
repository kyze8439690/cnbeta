package com.yugy.cnbeta.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.HotCommentModel;
import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.model.TopTenNewsModel;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.fragment.NewsFragment;
import com.yugy.cnbeta.ui.listener.OnCommentButtonClickListener;
import com.yugy.cnbeta.ui.view.FadingActionBarHelper;
import com.yugy.cnbeta.ui.view.KenBurnsView;
import com.yugy.cnbeta.ui.view.RefreshActionItem;
import com.yugy.cnbeta.ui.view.RelativeTimeTextView;
import com.yugy.cnbeta.ui.view.SelectorImageView;
import com.yugy.cnbeta.utils.MessageUtils;
import com.yugy.cnbeta.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.yugy.cnbeta.ui.view.RefreshActionItem.RefreshActionListener;

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
