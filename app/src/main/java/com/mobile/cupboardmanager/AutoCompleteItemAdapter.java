package com.mobile.cupboardmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt-auld on 02/12/14.
 */
public class AutoCompleteItemAdapter extends ArrayAdapter<String> {

    private ArrayList<String> mItemNames;

    private Filter mFilter = new Filter() {

        @Override
        public String convertResultToString(Object resultValue) {
            return (String)resultValue;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null) {
                List<String> matches = queryItems(constraint);
                filterResults.values = matches;
                filterResults.count = matches.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                clear();
                ArrayList<String> matches = (ArrayList<String>)results.values;
                for (String itemName : matches) {
                    add(itemName);
                }
                notifyDataSetChanged();
            }
        }
    };

    public AutoCompleteItemAdapter(Context context, int res, List<String> items) {
        super(context, res, items);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.layout_auto_complete_item, null);
        }
        String itemName = getItem(pos);
        ((TextView)view.findViewById(R.id.name)).setText(itemName);

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private List<String> queryItems(CharSequence constraint) {
        Cursor cursor = getContext().getContentResolver().query(DBContentProvider.ITEMS.CONTENT_URI,
                new String[] {DBContentProvider.ITEMS.Name}, DBContentProvider.ITEMS.Name +
                        " LIKE ?", new String[]{"%"+constraint+"%"}, null);

        List<String> matchingItemNames = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name =  cursor.getString(cursor.getColumnIndex(DBContentProvider.ITEMS.Name));
            matchingItemNames.add(name);
        }

        cursor.close();
        return matchingItemNames;
    }
}
