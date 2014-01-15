package com.yugy.cnbeta.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by yugy on 14-1-8.
 */
public class NewsCommentModel {
    public String id;
    public String comment;
    public String name;
    public long time;
    public String supportCount;
    public String againstCount;

    /**
     * example : 2014-01-06 18:34:38
     */
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void parse(JSONObject jsonObject) throws JSONException, ParseException {
        id = jsonObject.getString("tid");
        comment = jsonObject.getString("comment");
        if((name = jsonObject.getString("name")).equals("")){
            name = "匿名人士";
        }
        time = sSimpleDateFormat.parse(jsonObject.getString("date")).getTime();
        supportCount = jsonObject.getString("support");
        againstCount = jsonObject.getString("against");
    }
}
