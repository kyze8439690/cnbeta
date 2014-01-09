package com.yugy.cnbeta.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-1-9.
 */
public class TopTenNewsModel implements Parcelable{
    public String rank;
    public String id;
    public String readCount;
    public String title;
    public String commentCount;

    public TopTenNewsModel(){}

    public void parse(int rank, JSONObject jsonObject) throws JSONException {
        this.rank = String.valueOf(rank);
        id = jsonObject.getString("ArticleID");
        readCount = jsonObject.getString("counter");
        title = jsonObject.getString("title");
        commentCount = jsonObject.getString("cmtnum");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private TopTenNewsModel(Parcel in){
        String[] strings = new String[5];
        in.readStringArray(strings);
        rank = strings[0];
        id = strings[1];
        readCount = strings[2];
        title = strings[3];
        commentCount = strings[4];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                rank,
                id,
                readCount,
                title,
                commentCount
        });
    }

    public static final Creator<TopTenNewsModel> CREATOR = new Creator<TopTenNewsModel>() {
        @Override
        public TopTenNewsModel createFromParcel(Parcel source) {
            return new TopTenNewsModel(source);
        }

        @Override
        public TopTenNewsModel[] newArray(int size) {
            return new TopTenNewsModel[size];
        }
    };
}
