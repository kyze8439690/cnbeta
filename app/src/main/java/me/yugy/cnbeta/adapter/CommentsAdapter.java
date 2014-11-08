package me.yugy.cnbeta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

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

    private Comment[] mComments;

    public CommentsAdapter(Comment[] comments){
        mComments = comments;
    }

    @Override
    public int getCount() {
        return mComments.length;
    }

    @Override
    public Comment getItem(int position) {
        return mComments[position];
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

    public void append(Comment[] comments){
        ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
        commentArrayList.addAll(Arrays.asList(mComments));
        commentArrayList.addAll(Arrays.asList(comments));
        mComments = commentArrayList.toArray(new Comment[commentArrayList.size()]);

        notifyDataSetChanged();
    }

    class ViewHolder{

        @InjectView(R.id.comment_first_letter) TextView mFirstLetter;
        @InjectView(R.id.comment_name) TextView mName;
        @InjectView(R.id.comment_content) TextView mContent;
        @InjectView(R.id.comment_time) RelativeTimeTextView mTime;

        public ViewHolder(View view){
            ButterKnife.inject(this, view);
        }

        public void parse(Comment comment){
            mFirstLetter.setText(comment.author.substring(0, 1));
            mName.setText(comment.author);
            mContent.setText(comment.content);
            mTime.setReferenceTime(comment.time);
        }

    }
}
