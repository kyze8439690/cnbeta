package me.yugy.cnbeta.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by yugy on 2014/9/6.
 */
public class Comment {

    public String author;
    public long time;
    public String content;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA);

    public static Comment fromJson(JSONObject json) throws JSONException, ParseException {
        Comment comment = new Comment();
        comment.author = json.getString("username");
        if(comment.author.equals("")){
            comment.author = "匿名人士";
        }
        String timeString = json.getString("created_time");
        comment.time = DATE_FORMAT.parse(timeString).getTime();
        comment.content = json.getString("content");
        return comment;
    }

}
