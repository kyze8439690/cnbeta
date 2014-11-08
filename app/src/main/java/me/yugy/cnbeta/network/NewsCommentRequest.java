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
public class NewsCommentRequest extends Request<Comment[]> {

    private Response.Listener<Comment[]> mListener;

    public NewsCommentRequest(String url, Response.Listener<Comment[]> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<Comment[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            if(json.getString("status").equals("success")){
                JSONArray result = json.getJSONArray("result");
                Comment[] comments = new Comment[result.length()];
                for (int i = 0; i < result.length(); i++) {
                    Comment comment = Comment.fromJson(result.getJSONObject(i));
                    comments[i] = comment;
                }
                return Response.success(comments, HttpHeaderParser.parseCacheHeaders(response));
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
    protected void deliverResponse(Comment[] response) {
        mListener.onResponse(response);
    }

}
