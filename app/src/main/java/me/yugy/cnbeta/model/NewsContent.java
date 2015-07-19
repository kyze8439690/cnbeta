package me.yugy.cnbeta.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import me.yugy.app.common.utils.TextUtils;
import me.yugy.cnbeta.dao.dbinfo.NewsContentDBInfo;

/**
 * Created by yugy on 2014/8/31.
 */
public class NewsContent implements Parcelable {

    public int sid;
    public String intro;
    public String[] strings;
    public String[] images;
    public String sn;

    public static NewsContent fromCursor(Cursor cursor) {
        NewsContent newsContent = new NewsContent();
        newsContent.sid = cursor.getInt(cursor.getColumnIndex(NewsContentDBInfo.SID));
        newsContent.intro = cursor.getString(cursor.getColumnIndex(NewsContentDBInfo.INTRO));
        newsContent.strings = TextUtils.convertStringToArray(
                cursor.getString(cursor.getColumnIndex(NewsContentDBInfo.STRINGS)));
        newsContent.images = TextUtils.convertStringToArray(
                cursor.getString(cursor.getColumnIndex(NewsContentDBInfo.IMAGES)));
        newsContent.sn = cursor.getString(cursor.getColumnIndex(NewsContentDBInfo.SN));
        return newsContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sid);
        dest.writeString(this.intro);
        dest.writeStringArray(this.strings);
        dest.writeStringArray(this.images);
        dest.writeString(this.sn);
    }

    public NewsContent() {
    }

    private NewsContent(Parcel in) {
        this.sid = in.readInt();
        this.intro = in.readString();
        this.strings = in.createStringArray();
        this.images = in.createStringArray();
        this.sn = in.readString();
    }

    public static final Parcelable.Creator<NewsContent> CREATOR = new Parcelable.Creator<NewsContent>() {
        public NewsContent createFromParcel(Parcel source) {
            return new NewsContent(source);
        }

        public NewsContent[] newArray(int size) {
            return new NewsContent[size];
        }
    };
}
