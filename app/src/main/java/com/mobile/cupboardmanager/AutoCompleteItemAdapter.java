package com.mobile.cupboardmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matt-auld on 02/12/14.
 */
public class AutoCompleteItemAdapter extends ArrayAdapter<Item> {

    private ArrayList<Item> mItems;

    private Filter mFilter = new Filter() {

        @Override
        public String convertResultToString(Object resultValue) {
            return ((Item)resultValue).getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null) {
                List<Item> matches = queryItems(constraint);
                filterResults.values = matches;
                filterResults.count = matches.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                clear();
                ArrayList<Item> matches = (ArrayList<Item>)results.values;
                for (Item item : matches) {
                    add(item);
                }
                notifyDataSetChanged();
            }
        }
    };

    public AutoCompleteItemAdapter(Context context, int res, List<Item> items) {
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
        Item item = getItem(pos);
        ((TextView)view.findViewById(R.id.name)).setText(item.getName());

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private List<Item> queryItems(CharSequence constraint) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        List<Item> items = db.fetchAllItemsLike(constraint.toString());
        db.close();
        return items;
    }
}
