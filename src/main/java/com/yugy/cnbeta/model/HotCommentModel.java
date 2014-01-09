package com.yugy.cnbeta.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yugy on 14-1-9.
 */
public class HotCommentModel implements Parcelable{
    public String id;
    public String comment;
    public String articleId;
    public String authorName;
    public String articleTitle;
    public String commentCount;

    public HotCommentModel(){}

    public void parse(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString("HMID");
        comment = jsonObject.getString("comment");
        articleId = jsonObject.getString("ArticleID");
        if((authorName = jsonObject.getString("name")).equals("")){
            authorName = "匿名人士";
        }
        articleTitle = jsonObject.getString("title");
        commentCount = jsonObject.getString("cmtnum");
    }

    private HotCommentModel(Parcel in){
        String[] strings = new String[6];
        in.readStringArray(strings);
        id = strings[0];
        comment = strings[1];
        articleId = strings[2];
        authorName = strings[3];
        articleTitle = strings[4];
        commentCount = strings[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                id,
                comment,
                articleId,
                authorName,
                articleTitle,
                commentCount
        });
    }

    public static final Creator<HotCommentModel> CREATOR = new Creator<HotCommentModel>() {
        @Override
        public HotCommentModel createFromParcel(Parcel source) {
            return new HotCommentModel(source);
        }

        @Override
        public HotCommentModel[] newArray(int size) {
            return new HotCommentModel[size];
        }
    };
}
