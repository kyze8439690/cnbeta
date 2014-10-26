package me.yugy.cnbeta.network;

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
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Map;

import me.yugy.cnbeta.model.RealTimeNews;

/**
 * Created by yugy on 14/10/26.
 */
public class RealTimeNewsRequest extends Request<RealTimeNews[]> {

    private Response.Listener<RealTimeNews[]> mListener;

    public RealTimeNewsRequest(String url, Response.Listener<RealTimeNews[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);
        mListener = listener;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new Hashtable<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Referer", "http://www.cnbeta.com/");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.put("Accept", "*/*");
        return headers;
    }

    @Override
    protected Response<RealTimeNews[]> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            jsonString = jsonString.replaceAll("jQuery[0-9]+_[0-9]+s\\(", "");
            jsonString = jsonString.substring(0, jsonString.length() - 1);
            JSONObject json = new JSONObject(jsonString);
            if(!json.getString("status").equals("success")){
                return Response.error(new ParseError(new Exception("Get News failed.")));
            }else{
                JSONArray array = json.getJSONArray("result");
                RealTimeNews[] newses = new RealTimeNews[array.length()];
                for (int i = 0; i < newses.length; i++) {
                    newses[i] = RealTimeNews.fromJson(array.getJSONObject(i));
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
    protected void deliverResponse(RealTimeNews[] response) {
        mListener.onResponse(response);
    }
}
