package me.yugy.cnbeta.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.yugy.cnbeta.model.Comment;
import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.model.HotComment;
import me.yugy.cnbeta.model.News;
import me.yugy.cnbeta.model.NewsContent;
import me.yugy.cnbeta.model.RealTimeNews;

/**
 * Created by yugy on 2014/8/30.
 */
public class CnBeta {

    private static final String LOG_TAG = CnBeta.class.getName();

    private static void log(String log){
        Log.d(LOG_TAG, log);
    }

    public static final String TYPE_ALL = "Article.Lists";
    public static final String TYPE_HOT_COMMENT = "Article.RecommendComment";
    public static final String TYPE_RECOMMEND = "Article.TodayRank";
    public static final String TYPE_NEWS_CONTENT = "Article.NewsContent";
    public static final String TYPE_NEWS_COMMENT = "Article.Comment";
    public static final String TYPE_REALTIME = "realtime";

    private static final String API_URL = "http://api.cnbeta.com/capi?";

    public static void getNews(Context context, final String type, final int endSid, Response.Listener<News[]> listener
            , Response.ErrorListener errorListener){
        String params = "app_key=10000";
        if(endSid != 0) {
            params += "&end_id=" + endSid;
        }
        params += "&format=json&method=" + type + "&timestamp=" + System.currentTimeMillis();
        params += "&v=1.0";
        params += "&sign=" + getSign(params + "&mpuffgvbvbttn3Rc");

        String url = API_URL + params;
        NewsRequest request = new NewsRequest(url,
                listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getRealTimeNews(Context context, Response.Listener<RealTimeNews[]> listener
                    , Response.ErrorListener errorListener){
        String url = API_URL + "?jsoncallback=jQuery18008753548712314047_" + System.currentTimeMillis()
                + "s&type=" + TYPE_REALTIME + "&&_=" + (System.currentTimeMillis() + 1L);
        RealTimeNewsRequest request = new RealTimeNewsRequest(url, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getHotComments(Context context, Response.Listener<HotComment[]> listener
            , Response.ErrorListener errorListener){
        String params = "app_key=10000&format=json&method=" + TYPE_HOT_COMMENT
                + "&timestamp=" + System.currentTimeMillis();
        params += "&v=1.0";
        params += "&sign=" + getSign(params + "&mpuffgvbvbttn3Rc");

        String url = API_URL + params;
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

    public static void getNewsContent2(Context context, int sid, Response.Listener<NewsContent> listener
            , Response.ErrorListener errorListener){
        String params = "app_key=10000&format=json&method=" + TYPE_NEWS_CONTENT
                + "&sid=" + sid + "&timestamp=" + System.currentTimeMillis();
        params += "&v=1.0";
        params += "&sign=" + getSign(params + "&mpuffgvbvbttn3Rc");

        String url = API_URL + params;
        NewsContentRequest2 request = new NewsContentRequest2(url, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    public static void getNewsComments(Context context, int sid, int page, Response.Listener<Comment[]> listener
            , Response.ErrorListener errorListener){
        String params = "app_key=10000&format=json&method=" + TYPE_NEWS_COMMENT
                + "&page=" + page + "&sid=" + sid + "&timestamp=" + System.currentTimeMillis();
        params += "&v=1.0";
        params += "&sign=" + getSign(params + "&mpuffgvbvbttn3Rc");

        String url = API_URL + params;
        NewsCommentRequest request = new NewsCommentRequest(url, listener, errorListener);
        request.setTag(context);
        Volley.getInstance().add(request);
    }

    private static final char[] a = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    public static String getSign(String paramString)
    {
        try
        {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramString.getBytes());
            byte[] arrayOfByte = localMessageDigest.digest();
            StringBuilder localStringBuilder = new StringBuilder(2 * arrayOfByte.length);
            for (int i = 0; ; i++)
            {
                if (i >= arrayOfByte.length)
                    return localStringBuilder.toString().toLowerCase();
                localStringBuilder.append(a[((0xF0 & arrayOfByte[i]) >>> 4)]);
                localStringBuilder.append(a[(0xF & arrayOfByte[i])]);
            }
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return "";
    }

}
