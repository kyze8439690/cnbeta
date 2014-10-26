package me.yugy.cnbeta.network;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.yugy.cnbeta.model.Comment;
import me.yugy.cnbeta.model.Comments;

/**
 * Created by yugy on 2014/9/6.
 */
public class NewsCommentRequest extends Request<Comments> {

    private int mSid;
    private int mPage;
    private String mSn;
    private Response.Listener<Comments> mListener;

    public NewsCommentRequest(int sid, int page, String sn, Response.Listener<Comments> listener, Response.ErrorListener errorListener) {
        super(Method.POST, "http://www.cnbeta.com/cmt", errorListener);
        mSid = sid;
        mPage = page;
        mSn = sn;
        mListener = listener;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        try {
            Map<String, String> params = new Hashtable<String, String>();
            params.put("op", generateOp(mPage, mSid, mSn));
            return params;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return super.getParams();
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new Hashtable<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Referer", "http://www.cnbeta.com/articles/" + mSid + ".htm");
        return headers;
    }

    @Override
    protected Response<Comments> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            if(json.getString("status").equals("success")){
                String commentString = new String(Base64.decode(json.getString("result"), Base64.NO_WRAP), "UTF-8");
                JSONObject commentJson = new JSONObject(commentString);

                JSONArray hotCommentIdJson = commentJson.getJSONArray("hotlist");
                int hotCommentCount = hotCommentIdJson.length();
                List<Integer> hotCommentIds = new ArrayList<Integer>();
                for (int i = 0; i < hotCommentCount; i++) {
                    hotCommentIds.add(hotCommentIdJson.getJSONObject(i).getInt("tid"));
                }

                JSONObject commentStore = commentJson.optJSONObject("cmntstore");

                if(commentStore != null) {
                    List<Comment> commentList = new ArrayList<Comment>();
                    List<Comment> hotCommentList = new ArrayList<Comment>();

                    Iterator iterator = commentStore.keys();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        JSONObject commentStoreJsonItem = commentStore.getJSONObject(key);
                        Comment comment = Comment.fromJson(commentStoreJsonItem);
                        commentList.add(comment);
                        if (hotCommentIds.contains(Integer.valueOf(key))) {
                            hotCommentList.add(comment);
                        }
                    }

                    int totalCount = commentJson.getInt("comment_num");
                    Comments comments = new Comments(commentList, hotCommentList, totalCount);
                    return Response.success(comments, HttpHeaderParser.parseCacheHeaders(response));
                }else{
                    return Response.success(new Comments(new ArrayList<Comment>(), new ArrayList<Comment>(),
                        0), HttpHeaderParser.parseCacheHeaders(response));
                }
            }else{
                return Response.error(new ParseError(new Exception("get comments failed")));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        } catch (ParseException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Comments response) {
        mListener.onResponse(response);
    }

    private static String generateOp(int page, int sid, String sn) throws UnsupportedEncodingException {
        return URLEncoder.encode(Base64.encodeToString((page + "," + sid + "," + sn).getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8");
    }

}
