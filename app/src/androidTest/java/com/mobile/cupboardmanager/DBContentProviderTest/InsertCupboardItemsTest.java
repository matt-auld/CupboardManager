package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class InsertCupboardItemsTest extends ItemsBaseTest  {
    private Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues newValues = new ContentValues();
        newValues.put(DBContentProvider.CUPBOARD_ITEMS.Item, 1);
        newValues.put(DBContentProvider.CUPBOARD_ITEMS.Quantity, 2);
        newValues.put(DBContentProvider.CUPBOARD_ITEMS.Expiry_Time, 10000);

        provider.insert(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, newValues);

        mCursor = provider.query(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, null, null, null, null);
    }

    public void test1ItemsWereAddedto3AlreadyThere() throws Exception {

        assertEquals(4, mCursor.getCount());
    }

    public void testAllItemsAreCorrect() throws Exception {
        int itemIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Item);
        int quantityIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Quantity);
        int expiryTimeIndex = mCursor.getColumnIndex(DBContentProvider.CUPBOARD_ITEMS.Expiry_Time);

        mCursor.moveToNext();
        assertEquals(1, mCursor.getInt(itemIndex));
        assertEquals(0, mCursor.getInt(quantityIndex));
        assertEquals(0, mCursor.getInt(expiryTimeIndex));
        mCursor.moveToNext();
        assertEquals(2, mCursor.getInt(itemIndex));
        assertEquals(1, mCursor.getInt(quantityIndex));
        assertEquals(1000, mCursor.getInt(expiryTimeIndex));
        mCursor.moveToNext();
        assertEquals(3, mCursor.getInt(itemIndex));
        assertEquals(2, mCursor.getInt(quantityIndex));
        assertEquals(2000, mCursor.getInt(expiryTimeIndex));
        mCursor.moveToNext();
        assertEquals(1, mCursor.getInt(itemIndex));
        assertEquals(2, mCursor.getInt(quantityIndex));
        assertEquals(10000, mCursor.getInt(expiryTimeIndex));
    }
}
