package com.yugy.cnbeta.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yugy on 14-1-6.
 */
public class NewsListModel implements Parcelable{
    public String title;
    public String time;
    public String id;
    public String commentCount;
    public Spanned summary;
    public String theme;
    public boolean hasHeaderPic;
    public boolean readed;

    /**
     * example : 2014-01-06 18:34:38
     */
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public NewsListModel(){}

    public void parse(JSONObject json) throws JSONException, ParseException {
        title = json.getString("title");
        time = "发表于 " + json.getString("pubtime");
        id = json.getString("ArticleID");
        commentCount = json.getString("cmtnum");
        summary = Html.fromHtml(json.getString("summary"));
        theme = json.getString("theme");
        hasHeaderPic = !json.getString("theme").equals(json.getString("topicLogo"));
        readed = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                title,
                time,
                id,
                commentCount,
                summary.toString(),
                theme
        });
        dest.writeBooleanArray(new boolean[]{
                hasHeaderPic,
                readed
        });
    }

    private NewsListModel(Parcel in){
        String[] strings = new String[6];
        in.readStringArray(strings);
        title = strings[0];
        time = strings[1];
        id = strings[2];
        commentCount = strings[3];
        summary = Html.fromHtml(strings[4]);
        theme = strings[5];
        boolean[] booleans = new boolean[2];
        in.readBooleanArray(booleans);
        hasHeaderPic = booleans[0];
        readed = booleans[1];
    }

    public static final Creator<NewsListModel> CREATOR = new Creator<NewsListModel>() {
        @Override
        public NewsListModel createFromParcel(Parcel source) {
            return new NewsListModel(source);
        }

        @Override
        public NewsListModel[] newArray(int size) {
            return new NewsListModel[size];
        }
    };
}
