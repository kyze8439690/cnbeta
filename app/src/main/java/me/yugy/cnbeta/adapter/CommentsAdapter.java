package me.yugy.cnbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.model.Comment;
import me.yugy.cnbeta.model.Comments;
import me.yugy.cnbeta.widget.RelativeTimeTextView;

/**
 * Created by yugy on 2014/9/6.
 */
public class CommentsAdapter extends BaseAdapter{

    public static final int TYPE_ALL_COMMENTS = 0;
    public static final int TYPE_HOT_COMMENTS = 1;

    private int mType;

    private ArrayList<Comment> mComments;

    public CommentsAdapter(Comments comments, int type){
        mType = type;
        mComments = new ArrayList<Comment>();
        switch (type){
            case TYPE_ALL_COMMENTS:
                int count = comments.getComments().length;
                for (int i = 0; i < count; i++) {
                    mComments.add(comments.getComments()[i]);
                }
                break;
            case TYPE_HOT_COMMENTS:
                count = comments.getHotComments().length;
                for (int i = 0; i < count; i++) {
                    mComments.add(comments.getHotComments()[i]);
                }
                break;
        }

    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_comment_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else{
          holder = (ViewHolder) view.getTag();
        }
        holder.parse(getItem(position));
        return view;
    }

    public void append(Comments comments){
        switch (mType){
            case TYPE_ALL_COMMENTS:
                int count = comments.getComments().length;
                for (int i = 0; i < count; i++) {
                    mComments.add(comments.getComments()[i]);
                }
                break;
            case TYPE_HOT_COMMENTS:
                count = comments.getHotComments().length;
                for (int i = 0; i < count; i++) {
                    mComments.add(comments.getHotComments()[i]);
                }
                break;
        }
        notifyDataSetChanged();
    }

    class ViewHolder{

        @InjectView(R.id.comment_first_letter) TextView mFirstLetter;
        @InjectView(R.id.comment_name) TextView mName;
        @InjectView(R.id.comment_content) TextView mContent;
        @InjectView(R.id.comment_time) RelativeTimeTextView mTime;
        @InjectView(R.id.comment_location) TextView mLocation;

        public ViewHolder(View view){
            ButterKnife.inject(this, view);
        }

        public void parse(Comment comment){
            mFirstLetter.setText(comment.author.substring(0, 1));
            mName.setText(comment.author);
            mContent.setText(comment.content);
            mTime.setReferenceTime(comment.time);
            mLocation.setText(comment.location);
        }

    }
}
