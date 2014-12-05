package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.Context;
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

        // Insert Example data
        ArrayList<String> itemData = new ArrayList<String>();
        itemData.add("Banana");
        itemData.add("Bread");
        itemData.add("Cheese");

        for (int i = 0; i < itemData.size(); i++) {
            String name = itemData.get(i);
            Item temp = new Item(name);

            dbHandler.insertItem(temp);
            dbHandler.insertItem(new CupboardItem(name, i, 1000 * i));
            dbHandler.insertItem(new ShoppingItem(name, i));
        }

        provider = getProvider();
        provider.setDBHandler(dbHandler);
    }
}
