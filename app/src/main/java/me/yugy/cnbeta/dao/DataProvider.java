package me.yugy.cnbeta.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import me.yugy.cnbeta.Application;
import me.yugy.cnbeta.dao.datahelper.AllNewsDataHelper;
import me.yugy.cnbeta.dao.datahelper.HotCommentsDataHelper;
import me.yugy.cnbeta.dao.datahelper.NewsContentDataHelper;
import me.yugy.cnbeta.dao.datahelper.RealTimeNewsDataHelper;
import me.yugy.cnbeta.dao.datahelper.RecommendNewsDataHelper;
import me.yugy.cnbeta.dao.dbinfo.AllNewsDBInfo;
import me.yugy.cnbeta.dao.dbinfo.HotCommentsDBInfo;
import me.yugy.cnbeta.dao.dbinfo.NewsContentDBInfo;
import me.yugy.cnbeta.dao.dbinfo.RealTimeNewsDBInfo;
import me.yugy.cnbeta.dao.dbinfo.RecommendNewsDBInfo;


/**
 * Created by yugy on 2014/7/3.
 */
public class DataProvider extends ContentProvider{

    public static final Object obj = new Object();
    private static final String AUTHORITY = "me.yugy.cnbeta.provider";
    private static final String SCHEME = "content://";

    private static final String PATH_ALL_NEWS = "/all_news";
    private static final String PATH_NEWS_CONTENT = "/news_content";
    private static final String PATH_HOT_COMMENTS = "/hot_comments";
    private static final String PATH_RECOMMEND_NEWS = "/recommend_news";
    private static final String PATH_REALTIME_NEWS = "/realtime_news";

    public static final Uri ALL_NEWS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_ALL_NEWS);
    public static final Uri NEWS_CONTENT_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_NEWS_CONTENT);
    public static final Uri HOT_COMMENTS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_HOT_COMMENTS);
    public static final Uri RECOMMEND_NEWS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_RECOMMEND_NEWS);
    public static final Uri REALTIME_NEWS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_REALTIME_NEWS);

    private static final int ALL_NEWS = 0;
    private static final int NEWS_CONTENT = 1;
    private static final int HOT_COMMENTS = 2;
    private static final int RECOMMEND_NEWS = 3;
    private static final int REALTIME_NEWS = 4;

    private static final String ALL_NEWS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.cnbeta.news.all";
    private static final String NEWS_CONTENT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.cnbeta.news.content";
    private static final String HOT_COMMENTS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.cnbeta.comment.hot";
    private static final String RECOMMEND_NEWS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.cnbeta.news.recommend";
    private static final String REALTIME_NEWS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.yugy.cnbeta.news.realtime";

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH){{
        addURI(AUTHORITY, "all_news", ALL_NEWS);
        addURI(AUTHORITY, "news_content", NEWS_CONTENT);
        addURI(AUTHORITY, "hot_comments", HOT_COMMENTS);
        addURI(AUTHORITY, "recommend_news", RECOMMEND_NEWS);
        addURI(AUTHORITY, "realtime_news", REALTIME_NEWS);
    }};

    private static DBHelper mDBHelper;

    public static DBHelper getDBHelper() {
        if(mDBHelper == null){
            mDBHelper = new DBHelper(Application.getContext());
        }
        return mDBHelper;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (obj){
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(matchTable(uri));

            SQLiteDatabase db = getDBHelper().getReadableDatabase();
            Cursor cursor = queryBuilder.query(db,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        }
    }

    private String matchTable(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case ALL_NEWS:
                return AllNewsDataHelper.TABLE_NAME;
            case NEWS_CONTENT:
                return NewsContentDataHelper.TABLE_NAME;
            case HOT_COMMENTS:
                return HotCommentsDataHelper.TABLE_NAME;
            case RECOMMEND_NEWS:
                return RecommendNewsDataHelper.TABLE_NAME;
            case REALTIME_NEWS:
                return RealTimeNewsDataHelper.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case ALL_NEWS:
                return ALL_NEWS_CONTENT_TYPE;
            case NEWS_CONTENT:
                return NEWS_CONTENT_CONTENT_TYPE;
            case HOT_COMMENTS:
                return HOT_COMMENTS_CONTENT_TYPE;
            case RECOMMEND_NEWS:
                return RECOMMEND_NEWS_CONTENT_TYPE;
            case REALTIME_NEWS:
                return REALTIME_NEWS_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId =db.insert(matchTable(uri), null, values);
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
            if(rowId > 0){
                Uri returnUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            db.beginTransaction();
            try {
                for(ContentValues contentValues : values){
                    db.insertWithOnConflict(matchTable(uri), BaseColumns._ID, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
                db.setTransactionSuccessful();
                getContext().getContentResolver().notifyChange(uri, null);
                return values.length;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
            throw new SQLException("Failed to insert row into "+ uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count = 0;
            db.beginTransaction();
            try {
                count = db.delete(matchTable(uri), selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (obj){
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            int count;
            db.beginTransaction();
            try {
                count = db.update(matchTable(uri), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    public static class DBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "cnbeta.db";

        private static final int DB_VERSION = 7;

        private DBHelper(Context context){
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            AllNewsDBInfo.TABLE.create(db);
            NewsContentDBInfo.TABLE.create(db);
            HotCommentsDBInfo.TABLE.create(db);
            RecommendNewsDBInfo.TABLE.create(db);
            RealTimeNewsDBInfo.TABLE.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            AllNewsDBInfo.TABLE.delete(db);
            NewsContentDBInfo.TABLE.delete(db);
            HotCommentsDBInfo.TABLE.delete(db);
            RecommendNewsDBInfo.TABLE.delete(db);
            RealTimeNewsDBInfo.TABLE.delete(db);
            onCreate(db);
        }
    }
}
