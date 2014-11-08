package me.yugy.cnbeta.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.dao.datahelper.AllNewsDataHelper;
import me.yugy.cnbeta.dao.datahelper.HotCommentsDataHelper;
import me.yugy.cnbeta.dao.datahelper.NewsContentDataHelper;
import me.yugy.cnbeta.dao.datahelper.RealTimeNewsDataHelper;
import me.yugy.cnbeta.dao.datahelper.RecommendNewsDataHelper;
import me.yugy.cnbeta.model.HotComment;
import me.yugy.cnbeta.model.News;
import me.yugy.cnbeta.model.NewsContent;
import me.yugy.cnbeta.model.RealTimeNews;
import me.yugy.cnbeta.utils.UIUtils;
import me.yugy.cnbeta.network.CnBeta;
import me.yugy.cnbeta.widget.AlphaForegroundColorSpan;
import me.yugy.cnbeta.widget.CircularProgressBar;
import me.yugy.cnbeta.widget.FloatingActionButton;
import me.yugy.cnbeta.widget.NotifyScrollView;
import me.yugy.cnbeta.widget.RelativeTimeTextView;
import me.yugy.cnbeta.widget.SelectorImageView;

/**
 * Created by yugy on 2014/8/31.
 */
public class ArticleFragment extends Fragment {

    private static final String LOG_TAG = ArticleFragment.class.getName();
    private News mNews;
    private NewsContent mNewsContent;

    @InjectView(R.id.scroll_view) NotifyScrollView mScrollView;
    @InjectView(R.id.title) TextView mTitle;
    @InjectView(R.id.time) RelativeTimeTextView mTime;
    @InjectView(R.id.desc) TextView mDesc;
    @InjectView(R.id.article_container) LinearLayout mContainer;
    @InjectView(R.id.floating_button) FloatingActionButton mFloatingActionButton;
    @InjectView(R.id.article_title_placeholder) View mTitlePlaceHolder;
    private CircularProgressBar mLoadingProgressBar;

    private boolean mLoadFromDatabase = true;
    private int mTriggerDistance;

    public static final int NEWS_TYPE_ALL_NEWS = 0;
    public static final int NEWS_TYPE_HOT_COMMENT = 1;
    public static final int NEWS_TYPE_RECOMMEND = 2;
    public static final int NEWS_TYPE_REALTIME = 3;

    private AlphaForegroundColorSpan mAlphaActionBarTitleColorSpan;
    private AlphaForegroundColorSpan mAlphaTitleColorSpan;
    private SpannableString mActionBarTitleSpannable;
    private SpannableString mTitleSpannable;

    public static ArticleFragment newInstance(int sid, int type){
        ArticleFragment fragment = new ArticleFragment();
        Bundle arg = new Bundle();
        arg.putInt("sid", sid);
        arg.putInt("type", type);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int sid = getArguments().getInt("sid");
        int type = getArguments().getInt("type");
        switch (type){
            case NEWS_TYPE_ALL_NEWS:
                mNews = new AllNewsDataHelper().select(sid);
                if(mNews != null) {
                    new AllNewsDataHelper().setAsRead(sid);
                }
                break;
            case NEWS_TYPE_HOT_COMMENT:
                HotComment hotComment = new HotCommentsDataHelper().select(sid);
                if(hotComment != null){
                    mNews = new News();
                    mNews.sid = sid;
                    mNews.title = hotComment.subject;
                }
                break;
            case NEWS_TYPE_RECOMMEND:
                mNews = new RecommendNewsDataHelper().select(sid);
                if(mNews != null) {
                    new RecommendNewsDataHelper().setAsRead(sid);
                }
                break;
            case NEWS_TYPE_REALTIME:
                RealTimeNews news = new RealTimeNewsDataHelper().select(sid);
                if(news != null){
                    new RealTimeNewsDataHelper().setAsRead(sid);
                    mNews = News.fromRealTimeNews(news);
                }
                break;
        }

        mActionBarTitleSpannable = new SpannableString(mNews.title);
        mAlphaActionBarTitleColorSpan = new AlphaForegroundColorSpan(Color.WHITE);
        mTitleSpannable = new SpannableString(mNews.title);
        mAlphaTitleColorSpan = new AlphaForegroundColorSpan(Color.WHITE);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int titleHeight = mTitle.getHeight();

                mTitlePlaceHolder.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        titleHeight
                ));

                int actionbarHeight = ((ActionBarActivity)getActivity()).getSupportActionBar().getHeight();

                mTriggerDistance = titleHeight - actionbarHeight;

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    //noinspection deprecation
                    mTitle.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }else{
                    mTitle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mScrollView.setOnScrollListener(new NotifyScrollView.OnScrollListener() {
            @Override
            public void onScroll(int direction, int l, int t, int oldl, int oldt) {
                if(direction == DIRECTION_UP){
                    mFloatingActionButton.hide();
                }else if(direction == DIRECTION_DOWN){
                    mFloatingActionButton.show();
                }
                applyActionBarChange(t);
            }
        });

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                applyActionBarChange(mScrollView.getScrollY());
            }
        });

        if(mNews != null){
            mTitle.setText(mTitleSpannable);
            if(mNews.time != 0) {
                mTime.setReferenceTime(mNews.time);
            }else{
                mTime.setText("");
            }
            if(mNews.summary != null) {
                mDesc.setText(mNews.summary);
            }
        }
        mNewsContent = new NewsContentDataHelper().select(mNews.sid);
        if(mNewsContent == null) {
            mLoadFromDatabase = false;
            fetchData();
        }else{
            mLoadFromDatabase = true;
            parseData(mNewsContent);
        }
    }

    private void applyActionBarChange(int top){
        if(mTriggerDistance != 0){
            if(mTriggerDistance >= top){
                ViewHelper.setTranslationY(mTitle, -top);
                float alpha = (float) top / mTriggerDistance;
                setActionBarTitleAlpha(alpha);
                setTitleAlpha(1f - alpha);
            }else{
                ViewHelper.setTranslationY(mTitle, -mTriggerDistance);
                if(mAlphaActionBarTitleColorSpan.getAlpha() != 1f) {
                    setActionBarTitleAlpha(1f);
                }
                if(mAlphaTitleColorSpan.getAlpha() != 0f){
                    setTitleAlpha(0f);
                }
            }
        }
    }

    private void setActionBarTitleAlpha(float alpha){
        mAlphaActionBarTitleColorSpan.setAlpha(alpha);
        mActionBarTitleSpannable.setSpan(mAlphaActionBarTitleColorSpan, 0, mActionBarTitleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(mActionBarTitleSpannable);
    }

    private void setTitleAlpha(float alpha){
        mAlphaTitleColorSpan.setAlpha(alpha);
        mTitleSpannable.setSpan(mAlphaTitleColorSpan, 0, mTitleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTitle.setText(mTitleSpannable);
    }

    @OnClick(R.id.floating_button)
    void onFloatingButtonClick(){
        if(mNews != null && mNewsContent != null) {
            CommentsActivity.launch(getActivity(), mNews.sid);
        }
    }

    private void fetchData() {
        CnBeta.getNewsContent2(getActivity(), mNews.sid, new Response.Listener<NewsContent>() {
            @Override
            public void onResponse(final NewsContent response) {
                Log.d(LOG_TAG, "get news content");
                mLoadFromDatabase = true;
                mNewsContent = response;
                new NewsContentDataHelper().insert(response);
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getCause() instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
                if (mLoadingProgressBar != null) {
                    mLoadingProgressBar.progressiveStop();
                }
            }
        });
    }

    private void parseData(final NewsContent response){
        if(mLoadingProgressBar != null){
            mLoadingProgressBar.progressiveStop();
        }
        mDesc.setText(Html.fromHtml(response.intro));
        if(mContainer.getChildCount() > 3){
            mContainer.removeViews(3, mContainer.getChildCount() - 3);
        }
        if(response.strings.length > response.images.length) {
            int count = Math.max(response.strings.length, response.images.length);
            for (int i = 0; i < count; i++) {
                //add image
                if (i < response.images.length) {
                    final String imageUrl = response.images[i];
                    final SelectorImageView imageView = getNewImageView();
                    mContainer.addView(imageView);
                    ImageLoader.getInstance().displayImage(imageUrl, imageView, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int[] screenLocation = new int[2];
                                    v.getLocationOnScreen(screenLocation);
                                    ImageActivity.ImageDetail imageDetail = new ImageActivity.ImageDetail();
                                    imageDetail.imageUrl = imageUrl;
                                    imageDetail.left = screenLocation[0];
                                    imageDetail.top = screenLocation[1];
                                    imageDetail.width = v.getWidth();
                                    imageDetail.height = v.getHeight();
                                    imageDetail.title = mNews.title;
                                    imageDetail.newsUrl = "http://www.cnbeta.com/articles/" + mNews.sid + ".htm";
                                    ImageActivity.launch(v.getContext(), imageDetail);
                                }
                            });
                        }
                    });
                }

                //add text
                if (i < response.strings.length) {
                    String textString = response.strings[i];
                    TextView textView = getNewTextView();
                    mContainer.addView(textView);
                    textView.setText(Html.fromHtml(textString));
                }
            }
        }else{
            int count = Math.max(response.strings.length, response.images.length);
            for (int i = 0; i < count; i++) {
                //add text
                if (i < response.strings.length) {
                    String textString = response.strings[i];
                    TextView textView = getNewTextView();
                    mContainer.addView(textView);
                    textView.setText(Html.fromHtml(textString));
                }

                //add image
                if (i < response.images.length) {
                    final String imageUrl = response.images[i];
                    final SelectorImageView imageView = getNewImageView();
                    mContainer.addView(imageView);
                    ImageLoader.getInstance().displayImage(imageUrl, imageView, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int[] screenLocation = new int[2];
                                    v.getLocationOnScreen(screenLocation);
                                    ImageActivity.ImageDetail imageDetail = new ImageActivity.ImageDetail();
                                    imageDetail.imageUrl = imageUrl;
                                    imageDetail.left = screenLocation[0];
                                    imageDetail.top = screenLocation[1];
                                    imageDetail.width = v.getWidth();
                                    imageDetail.height = v.getHeight();
                                    imageDetail.title = mNews.title;
                                    imageDetail.newsUrl = "http://www.cnbeta.com/articles/" + mNews.sid + ".htm";
                                    ImageActivity.launch(v.getContext(), imageDetail);
                                }
                            });
                        }
                    });
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article, menu);
        if(!mLoadFromDatabase) {
            MenuItem item = menu.findItem(R.id.loading_progress);
            View actionView = LayoutInflater.from(getActivity()).inflate(R.layout.view_article_loading_progress, null);
            mLoadingProgressBar = (CircularProgressBar) actionView.findViewById(R.id.progress);
            int size = UIUtils.dp2px(getActivity(), 48);
            actionView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            MenuItemCompat.setActionView(item, actionView);
            mLoadingProgressBar.setIndeterminate(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, mNews.title + " " + "http://www.cnbeta.com/articles/" + mNews.sid + ".htm");
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
                return true;
            case R.id.action_view_in_browser:
                String url = "http://www.cnbeta.com/articles/" + mNews.sid + ".htm";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextView getNewTextView(){
        return (TextView) LayoutInflater.from(getActivity()).inflate(
                R.layout.view_article_text_piece, mContainer, false);
    }

    private SelectorImageView getNewImageView(){
        return (SelectorImageView) LayoutInflater.from(getActivity()).inflate(
                R.layout.view_article_image_piece, mContainer, false);
    }
}
