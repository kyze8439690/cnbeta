package me.yugy.cnbeta.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import me.yugy.app.common.utils.TextUtils;
import me.yugy.cnbeta.Application;
import me.yugy.cnbeta.dao.BaseDataHelper;
import me.yugy.cnbeta.dao.DataProvider;
import me.yugy.cnbeta.dao.dbinfo.AllNewsDBInfo;
import me.yugy.cnbeta.dao.dbinfo.NewsContentDBInfo;
import me.yugy.cnbeta.model.NewsContent;

/**
 * Created by yugy on 2014/9/5.
 */
public class NewsContentDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "news_content";

    public NewsContentDataHelper() {
        super(Application.getContext());
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.NEWS_CONTENT_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static ContentValues getContentValues(NewsContent newsContent){
        ContentValues values = new ContentValues();
        values.put(NewsContentDBInfo.SID, newsContent.sid);
        values.put(NewsContentDBInfo.INTRO, newsContent.intro);
        values.put(NewsContentDBInfo.STRINGS, TextUtils.convertArrayToString(newsContent.strings));
        values.put(NewsContentDBInfo.IMAGES, TextUtils.convertArrayToString(newsContent.images));
        values.put(NewsContentDBInfo.SN, newsContent.sn);
        return values;
    }

    public NewsContent select(int sid){
        Cursor cursor = query(null, NewsContentDBInfo.SID + "=?", new String[]{String.valueOf(sid)}, null);
        NewsContent newsContent = null;
        if(cursor.moveToFirst()){
            newsContent = NewsContent.fromCursor(cursor);
        }
        cursor.close();
        return newsContent;
    }

    public void update(NewsContent newsContent){
        ContentValues values = getContentValues(newsContent);
        update(values, AllNewsDBInfo.SID + "=?", new String[]{String.valueOf(newsContent.sid)});
    }

    public void insert(NewsContent newsContent){
        ContentValues values = getContentValues(newsContent);
        insert(values);
    }
}
