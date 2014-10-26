package me.yugy.cnbeta.model;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.yugy.cnbeta.dao.dbinfo.AllNewsDBInfo;

/**
 * Created by yugy on 2014/8/30.
 */
public class News {

    public static final int TYPE_WITH_IMAGE = 0;
    public static final int TYPE_ONLY_TEXT = 1;

    public int sid;
    public String titleShow;
    public String homeTextShowShort;
    public String logo;
    public long time;
    public int type;
    public boolean read = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    public static News fromJson(JSONObject json) throws JSONException, ParseException {
        News news = new News();

        news.sid = json.getInt("sid");
        news.titleShow = json.getString("title_show");
        news.homeTextShowShort = json.getString("hometext_show_short").replace("\\r\\n", "");
        news.logo = json.getString("logo").replace(" ", "%20");
        String timeString = json.getString("time");
        news.time = DATE_FORMAT.parse(timeString).getTime();

        if(news.logo.contains("http://static.cnbetacdn.com/topics")){
            news.type = TYPE_ONLY_TEXT;
            news.logo = "";
        }else{
            news.type = TYPE_WITH_IMAGE;
        }

        return news;
    }

    public static News fromCursor(Cursor cursor) {
        News news = new News();
        news.sid = cursor.getInt(cursor.getColumnIndex(AllNewsDBInfo.SID));
        news.titleShow = cursor.getString(cursor.getColumnIndex(AllNewsDBInfo.TITLE_SHOW));
        news.homeTextShowShort = cursor.getString(cursor.getColumnIndex(AllNewsDBInfo.HOMETEXT_SHOW_SHORT));
        news.logo = cursor.getString(cursor.getColumnIndex(AllNewsDBInfo.LOGO));
        news.time = cursor.getLong(cursor.getColumnIndex(AllNewsDBInfo.TIME));
        news.type = cursor.getInt(cursor.getColumnIndex(AllNewsDBInfo.TYPE));
        news.read = cursor.getInt(cursor.getColumnIndex(AllNewsDBInfo.READ)) == 1;
        return news;
    }

    public static News fromRealTimeNews(RealTimeNews realTimeNews){
        News news = new News();
        news.sid = realTimeNews.sid;
        news.titleShow = realTimeNews.title;
        news.read = realTimeNews.read;
        news.time = realTimeNews.time;
        news.type = realTimeNews.type;
        news.logo = realTimeNews.logo;
        news.homeTextShowShort = realTimeNews.homeTextShow;
        return news;
    }
}
