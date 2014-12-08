package com.mobile.cupboardmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by matt-auld on 07/12/14.
 */
public class CustomCursorAdapter extends CursorAdapter {

    /**
     * Interface between Adapter and Fragments
     * This will allow each of the fragments to setup their onClickListeners
     */
    public static interface onViewListener {
        public void onBindView(View view, Cursor cursor);
    }

    private int mLayoutId;
    private onViewListener mOnViewListener = null;

    public CustomCursorAdapter(Context context, Cursor c, boolean autoRequery, int res) {
        super(context, c, autoRequery);
        mLayoutId = res;
    }

    public void setOnViewListener(onViewListener onViewListener) {
        mOnViewListener = onViewListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(mLayoutId, null);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (mOnViewListener != null)
            mOnViewListener.onBindView(view, cursor);
    }
}
