package com.yugy.cnbeta.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.yugy.cnbeta.ui.fragment.PicFragment;

import java.util.ArrayList;

/**
 * Created by yugy on 14-1-8.
 */
public class PicFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> mImgUrls;

    public PicFragmentPagerAdapter(FragmentManager fragmentManager, ArrayList<String> imgUrls){
        super(fragmentManager);
        mImgUrls = imgUrls;
    }

    @Override
    public Fragment getItem(int i) {
        return PicFragment.newInstance(mImgUrls.get(i));
    }

    @Override
    public int getCount() {
        return mImgUrls.size();
    }
}
