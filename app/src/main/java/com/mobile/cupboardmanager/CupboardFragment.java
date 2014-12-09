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

/**
 * Display interactive list of CupboardItems, with creation button
 */
public class CupboardFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, CustomCursorAdapter.onViewListener {

    private CustomCursorAdapter adapter;

    final private static Calendar mCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cupboard, container, false);
        rootView.findViewById(R.id.add_cupboard_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.INTENT_ITEM_URI,
                        DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI);
                startActivity(intent);
            }
        });

        populateData(rootView);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(getActivity().getBaseContext(),
                DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, null, null, null, null);
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
                DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, null, null, null, null);

        adapter = new CustomCursorAdapter(getActivity(), cursor, false, R.layout.cupboardrow);
        adapter.setOnViewListener(this);

        ListView listView = (ListView)rootView.findViewById(R.id.cupboard_list);
        listView.setAdapter(adapter);
    }

    private void moveCupboardItemToShopping(long cupboardID, long itemID, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.SHOPPING_ITEM, itemID);
        values.put(DatabaseHandler.SHOPPING_QUANTITY, quantity);

        getActivity().getContentResolver().insert(DBContentProvider.SHOPPING_ITEMS.CONTENT_URI,
                values);
        getActivity().getContentResolver().delete(ContentUris.withAppendedId(
                DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, cupboardID), null, null);
    }

    private static String getDateString(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return (day + "-" + month + "-" + year);
    }

    @Override
    public void onBindView(final View view, final Cursor cursor) {
        view.setAlpha(1f);
        final long cupboardId = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.CUPBOARD_ID));
        final long itemId = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.CUPBOARD_ITEM));
        final int itemQuantity = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.CUPBOARD_QUANTITY));
        final String itemName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ITEM_NAME));
        final long itemExpiry = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.CUPBOARD_EXPIRY_TIME));
        mCalendar.setTimeInMillis(itemExpiry);

        ((TextView) view.findViewById(R.id.item_name)).setText(itemName);
        ((TextView) view.findViewById(R.id.item_expiry_date)).setText(getDateString(mCalendar));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(ItemActivity.INTENT_ITEM_URI,
                        DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI);
                intent.putExtra(ItemActivity.INTENT_ITEM_ID, cupboardId);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.need_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.animate().alpha(0.3f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        moveCupboardItemToShopping(cupboardId, itemId, itemQuantity);
                        Toast.makeText(getActivity(), "Item moved to shopping list", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
