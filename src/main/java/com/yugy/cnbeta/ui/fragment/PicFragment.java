package com.yugy.cnbeta.ui.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.yugy.cnbeta.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yugy on 14-1-8.
 */
public class PicFragment extends Fragment implements PhotoViewAttacher.OnViewTapListener, View.OnClickListener{

    private ProgressBar mProgressBar;
    private PhotoView mPhotoView;

    public static PicFragment newInstance(String url){
        PicFragment picFragment = new PicFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        picFragment.setArguments(args);
        return picFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pic, container, false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.pic_progress);
        mPhotoView = (PhotoView) rootView.findViewById(R.id.pic_photoview);
        mProgressBar.setOnClickListener(this);
        mPhotoView.setOnViewTapListener(this);

        String picUrl = getArguments().getString("url");
        ImageLoader.getInstance().displayImage(picUrl, mPhotoView, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    private void toggleActionBar(){
        if(getActivity().getActionBar().isShowing()){
            getActivity().getActionBar().hide();
        }else{
            getActivity().getActionBar().show();
        }
    }

    @Override
    public void onViewTap(View view, float v, float v2) {
        toggleActionBar();
    }

    @Override
    public void onClick(View v) {
        toggleActionBar();
    }
}
