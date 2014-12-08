package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.mobile.cupboardmanager.CupboardItem;
import com.mobile.cupboardmanager.DatabaseHandler;
import com.mobile.cupboardmanager.Item;
import com.mobile.cupboardmanager.ShoppingItem;
import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.util.ArrayList;

/**
 * Created by Paul on 02/12/2014.
 */
public class ItemsBaseTest extends ProviderTestCase2<DBContentProvider> {

    protected DBContentProvider provider;

    public ItemsBaseTest() {
        super(DBContentProvider.class, DBContentProvider.AUTHORITY);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Delete any existing database and set up ready to create example data
        Context mContext = getContext();
        mContext.deleteDatabase(DatabaseHandler.DATABASE_NAME);
        DatabaseHandler dbHandler = new DatabaseHandler(mContext);
        dbHandler.getWritableDatabase();

        provider = getProvider();
        provider.setDBHandler(dbHandler);

        // Insert Example data
        ArrayList<String> itemData = new ArrayList<String>();
        itemData.add("Banana");
        itemData.add("Bread");
        itemData.add("Cheese");

        ContentValues values = new ContentValues();

        for (int i = 0; i < itemData.size(); i++) {
            String name = itemData.get(i);

            values.put(DatabaseHandler.ITEM_NAME, name);
            Uri uri = provider.insert(DBContentProvider.ITEMS.CONTENT_URI, values);
            values.clear();

            long itemId = ContentUris.parseId(uri);

            values.put(DatabaseHandler.SHOPPING_ITEM, itemId);
            values.put(DatabaseHandler.SHOPPING_QUANTITY, i);
            provider.insert(DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, values);
            values.clear();

            values.put(DatabaseHandler.CUPBOARD_ITEM, itemId);
            values.put(DatabaseHandler.CUPBOARD_QUANTITY, i);
            values.put(DatabaseHandler.CUPBOARD_EXPIRY_TIME, 1000 * i);
            provider.insert(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, values);
            values.clear();
        }
    }
}
