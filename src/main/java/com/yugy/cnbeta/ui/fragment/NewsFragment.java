package com.yugy.cnbeta.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.yugy.cnbeta.ui.activity.CommentActivity;
import com.yugy.cnbeta.ui.activity.PicActivity;
import com.yugy.cnbeta.ui.view.FadingActionBarHelper;
import com.yugy.cnbeta.ui.view.KenBurnsView;
import com.yugy.cnbeta.ui.view.RefreshActionItem;
import com.yugy.cnbeta.ui.view.RelativeTimeTextView;
import com.yugy.cnbeta.ui.view.SelectorImageView;
import com.yugy.cnbeta.utils.MessageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.inflate;
import static android.widget.LinearLayout.LayoutParams;
import static com.yugy.cnbeta.ui.view.RefreshActionItem.RefreshActionListener;

/**
 * Created by yugy on 14-1-20.
 */
public class NewsFragment extends Fragment implements RefreshActionListener{

    private LayoutParams mImageLayoutParams;
    private LinearLayout mContainer;
    private KenBurnsView mHeaderImage;
    private TextView mTitle;
    private TextView mCommentCount;
    private RelativeTimeTextView mTime;
    private Button mCommentButton;
    private RefreshActionItem mRefreshActionItem;
    private ShareActionProvider mShareActionProvider;

    private String mId;
    private String mTitleString;
    private String mCommentCountString;
    private ArrayList<String> mImgUrls = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_solid_bg)
                .headerLayout(R.layout.view_news_header)
                .contentLayout(R.layout.fragment_news);
        View rootView = helper.createView(getActivity());
        helper.initActionBar(getActivity());

        int mImageMarginBottom = (int) getResources().getDimension(R.dimen.news_image_margin_bottom);
        mImageLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mImageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mImageLayoutParams.setMargins(0, 0, 0, mImageMarginBottom);

        mContainer = (LinearLayout) rootView.findViewById(R.id.news_container);
        mHeaderImage = (KenBurnsView) rootView.findViewById(R.id.news_header_image);
        mTitle = (TextView) rootView.findViewById(R.id.news_header_title);
        mCommentCount = (TextView) rootView.findViewById(R.id.news_header_comment_count);
        mTime = (RelativeTimeTextView) rootView.findViewById(R.id.news_header_time);
        mCommentButton = (Button) rootView.findViewById(R.id.news_comment_button);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments().containsKey("top10")){
            TopTenNewsModel data = getArguments().getParcelable("data");
            mId = data.id;
            mTitleString = data.title;
            mCommentCountString = data.commentCount;
            mTime.setText(data.readCount + "次阅读");
        }else if(getArguments().containsKey("hotComment")){
            HotCommentModel data = getArguments().getParcelable("data");
            mId = data.articleId;
            mTitleString = data.articleTitle;
            mCommentCountString = data.commentCount;
            mTime.setText("“" + data.comment + "”");
        }else{
            NewsListModel data = getArguments().getParcelable("data");
            mId = data.id;
            mTitleString = data.title;
            mCommentCountString = data.commentCount;

            mTime.setReferenceTime(data.time);
            TextView summary = getNewTextView();
            summary.setText(data.summary);
            mContainer.addView(summary);
        }

        mTitle.setText(mTitleString);
        mCommentCount.setText(mCommentCountString);
        mCommentButton.setText(mCommentCountString);
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("id", mId);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.activity_in, 0);
            }
        });

        getArticleData();
    }

    private void getArticleData(){
        Cnbeta.getNewsContent(getActivity(), mId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        mContainer.removeAllViews();
                        mImgUrls.clear();
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getString("type").equals("text")) {
                                    TextView textView = getNewTextView();
                                    textView.setText(Html.fromHtml(jsonObject.getString("value")));
                                    mContainer.addView(textView);
                                } else if (jsonObject.getString("type").equals("img")) {
                                    final SelectorImageView imageView = getNewImageView();
                                    final String imgUrl = jsonObject.getString("value");
                                    mImgUrls.add(imgUrl);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getActivity(), PicActivity.class);
                                            intent.putExtra("list", mImgUrls);
                                            intent.putExtra("current", mImgUrls.indexOf(imgUrl));
                                            intent.putExtra("title", mTitleString);
                                            startActivity(intent);
                                            getActivity().overridePendingTransition(R.anim.activity_in, 0);
                                        }
                                    });
                                    RequestManager.getInstance().displayImage(imgUrl, imageView);
                                    mContainer.addView(imageView);
                                }
                            }
                            if (mImgUrls.size() > 0) {
                                mHeaderImage.setResourceIds(mImgUrls.toArray(new String[mImgUrls.size()]));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mRefreshActionItem.setRefreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        MessageUtils.toast(getActivity(), "获取数据失败");
                        if (mRefreshActionItem != null) {
                            mRefreshActionItem.setRefreshing(false);
                        }
                    }
                }
        );
    }

    private void setShareIntent(Intent shareIntent){
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.news, menu);

        MenuItem shareItem = menu.findItem(R.id.news_action_share);
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        StringBuilder shareText = new StringBuilder("《");
        shareText.append(mTitleString);
        shareText.append("》 ");
        shareText.append("http://www.cnbeta.com/articles/");
        shareText.append(mId);
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
            case R.id.news_action_website:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://m.cnbeta.com/view_" + mId + ".htm"));
                if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(intent);
                }else{
                    MessageUtils.toast(getActivity(), "没有安装浏览器");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextView getNewTextView(){
        TextView textView = (TextView) inflate(getActivity(), R.layout.view_news_text, null);
        return textView;
    }

    private SelectorImageView getNewImageView(){
        SelectorImageView imageView = new SelectorImageView(getActivity());
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(mImageLayoutParams);
        return imageView;
    }

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        mRefreshActionItem.setRefreshing(true);
        getArticleData();
    }
}
