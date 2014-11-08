package me.yugy.cnbeta.model;

import android.database.Cursor;
import me.yugy.cnbeta.dao.dbinfo.HotCommentsDBInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by yugy on 2014/9/7.
 */
public class HotComment {

    public int cid;
    public int sid;
    public String comment;
    public String username;
    public String subject;

    public static HotComment fromJson(JSONObject json) throws JSONException, ParseException {
        HotComment hotComment = new HotComment();
        hotComment.cid = json.getInt("cid");
        hotComment.sid = json.getInt("sid");
        hotComment.comment = json.getString("comment");
        hotComment.username = json.getString("username");
        if(hotComment.username.equals("")){
            hotComment.username = "匿名人士";
        }
        hotComment.subject = json.getString("subject");
        return hotComment;
    }

    public static HotComment fromCursor(Cursor cursor) {
        HotComment hotComment = new HotComment();
        hotComment.cid = cursor.getInt(cursor.getColumnIndex(HotCommentsDBInfo.CID));
        hotComment.sid = cursor.getInt(cursor.getColumnIndex(HotCommentsDBInfo.SID));
        hotComment.comment = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.COMMENT));
        hotComment.username = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.AUTHOR));
        hotComment.subject = cursor.getString(cursor.getColumnIndex(HotCommentsDBInfo.TITLE));
        return hotComment;
    }
}
