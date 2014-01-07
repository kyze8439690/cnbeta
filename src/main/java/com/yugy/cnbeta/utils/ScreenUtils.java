package com.yugy.cnbeta.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * Created by yugy on 14-1-7.
 */
public class ScreenUtils {

    public static int dp(Context context, float dp){
        Resources resources = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
        int displayHeight = wm.getDefaultDisplay().getHeight();
        return displayHeight;
    }

}
