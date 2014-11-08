package me.yugy.cnbeta.model;

import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.yugy.cnbeta.dao.dbinfo.RealTimeNewsDBInfo;

/**
 * Created by yugy on 14/10/24.
 */
public class RealTimeNews {

    public static final int TYPE_WITH_IMAGE = 0;
    public static final int TYPE_ONLY_TEXT = 1;

    public int sid;
    public String title;
    public String homeTextShow;
    public String logo;
    public long time;
    public int type;
    public boolean read = false;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    public static RealTimeNews fromJson(JSONObject json) throws JSONException, ParseException {
        RealTimeNews realTimeNews = new RealTimeNews();

        realTimeNews.sid = json.getInt("sid");
        realTimeNews.title = json.getString("subject");
        realTimeNews.homeTextShow = json.getString("hometext_show").replace("\\r\\n", "");
        realTimeNews.logo = json.getString("logo").replace(" ", "%20");
        String timeString = json.getString("time");
        realTimeNews.time = DATE_FORMAT.parse(timeString).getTime();

        if(realTimeNews.logo.contains("http://static.cnbetacdn.com/topics")){
            realTimeNews.type = TYPE_ONLY_TEXT;
            realTimeNews.logo = "";
        }else{
            realTimeNews.type = TYPE_WITH_IMAGE;
        }

        return realTimeNews;
    }

    public static RealTimeNews fromCursor(Cursor cursor) {
        RealTimeNews news = new RealTimeNews();
        news.sid = cursor.getInt(cursor.getColumnIndex(RealTimeNewsDBInfo.SID));
        news.title = cursor.getString(cursor.getColumnIndex(RealTimeNewsDBInfo.TITLE));
        news.homeTextShow = cursor.getString(cursor.getColumnIndex(RealTimeNewsDBInfo.HOMETEXT_SHOW));
        news.logo = cursor.getString(cursor.getColumnIndex(RealTimeNewsDBInfo.LOGO));
        news.time = cursor.getLong(cursor.getColumnIndex(RealTimeNewsDBInfo.TIME));
        news.type = cursor.getInt(cursor.getColumnIndex(RealTimeNewsDBInfo.TYPE));
        news.read = cursor.getInt(cursor.getColumnIndex(RealTimeNewsDBInfo.READ)) == 1;
        return news;
    }
}
