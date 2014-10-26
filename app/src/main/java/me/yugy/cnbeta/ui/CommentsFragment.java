package me.yugy.cnbeta.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.CommentsAdapter;
import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.utils.UIUtils;
import me.yugy.cnbeta.utils.VersionUtils;
import me.yugy.cnbeta.widget.CircularProgressBar;

/**
 * Created by yugy on 2014/9/6.
 */
public class CommentsFragment extends Fragment{

    @InjectView(R.id.list) ListView mListView;
    @InjectView(R.id.loding_progress) CircularProgressBar mLoadingProgressBar;

    private CommentsAdapter mCommentsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.inject(this, view);
        if(VersionUtils.isKitKat()){
            mListView.setPadding(
                    mListView.getPaddingLeft(),
                    mListView.getPaddingTop(),
                    mListView.getPaddingRight(),
                    mListView.getPaddingBottom() + UIUtils.getNavigationBarHeight(getActivity())
            );
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setComments(Comments comments, int type){
        mLoadingProgressBar.setVisibility(View.GONE);
        mCommentsAdapter = new CommentsAdapter(comments, type);
        mListView.setAdapter(mCommentsAdapter);
    }

    public void appendComments(Comments comments){
        if(mCommentsAdapter != null){
            mCommentsAdapter.append(comments);
        }
    }

}
