package me.yugy.cnbeta.utils;

import android.os.Build;

import java.lang.reflect.Method;

/**
 * Created by gzyanghui on 2014/9/17.
 */
public class MeizuUtils {

    /**
     * 新型号可用反射调用Build.hasSmartBar()来判断有无SmartBar
     * @return
     */
    public static boolean hasSmartBar() {
        try {
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }

        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

    public static final boolean isMeizu(){
        return Build.MANUFACTURER.equalsIgnoreCase("meizu");
    }

}
