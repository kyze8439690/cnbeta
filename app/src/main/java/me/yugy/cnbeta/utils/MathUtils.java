package me.yugy.cnbeta.utils;

import java.text.DecimalFormat;

/**
 * Created by gzyanghui on 2014/9/25.
 */
public class MathUtils {

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    public static String getSize(long byteSize){
        String fileSizeString = "0B";

        if (byteSize<=0){
            return fileSizeString;
        }

        DecimalFormat df = new DecimalFormat("#.00");

        if (byteSize < 1024)
        {
            fileSizeString = df.format((double) byteSize) + "B";
        }
        else if (byteSize < 1048576)
        {
            fileSizeString = df.format((double) byteSize / 1024) + "KB";
        }
        else if (byteSize < 1073741824)
        {
            fileSizeString = df.format((double) byteSize / 1048576) + "MB";
        }
        else
        {
            fileSizeString = df.format((double) byteSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

}
