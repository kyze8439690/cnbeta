package me.yugy.cnbeta.dao.datahelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import me.yugy.cnbeta.Application;
import me.yugy.cnbeta.dao.BaseDataHelper;
import me.yugy.cnbeta.dao.DataProvider;
import me.yugy.cnbeta.dao.dbinfo.HotCommentsDBInfo;
import me.yugy.cnbeta.model.HotComment;

/**
 * Created by yugy on 2014/9/8.
 */
public class HotCommentsDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "hot_comments";

    public HotCommentsDataHelper() {
        super(Application.getContext());
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.HOT_COMMENTS_CONTENT_URI;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    public static ContentValues getContentValues(HotComment hotComment){
        ContentValues values = new ContentValues();
        values.put(HotCommentsDBInfo.SID, hotComment.sid);
        values.put(HotCommentsDBInfo.COMMENT, hotComment.comment);
        values.put(HotCommentsDBInfo.AUTHOR, hotComment.author);
        values.put(HotCommentsDBInfo.TITLE, hotComment.title);
        values.put(HotCommentsDBInfo.TIME, hotComment.time);
        return values;
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

    public HotComment select(int sid){
        Cursor cursor = query(null, HotCommentsDBInfo.SID + "=?", new String[]{String.valueOf(sid)}, null);
        HotComment hotComment = null;
        if(cursor.moveToFirst()){
            hotComment = HotComment.fromCursor(cursor);
        }
        cursor.close();
        return hotComment;
    }

    public void update(HotComment hotComment){
        ContentValues values = getContentValues(hotComment);
        update(values, HotCommentsDBInfo.SID + "=?", new String[]{String.valueOf(hotComment.sid)});
    }

    public void insert(HotComment hotComment){
        ContentValues values = getContentValues(hotComment);
        insert(values);
    }

    public int bulkInsert(HotComment[] hotComments){
        synchronized (DataProvider.obj){
            int insertCount = 0;
            SQLiteDatabase db = DataProvider.getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try{
                for(HotComment hotComment : hotComments){
                    if(select(hotComment.sid) == null) {
                        ContentValues values = getContentValues(hotComment);
                        db.insert(getTableName(), null, values);
                        insertCount++;
                    }else{
                        ContentValues values = getContentValues(hotComment);
                        db.update(getTableName(), values, HotCommentsDBInfo.SID + "=?", new String[]{String.valueOf(hotComment.sid)});
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
