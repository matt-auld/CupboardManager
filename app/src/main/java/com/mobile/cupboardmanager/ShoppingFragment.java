package com.mobile.cupboardmanager;


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

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Display interactive list of ShoppingItems, with creation button
 */
public class ShoppingFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{


    private SimpleCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shopping, container, false);
        rootView.findViewById(R.id.add_shopping_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.ITEM_TYPE_INTENT_EXTRA,
                        ItemActivity.ITEM_SHOPPING_TYPE);
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

    private void populateData(View rootView)
    {
        getLoaderManager().initLoader(0, null, this);

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { DBContentProvider.SHOPPING_ITEMS._ID };

        // Fields on the UI to map to
        int[] to = new int[] { R.id.shopping_item };

        adapter = new SimpleCursorAdapter(getActivity().getApplicationContext(), R.layout.shoppingrow, null, from,
                to, 0);

        ListView listView = (ListView)rootView.findViewById(R.id.shopping_list);

        listView.setAdapter(adapter);
    }
}
