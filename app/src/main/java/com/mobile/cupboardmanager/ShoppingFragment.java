package com.mobile.cupboardmanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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
        CursorLoader cursorLoader = new CursorLoader(getActivity().getBaseContext(),
                DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        ((MainActivity)getActivity()).setShareIntent();
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

    private void moveShoppingItemToCupboard(long shoppingId, long itemID, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.CUPBOARD_ITEM, itemID);
        values.put(DatabaseHandler.CUPBOARD_QUANTITY, quantity);

        Calendar calendar = Calendar.getInstance();
        long tomorrowInMs = calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1);
        // Placeholder date
        values.put(DatabaseHandler.CUPBOARD_EXPIRY_TIME, tomorrowInMs);

        getActivity().getContentResolver().insert(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI,
                values);
        getActivity().getContentResolver().delete(ContentUris.withAppendedId(
                DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, shoppingId), null, null);
    }

    @Override
    public void onBindView(View view, Cursor cursor) {
        final String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ITEM_NAME));
        final long itemId = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.SHOPPING_ITEM));
        final int itemQuantity = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.SHOPPING_QUANTITY));
        final long shoppingId = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.SHOPPING_ID));
        ((TextView)view.findViewById(R.id.item_name)).setText(itemName);
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
                moveShoppingItemToCupboard(shoppingId, itemId, itemQuantity);
                Toast.makeText(getActivity(), "Item moved to cupboard list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
