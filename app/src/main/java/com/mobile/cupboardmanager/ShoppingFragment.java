package com.mobile.cupboardmanager;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Display interactive list of ShoppingItems, with creation button
 */
public class ShoppingFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, CustomCursorAdapter.onViewListener {


    private CustomCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);
        rootView.findViewById(R.id.add_shopping_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.INTENT_ITEM_URI,
                        DBContentProvider.SHOPPING_ITEMS.CONTENT_URI);
                startActivity(intent);
            }
        });

        // Populate data
        populateData(rootView);

        return rootView;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {DBContentProvider.SHOPPING_ITEMS._ID,
                DBContentProvider.SHOPPING_ITEMS.Quantity };
        CursorLoader cursorLoader = new CursorLoader(getActivity().getBaseContext(),
                DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is no longer available so get rid of reference
        adapter.swapCursor(null);
    }

    private void populateData(View rootView) {
        getLoaderManager().initLoader(0, null, this);

        Cursor cursor = getActivity().getContentResolver().query(
                DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, null, null, null, null);

        adapter = new CustomCursorAdapter(getActivity(), cursor, false, R.layout.shoppingrow);
        adapter.setOnViewListener(this);

        ListView listView = (ListView)rootView.findViewById(R.id.shopping_list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBindView(View view, Cursor cursor) {
        ((TextView)view.findViewById(R.id.item_name)).setText(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.SHOPPING_ID)));
        final long shoppingId = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.SHOPPING_ID));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.INTENT_ITEM_URI,
                        DBContentProvider.SHOPPING_ITEMS.CONTENT_URI);
                intent.putExtra(ItemActivity.INTENT_ITEM_ID, shoppingId);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.need_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
