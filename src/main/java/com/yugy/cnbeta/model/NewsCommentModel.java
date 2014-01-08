package com.yugy.cnbeta.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-1-8.
 */
public class NewsCommentModel {
    public String id;
    public String comment;
    public String name;
    public String time;
    public String supportCount;
    public String againstCount;

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("tid");
        comment = jsonObject.getString("comment");
        if((name = jsonObject.getString("name")).equals("")){
            name = "匿名人士";
        }
        time = "发表于 " + jsonObject.getString("date");
        supportCount = jsonObject.getString("support");
        againstCount = jsonObject.getString("against");
    }
}
