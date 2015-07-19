package me.yugy.cnbeta.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import me.yugy.app.common.core.BaseFragment;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.adapter.MenuAdapter;

/**
 * Created by yugy on 2014/8/30.
 */
public class MenuFragment extends BaseFragment {

    public static final int TYPE_ALL_NEWS = 0;
    public static final int TYPE_HOT_COMMENTS = 1;
    public static final int TYPE_RECOMMEND = 2;

    private OnMenuSelectListener mListener;
    private MenuAdapter mAdapter;
    @InjectView(R.id.list) ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new MenuAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            int currentSelection = savedInstanceState.getInt("currentSelection", 0);
            mAdapter.setCurrentSelection(currentSelection);
        }
    }

    @OnItemClick(R.id.list)
    void onItemSelect(int position){
        mAdapter.setCurrentSelection(position);
        if(mListener != null) {
            switch (position) {
                case 0:
                    mListener.onMenuSelect(TYPE_ALL_NEWS);
                    break;
                case 1:
                    mListener.onMenuSelect(TYPE_HOT_COMMENTS);
                    break;
                case 2:
                    mListener.onMenuSelect(TYPE_RECOMMEND);
                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMenuSelectListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.getClass().getName() + "should implement the OnMenuSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentSelection", mAdapter.getCurrentSelection());
    }

    public interface OnMenuSelectListener{
        public void onMenuSelect(int type);
    }
}
