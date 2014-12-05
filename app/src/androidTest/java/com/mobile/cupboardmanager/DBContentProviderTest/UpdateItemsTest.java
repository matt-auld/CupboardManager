package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class UpdateItemsTest extends ItemsBaseTest {

    private int updatedCount;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues newValues = new ContentValues();
        newValues.put(DBContentProvider.ITEMS.Name, "Food");
        updatedCount = provider.update(DBContentProvider.ITEMS.CONTENT_URI, newValues,
                DBContentProvider.ITEMS.Name + " LIKE 'B%'", null);
    }

    public void testItemsUpdatedCorrectly() throws Exception {
        Cursor cursor = provider.query(DBContentProvider.ITEMS.CONTENT_URI,
                null, null, null, null);

        int columnId = cursor.getColumnIndex(DBContentProvider.ITEMS.Name);

        cursor.moveToNext();
        assertEquals("Food", cursor.getString(columnId));
        cursor.moveToNext();
        assertEquals("Food", cursor.getString(columnId));
        cursor.moveToNext();
        assertEquals("Cheese", cursor.getString(columnId));

    }

    public void test2ItemsUpdated() throws Exception {
        assertEquals(2, updatedCount);
    }
}
