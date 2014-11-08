package me.yugy.cnbeta.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Map;

import me.yugy.cnbeta.model.News;

/**
 * Created by yugy on 2014/8/30.
 */
public class NewsRequest extends Request<News[]>{

    private Response.Listener<News[]> mListener;

    public NewsRequest(String url, Response.Listener<News[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        mListener = listener;
    }

    @Override
    protected Response<News[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(jsonString);
            if(!json.getString("status").equals("success")){
                return Response.error(new ParseError(new Exception("Get News failed.")));
            }else{
                JSONArray array = json.getJSONArray("result");
                News[] newses = new News[array.length()];
                for (int i = 0; i < newses.length; i++) {
                    newses[i] = News.fromJson(array.getJSONObject(i));
                }
                return Response.success(newses, HttpHeaderParser.parseCacheHeaders(response));
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
    protected void deliverResponse(News[] response) {
        mListener.onResponse(response);
    }
}
