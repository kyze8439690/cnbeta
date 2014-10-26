package me.yugy.cnbeta.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.yugy.cnbeta.model.NewsContent;

/**
 * Created by yugy on 2014/8/31.
 */
public class NewsContentRequest extends Request<NewsContent>{

    private static final String API_NEWS_CONTENT = "http://www.cnbeta.com/articles/";

    private Response.Listener<NewsContent> mListener;
    private int mSid;

    public NewsContentRequest(int sid, Response.Listener<NewsContent> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, API_NEWS_CONTENT + sid + ".htm", errorListener);
        mSid = sid;
        mListener = listener;
    }

    @Override
    protected Response<NewsContent> parseNetworkResponse(NetworkResponse response) {
        String html = new String(response.data, Charset.forName("UTF-8"));
        Document document = Jsoup.parse(html);
        Elements elements = document.select("section.article_content");
        if(elements.size() == 0){
            return Response.error(new ParseError(new Exception("Can not find 'section.article_content'")));
        }else{
            NewsContent newsContent = new NewsContent();
            newsContent.sid = mSid;
            Element intro = elements.select("div.introduction").first();
            if(intro == null){
                return Response.error(new ParseError(new Exception("Can not find 'div.introduction")));
            }else{
                intro.select("div").remove();
                newsContent.intro = intro.html();
            }
            Element content = elements.select("div.content").first();
            if(content == null){
                return Response.error(new ParseError(new Exception("Can not find 'div.content'")));
            }else{
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
                Pattern snPattern = Pattern.compile("SN:\"([^\"]+)\"");
                Matcher snMatcher = snPattern.matcher(html);
                if(!snMatcher.find()){
                    return Response.error(new ParseError(new Exception("Can not find SN")));
                }else{
                    newsContent.sn = snMatcher.group(1);
                }
                return Response.success(newsContent, HttpHeaderParser.parseCacheHeaders(response));
            }
        }
    }

    @Override
    protected void deliverResponse(NewsContent response) {
        mListener.onResponse(response);
    }
}
