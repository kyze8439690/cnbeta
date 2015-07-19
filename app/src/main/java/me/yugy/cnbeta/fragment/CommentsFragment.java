package me.yugy.cnbeta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.app.common.core.BaseFragment;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.CommentsAdapter;
import me.yugy.cnbeta.model.Comment;
import me.yugy.cnbeta.network.CnBeta;
import me.yugy.cnbeta.widget.CircularProgressBar;

/**
 * Created by yugy on 2014/9/6.
 */
public class CommentsFragment extends BaseFragment {

    @InjectView(R.id.list) ListView mListView;
    @InjectView(R.id.loding_progress) CircularProgressBar mLoadingProgressBar;

    private CommentsAdapter mCommentsAdapter;
    private int mSid;

    public static CommentsFragment newInstance(int sid){
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putInt("sid", sid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSid = getArguments().getInt("sid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData(1);
    }

    private void getData(int page){
        CnBeta.getNewsComments(getActivity(), mSid, page, new Response.Listener<Comment[]>() {
            @Override
            public void onResponse(Comment[] response) {
                setComments(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getCause() instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "网络超时", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setComments(Comment[] comments){
        mLoadingProgressBar.setVisibility(View.GONE);
        mCommentsAdapter = new CommentsAdapter(comments);
        mListView.setAdapter(mCommentsAdapter);
    }

    public void appendComments(Comment[] comments){
        if(mCommentsAdapter != null){
            mCommentsAdapter.append(comments);
        }
    }

}
