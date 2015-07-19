package me.yugy.cnbeta.adapter;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yugy.cnbeta.R;
import me.yugy.cnbeta.dao.dbinfo.AllNewsDBInfo;
import me.yugy.cnbeta.model.News;
import me.yugy.cnbeta.fragment.AllNewsFragment;
import me.yugy.cnbeta.widget.CursorAdapter2;
import me.yugy.cnbeta.widget.RelativeTimeTextView;

/**
 * Created by yugy on 2014/8/30.
 */
public class AllNewsAdapter extends CursorAdapter2<AllNewsAdapter.BaseViewHolder> {

    private AllNewsFragment mFragment;

    public AllNewsAdapter(AllNewsFragment fragment) {
        super(fragment.getActivity(), null, true);
        mFragment = fragment;
    }

    @Override
    public void bindViewHolder(AllNewsAdapter.BaseViewHolder holder, Cursor cursor) {
        final News news = News.fromCursor(cursor);
        holder.parse(news);
    }

    @Override
    public AllNewsAdapter.BaseViewHolder newViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType){
            case News.TYPE_WITH_IMAGE:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_news_card_with_image, viewGroup, false);
                return new WithImageViewHolder(view);
            case News.TYPE_ONLY_TEXT:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_news_card_only_text, viewGroup, false);
                return new OnlyTextViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex(AllNewsDBInfo.TYPE));
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public abstract void parse(News news);
    }

    class OnlyTextViewHolder extends BaseViewHolder{

        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.time) RelativeTimeTextView time;
        @InjectView(R.id.desc) TextView desc;

        private int sid;

        public OnlyTextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void parse(News news) {
            sid = news.sid;
            if(news.read){
                title.setTextColor(Color.parseColor("#ffcccccc"));
            }else{
                title.setTextColor(Color.parseColor("#ff4d6674"));
            }
            title.setText(news.title);
            desc.setText(news.summary);
            time.setReferenceTime(news.time);
        }

        @Override
        public void onClick(View v) {
            mFragment.onNewsSelect(sid);
        }
    }

    class WithImageViewHolder extends BaseViewHolder{

        @InjectView(R.id.image) ImageView image;
        @InjectView(R.id.title) TextView title;
        @InjectView(R.id.time) RelativeTimeTextView time;
        @InjectView(R.id.desc) TextView desc;

        private int sid;

        public WithImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void parse(News news){
            sid = news.sid;
            if(news.read) {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);
                image.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                title.setTextColor(Color.parseColor("#ffcccccc"));
            }else{
                image.setColorFilter(null);
                title.setTextColor(Color.WHITE);
            }
            title.setText(news.title);
            desc.setText(news.summary);
            time.setReferenceTime(news.time);

            image.setImageResource(R.color.default_loading_color);

            String imageUrl = news.thumb;
            if(!imageUrl.contains("undefined")) {
                imageUrl = imageUrl.replace("_100x100", "").replace("thumb/mini/", "")
                        .replace(".jpg.jpg", ".jpg").replace(".png.png", ".png").replace(".gif.gif", ".gif");
            }
            ImageLoader.getInstance().displayImage(imageUrl, image);
        }

        @Override
        public void onClick(View v) {
            mFragment.onNewsSelect(sid);
        }
    }

}
