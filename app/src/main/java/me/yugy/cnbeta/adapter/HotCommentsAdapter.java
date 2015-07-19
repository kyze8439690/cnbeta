package me.yugy.cnbeta.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.model.HotComment;
import me.yugy.cnbeta.fragment.HotCommentsFragment;
import me.yugy.cnbeta.widget.CursorAdapter2;
import me.yugy.cnbeta.widget.RelativeTimeTextView;

/**
 * Created by yugy on 2014/9/8.
 */
public class HotCommentsAdapter extends CursorAdapter2<HotCommentsAdapter.HotCommentViewHolder> {

    private static final String LOG_TAG = HotCommentsAdapter.class.getName();
    private HotCommentsFragment mFragment;

    public HotCommentsAdapter(HotCommentsFragment fragment) {
        super(fragment.getActivity(), null);
        mFragment = fragment;
    }

    @Override
    public HotCommentViewHolder newViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_hotcomments_item, parent, false);
        HotCommentViewHolder holder = new HotCommentViewHolder(view);
        return holder;
    }

    @Override
    public void bindViewHolder(HotCommentViewHolder viewHolder, Cursor cursor) {
        HotComment hotComment = HotComment.fromCursor(cursor);
        viewHolder.parse(hotComment);
    }

    class HotCommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.comment_first_letter) TextView firstLetter;
        @InjectView(R.id.comment_content) TextView comment;
        @InjectView(R.id.comment_name_title) TextView nameAndTitle;
        @InjectView(R.id.comment_time) RelativeTimeTextView time;

        private int mSid;

        public HotCommentViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
        }

        public void parse(HotComment hotComment){
            mSid = hotComment.sid;
            firstLetter.setText(hotComment.username.substring(0, 1));
            comment.setText(hotComment.comment);
            nameAndTitle.setText(hotComment.username + "  — 《" + hotComment.subject + "》");
            time.setText("");
        }

        @Override
        public void onClick(View v) {
            mFragment.onNewsSelect(mSid);
        }
    }
}
