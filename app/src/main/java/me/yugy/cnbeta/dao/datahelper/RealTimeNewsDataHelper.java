package me.yugy.cnbeta.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import me.yugy.cnbeta.Application;
import me.yugy.cnbeta.dao.BaseDataHelper;
import me.yugy.cnbeta.dao.DataProvider;
import me.yugy.cnbeta.dao.dbinfo.RealTimeNewsDBInfo;
import me.yugy.cnbeta.model.RealTimeNews;

/**
 * Created by yugy on 2014/7/3.
 */
public class RealTimeNewsDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "realtime_news";

    public RealTimeNewsDataHelper() {
        super(Application.getContext());
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.REALTIME_NEWS_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static ContentValues getContentValues(RealTimeNews news){
        ContentValues values = new ContentValues();
        values.put(RealTimeNewsDBInfo.SID, news.sid);
        values.put(RealTimeNewsDBInfo.TITLE, news.title);
        values.put(RealTimeNewsDBInfo.HOMETEXT_SHOW, news.homeTextShow);
        values.put(RealTimeNewsDBInfo.LOGO, news.logo);
        values.put(RealTimeNewsDBInfo.TIME, news.time);
        values.put(RealTimeNewsDBInfo.TYPE, news.type);
        values.put(RealTimeNewsDBInfo.READ, news.read ? 1 : 0);
        return values;
    }

    public RealTimeNews select(int sid){
        Cursor cursor = query(null, RealTimeNewsDBInfo.SID + "=?", new String[]{String.valueOf(sid)}, null);
        RealTimeNews news = null;
        if(cursor.moveToFirst()){
            news = RealTimeNews.fromCursor(cursor);
        }
        cursor.close();
        return news;
    }

    public int getCount(){
        synchronized (DataProvider.obj){
            SQLiteDatabase db = DataProvider.getDBHelper().getReadableDatabase();
            Cursor cursor = db.query(getTableName(), new String[] {"count(*)"}, null, null, null, null, null);
            int count;
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }else{
                count = 0;
            }
            cursor.close();
            return count;
        }
    }

    public List<RealTimeNews> getAllNews(){
        List<RealTimeNews> newses = new ArrayList<RealTimeNews>();
        Cursor cursor = query(null, null, null, null);
        while (cursor != null && cursor.moveToNext()){
            RealTimeNews news = RealTimeNews.fromCursor(cursor);
            newses.add(news);
        }
        cursor.close();
        return newses;
    }

    public void update(RealTimeNews news){
        ContentValues values = getContentValues(news);
        update(values, RealTimeNewsDBInfo.SID + "=?", new String[]{String.valueOf(news.sid)});
    }

    public void setAsRead(int sid){
        RealTimeNews news = select(sid);
        if(news != null && !news.read){
            news.read = true;
            update(news);
        }
    }

//    public int bulkInsert(List<News> newses){
//        int size = newses.size();
//        ContentValues values[] = new ContentValues[size];
//        for (int i = 0; i < size; i++) {
//            values[i] = getContentValues(newses.get(i));
//        }
//        return bulkInsert(values);
//    }

    public int bulkInsert(RealTimeNews[] newses){
        synchronized (DataProvider.obj){
            int insertCount = 0;
            SQLiteDatabase db = DataProvider.getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try{
                for(RealTimeNews news : newses){
                    RealTimeNews oldNews;
                    if((oldNews = select(news.sid)) == null) {
                        ContentValues values = getContentValues(news);
                        db.insert(getTableName(), null, values);
                        insertCount++;
                    }else{
                        news.read = oldNews.read;
                        ContentValues values = getContentValues(news);
                        db.update(getTableName(), values, RealTimeNewsDBInfo.SID + "=?", new String[]{String.valueOf(news.sid)});
                    }
                }
                db.setTransactionSuccessful();
                if(insertCount > 0) {
                    getContext().getContentResolver().notifyChange(getContentUri(), null);
                }
                db.endTransaction();
                return insertCount;
            }catch (Exception e){
                e.printStackTrace();
                db.endTransaction();
            }
            throw new SQLException("Fail to insert row into " + getContentUri());
        }
    }
}
