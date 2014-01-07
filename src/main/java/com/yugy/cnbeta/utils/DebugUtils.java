package com.yugy.cnbeta.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.yugy.cnbeta.Conf;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yugy on 14-1-6.
 */
public class DebugUtils {

    public static void log(Object log){
        if(Conf.DEBUG){
            if(log == null){ //log null
                Log.e(Conf.LOG_TAG, "log content is null");
            }else if(log instanceof Integer || log instanceof Float || log instanceof Double){ //log num
                Log.d(Conf.LOG_TAG, String.valueOf(log));
            }else if(log instanceof String){ //log string
                Log.d(Conf.LOG_TAG, (String) log);
            }else if(log instanceof JSONObject || log instanceof JSONArray){ //log json
                Log.d(Conf.LOG_TAG, log.toString());
            }else if(log instanceof byte[]){ //log byte array
                Log.d(Conf.LOG_TAG, new String((byte[]) log));
            }
        }
    }
}

