package me.yugy.cnbeta.utils;

import android.os.Build;

/**
 * Created by gzyanghui on 2014/9/17.
 */
public class VersionUtils {

    public static boolean isKitKat(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

}
