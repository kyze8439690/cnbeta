package com.yugy.cnbeta.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.yugy.cnbeta.Application;

/**
 * Created by yugy on 14-1-6.
 */
public class RequestManager {

    private static RequestManager mInstance;
    private static RequestQueue mRequestQueue;

    private RequestManager(){
        mRequestQueue = Volley.newRequestQueue(Application.getContext(), new HurlStack());
        mRequestQueue.start();
    }

    public static RequestManager getInstance(){
        if(mInstance == null){
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    public void addRequest(Context context, Request request){
        request.setTag(context);
        mRequestQueue.add(request);
    }

    public void cancelRequests(Context tag){
        mRequestQueue.cancelAll(tag);
    }
}
