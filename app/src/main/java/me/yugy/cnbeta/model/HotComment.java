package me.yugy.cnbeta.model;

import android.database.Cursor;
import me.yugy.cnbeta.dao.dbinfo.HotCommentsDBInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by yugy on 2014/9/7.
 */
public class HotComment {

    public int sid;
    public String comment;
    public String author;
    public String title;
    public long time;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    public static HotComment fromJson(JSONObject json) throws JSONException, ParseException {
        HotComment hotComment = new HotComment();
        hotComment.sid = json.getJSONObject("comment_show").getInt("sid");
        hotComment.comment = json.getJSONObject("comment_show").getString("comment");
        hotComment.author = json.getJSONObject("comment_show").getString("name");
        if(hotComment.author.equals("")){
            hotComment.author = "匿名人士";
        }
        hotComment.title = json.getJSONObject("comment_show").getString("title_show");
        String timeString = json.getString("time");
        hotComment.time = DATE_FORMAT.parse(timeString).getTime();
        return hotComment;
    }

    public static HotComment fromCursor(Cursor cursor) {
        HotComment hotComment = new HotComment();
        hotComment.sid = cursor.getInt(cursor.getColumnIndex(HotCommentsDBInfo.SID));
        hotComment.comment = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.COMMENT));
        hotComment.author = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.AUTHOR));
        hotComment.title = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.TITLE));
        hotComment.time = cursor.getLong(cursor.getColumnIndex(HotCommentsDBInfo.TIME));
        return hotComment;
    }
}
