package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class UpdateCupboardItemsTest extends ItemsBaseTest {
    private int updatedCount;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues newValues = new ContentValues();
        newValues.put(DBContentProvider.CUPBOARD_ITEMS.Quantity, 10);
        updatedCount = provider.update(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, newValues,
                DBContentProvider.CUPBOARD_ITEMS.Quantity + " >= 1", null);
    }

    public void testItemsUpdatedCorrectly() throws Exception {

        Cursor cursor = provider.query(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, null, null, null, null);

        int idIndex = cursor.getColumnIndex(DBContentProvider.CUPBOARD_ITEMS._ID);
        int itemIndex = cursor.getColumnIndex(DBContentProvider.CUPBOARD_ITEMS.Item);
        int quantityIndex = cursor.getColumnIndex(DBContentProvider.CUPBOARD_ITEMS.Quantity);

        cursor.moveToNext();
        assertEquals(1, cursor.getInt(idIndex));
        assertEquals(1, cursor.getInt(itemIndex));
        assertEquals(0, cursor.getInt(quantityIndex));
        cursor.moveToNext();
        assertEquals(2, cursor.getInt(idIndex));
        assertEquals(2, cursor.getInt(itemIndex));
        assertEquals(10, cursor.getInt(quantityIndex));
        cursor.moveToNext();
        assertEquals(3, cursor.getInt(idIndex));
        assertEquals(3, cursor.getInt(itemIndex));
        assertEquals(10, cursor.getInt(quantityIndex));

    }

    public void test2ItemsUpdated() throws Exception {
        assertEquals(2, updatedCount);
    }
}
