package com.yugy.cnbeta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.model.NewsListModel;
import com.yugy.cnbeta.network.RequestManager;
import com.yugy.cnbeta.sdk.Cnbeta;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackActivity;
import com.yugy.cnbeta.ui.activity.swipeback.SwipeBackLayout;
import com.yugy.cnbeta.ui.view.RefreshActionItem;
import com.yugy.cnbeta.utils.DebugUtils;
import com.yugy.cnbeta.utils.MessageUtils;
import com.yugy.cnbeta.utils.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.yugy.cnbeta.ui.view.RefreshActionItem.RefreshActionListener;

/**
 * Created by yugy on 14-1-7.
 */
public class NewsActivity extends SwipeBackActivity implements RefreshActionListener{

    private NewsListModel mData;

    private LinearLayout mContainer;
    private TextView mTitle;
    private TextView mCommentCount;
    private TextView mTime;
    private RefreshActionItem mRefreshActionItem;
    private ShareActionProvider mShareActionProvider;
    private int mContentPadding;
    private int mContentMargin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_solid_bg)
                .headerLayout(R.layout.view_news_header)
                .contentLayout(R.layout.activity_news);
        setContentView(helper.createView(this));
        helper.initActionBar(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        mContentPadding = (int) getResources().getDimension(R.dimen.news_content_padding);
        mContentMargin = (int) getResources().getDimension(R.dimen.news_content_margin);

        mContainer = (LinearLayout) findViewById(R.id.news_container);
        mTitle = (TextView) findViewById(R.id.news_header_title);
        mCommentCount = (TextView) findViewById(R.id.news_header_comment_count);
        mTime = (TextView) findViewById(R.id.news_header_time);

        mData = getIntent().getParcelableExtra("data");

        mTitle.setText(mData.title);
        mCommentCount.setText(mData.commentCount);
        mTime.setText(mData.time);

        TextView summary = getNewTextView();
        summary.setText(mData.summary);
        mContainer.addView(summary);

        getArticleData();
    }

    private void getArticleData(){
        Cnbeta.getNewsContent(this, mData.id,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    mContainer.removeAllViews();
                    try {
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if(jsonObject.getString("type").equals("text")){
                                TextView textView = getNewTextView();
                                textView.setText(Html.fromHtml(jsonObject.getString("value")));
                                mContainer.addView(textView);
                            }else if(jsonObject.getString("type").equals("img")){
                                ImageView imageView = getNewImageView();
                                ImageLoader.getInstance().displayImage(jsonObject.getString("value"), imageView);
                                mContainer.addView(imageView);
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    mRefreshActionItem.setRefreshing(false);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    MessageUtils.toast(NewsActivity.this, "获取数据失败");
                    mRefreshActionItem.setRefreshing(false);
                }
            }
        );
    }

    private void setShareIntent(Intent shareIntent){
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private TextView getNewTextView(){
        TextView textView = new TextView(this);
        textView.setAutoLinkMask(Linkify.ALL);
        textView.setPadding(mContentPadding, mContentPadding, mContentPadding, mContentMargin);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setLineSpacing(0, 1.4f);
        return textView;
    }

    private ImageView getNewImageView(){
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginRight = ScreenUtils.dp(this, 50);
        layoutParams.setMargins(mContentPadding, mContentMargin, marginRight, mContentMargin);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news, menu);

        MenuItem shareItem = menu.findItem(R.id.news_action_share);
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        StringBuilder shareText = new StringBuilder("《");
        shareText.append(mData.title);
        shareText.append("》 ");
        shareText.append("http://www.cnbeta.com/articles/");
        shareText.append(mData.id);
        shareText.append(".htm");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        shareIntent.setType("text/plain");
        setShareIntent(shareIntent);

        MenuItem refreshItem = menu.findItem(R.id.news_action_refresh);
        mRefreshActionItem = (RefreshActionItem) refreshItem.getActionView();
        mRefreshActionItem.setMenuItem(refreshItem);
        mRefreshActionItem.setRefreshActionListener(this);
        mRefreshActionItem.setRefreshing(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if(NavUtils.shouldUpRecreateTask(this, upIntent)){
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }else{
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        mRefreshActionItem.setRefreshing(true);
        getArticleData();
    }

    @Override
    protected void onDestroy() {
        RequestManager.getInstance().cancelRequests(this);
        super.onDestroy();
    }
}
