package me.yugy.cnbeta.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yugy.cnbeta.model.NewsContent;

/**
 * Created by yugy on 2014/8/31.
 */
public class NewsContentRequest2 extends Request<NewsContent>{

    private Response.Listener<NewsContent> mListener;
    private int mSid;

    public NewsContentRequest2(String url, Response.Listener<NewsContent> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<NewsContent> parseNetworkResponse(NetworkResponse response) {
        String string = new String(response.data, Charset.forName("UTF-8"));
        try {
            JSONObject json = new JSONObject(string);
            NewsContent newsContent = new NewsContent();
            if(json.getString("status").equals("success")){
                JSONObject result = json.getJSONObject("result");
                newsContent.sid = result.getInt("sid");
                newsContent.intro = result.getString("hometext");
                Element content = Jsoup.parse(result.getString("bodytext"));
                //划分文字
                final String[] textPieces = content.html().split("<img[^>]+>");
                //选出所有图片
                Elements imgElements = content.select("img");
                final String[] imgPieces = new String[imgElements.size()];
                for(int i = 0; i < imgPieces.length; i++){
                    imgPieces[i] = imgElements.get(i).absUrl("src");
                }
                newsContent.strings = textPieces;
                newsContent.images = imgPieces;
                return Response.success(newsContent, HttpHeaderParser.parseCacheHeaders(response));
            }else{
                return Response.error(new ParseError(new Exception("Call api failed.")));
            }
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(NewsContent response) {
        mListener.onResponse(response);
    }
}
