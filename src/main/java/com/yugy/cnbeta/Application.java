package com.yugy.cnbeta;

import android.content.Context;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.File;

/**
 * Created by yugy on 14-1-6.
 */
public class Application extends android.app.Application{

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image_loading)
                .showImageOnFail(R.drawable.ic_image_fail)
                .cacheInMemory(false)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(600))
                .build();
        File cacheDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .defaultDisplayImageOptions(options)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static Context getContext(){
        return mContext;
    }
}
