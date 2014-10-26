package me.yugy.cnbeta.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.yugy.cnbeta.R;

/**
 * Created by yugy on 2014/9/6.
 */
public class MenuAdapter extends ArrayAdapter<String>{

    public void setCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
        notifyDataSetChanged();
    }

    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    private int mCurrentSelection = 0;

    public MenuAdapter(Context context) {
        super(context, R.layout.view_menu_item,
                context.getResources().getStringArray(R.array.toolbar_spinner_entries));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        if(position == mCurrentSelection){
            view.setTextColor(parent.getContext().getResources().getColor(R.color.menu_text_selected));
        }else{
            view.setTextColor(Color.BLACK);
        }
        return view;
    }
}
