package me.yugy.cnbeta.vendor;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.model.HotComment;
import me.yugy.cnbeta.model.News;
import me.yugy.cnbeta.model.NewsContent;
import me.yugy.cnbeta.model.RealTimeNews;
import me.yugy.cnbeta.network.HotCommentRequest;
import me.yugy.cnbeta.network.NewsCommentRequest;
import me.yugy.cnbeta.network.NewsContentRequest;
import me.yugy.cnbeta.network.NewsRequest;
import me.yugy.cnbeta.network.RealTimeNewsRequest;
import me.yugy.cnbeta.network.Volley;

/**
 * Created by yugy on 2014/8/30.
 */
public class CnBeta {

    private static final String LOG_TAG = CnBeta.class.getName();

    private static void log(String log){
        Log.d(LOG_TAG, log);
    }

    public static final String TYPE_ALL = "all";
    public static final String TYPE_HOT_COMMENT = "jhcomment";
    public static final String TYPE_RECOMMEND = "dig";
    public static final String TYPE_REALTIME = "realtime";

    private static final String API_NEWS_LIST = "http://www.cnbeta.com/more.htm";

    public static void getNews(Context context, final String type, final int page, Response.Listener<News[]> listener
            , Response.ErrorListener errorListener){
        String url = API_NEWS_LIST + "?jsoncallback=" + randomJQueryCallback()
                + "&type=" + type + "&page=" + page + "&_=" + (System.currentTimeMillis() + 1L);
        NewsRequest request = new NewsRequest(url,
                listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getRealTimeNews(Context context, Response.Listener<RealTimeNews[]> listener
                    , Response.ErrorListener errorListener){
        String url = API_NEWS_LIST + "?jsoncallback=jQuery18008753548712314047_" + System.currentTimeMillis()
                + "s&type=" + TYPE_REALTIME + "&&_=" + (System.currentTimeMillis() + 1L);
        RealTimeNewsRequest request = new RealTimeNewsRequest(url, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getHotComments(Context context, final int page, Response.Listener<HotComment[]> listener
            , Response.ErrorListener errorListener){
        String url = API_NEWS_LIST + "?jsoncallback=" + randomJQueryCallback()
                + "&type=" + TYPE_HOT_COMMENT + "&page=" + page + "&_=" + (System.currentTimeMillis() + 1L);
        HotCommentRequest request = new HotCommentRequest(url, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getNewsContent(Context context, int sid, Response.Listener<NewsContent> listener
            , Response.ErrorListener errorListener){
        NewsContentRequest request = new NewsContentRequest(sid,
                listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getNewsComments(Context context, int sid, int page, String sn, Response.Listener<Comments> listener
            , Response.ErrorListener errorListener){
        NewsCommentRequest request = new NewsCommentRequest(sid, page, sn, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    private static String randomJQueryCallback()
    {
        StringBuilder localStringBuilder = new StringBuilder("jQuery1800");
        for (int i = 0; i < "8753548712314047".length(); i++)
            localStringBuilder.append("0123456789".charAt((int)(Math.random() * "0123456789".length())));
        localStringBuilder.append("_");
        localStringBuilder.append(Math.round(System.currentTimeMillis() / 15000.0D));
        return localStringBuilder.toString();
    }

}
