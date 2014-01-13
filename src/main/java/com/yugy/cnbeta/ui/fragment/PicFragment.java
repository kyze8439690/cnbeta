package com.yugy.cnbeta.ui.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.yugy.cnbeta.Application;
import com.yugy.cnbeta.R;
import com.yugy.cnbeta.network.RequestManager;

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
        RequestManager.getImageLoader().get(picUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
            @Override
            public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                if (!isImmediate) {
                    TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{
                            new ColorDrawable(Color.TRANSPARENT),
                            new BitmapDrawable(Application.getContext().getResources(), imageContainer.getBitmap())
                    });
                    transitionDrawable.setCrossFadeEnabled(true);
                    mPhotoView.setImageDrawable(transitionDrawable);
                    transitionDrawable.startTransition(100);
                } else {
                    mPhotoView.setImageBitmap(imageContainer.getBitmap());
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mPhotoView.setImageResource(R.drawable.ic_image_fail);
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
