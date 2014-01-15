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
    public long time;
    public String id;
    public String commentCount;
    public Spanned summary;
    public String theme;
    public boolean readed;

    /**
     * example : 2014-01-06 18:34:38
     */
    private static final SimpleDateFormat sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public NewsListModel(){}

    public void parse(JSONObject json) throws JSONException, ParseException {
        title = json.getString("title");
        time = sSimpleDateFormat.parse(json.getString("pubtime")).getTime();
        id = json.getString("ArticleID");
        commentCount = json.getString("cmtnum");
        summary = Html.fromHtml(json.getString("summary"));
        theme = json.getString("theme");
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
                id,
                commentCount,
                summary.toString(),
                theme
        });
        dest.writeBooleanArray(new boolean[]{
                readed
        });
        dest.writeLong(time);
    }

    private NewsListModel(Parcel in){
        String[] strings = new String[5];
        in.readStringArray(strings);
        title = strings[0];
        id = strings[1];
        commentCount = strings[2];
        summary = Html.fromHtml(strings[3]);
        theme = strings[4];
        boolean[] booleans = new boolean[1];
        in.readBooleanArray(booleans);
        readed = booleans[0];
        time = in.readLong();
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
