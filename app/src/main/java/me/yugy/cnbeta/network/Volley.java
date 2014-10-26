package me.yugy.cnbeta.network;

import com.android.volley.RequestQueue;

import me.yugy.cnbeta.Application;

/**
 * Created by yugy on 2014/8/30.
 */
public class Volley {

    private static RequestQueue sInstance;

    public static RequestQueue getInstance() {
        if (sInstance == null){
            synchronized (Volley.class){
                if (sInstance == null){
                    sInstance = com.android.volley.toolbox.Volley.newRequestQueue(Application.getContext());
                }
            }
        }
        return sInstance;
    }
}
