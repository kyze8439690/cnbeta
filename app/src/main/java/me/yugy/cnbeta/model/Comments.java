package me.yugy.cnbeta.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yugy on 2014/9/6.
 */
public class Comments {

    private Comment[] mComments;
    private Comment[] mHotComments;
    private int mTotalCount;

    public Comments(List<Comment> comments, List<Comment> hotComments, int totalCount) {

        Collections.sort(comments, COMPARATOR);
        mComments = comments.toArray(new Comment[comments.size()]);

        Collections.sort(hotComments, COMPARATOR);
        mHotComments = hotComments.toArray(new Comment[hotComments.size()]);

        mTotalCount = totalCount;
    }

    public Comment[] getComments() {
        return mComments;
    }

    public Comment[] getHotComments() {
        return mHotComments;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    private static final Comparator<Comment> COMPARATOR = new Comparator<Comment>() {
        @Override
        public int compare(Comment lhs, Comment rhs) {
            return (int) (lhs.time - rhs.time);
        }
    };
}
