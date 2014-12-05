package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class InsertShoppingItemsTest extends ItemsBaseTest {
    private Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues newValues = new ContentValues();
        newValues.put(DBContentProvider.SHOPPING_ITEMS.Item, 1);
        newValues.put(DBContentProvider.SHOPPING_ITEMS.Quantity, 2);

        provider.insert(DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, newValues);

        mCursor = provider.query(DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, null, null, null, null);
    }

    public void test1ItemsWereAddedto3AlreadyThere() throws Exception {

        assertEquals(4, mCursor.getCount());
    }

    public void testAllItemsAreCorrect() throws Exception {
        int itemIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Item);
        int quantityIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Quantity);

        mCursor.moveToNext();
        assertEquals(1, mCursor.getInt(itemIndex));
        assertEquals(0, mCursor.getInt(quantityIndex));
        mCursor.moveToNext();
        assertEquals(2, mCursor.getInt(itemIndex));
        assertEquals(1, mCursor.getInt(quantityIndex));
        mCursor.moveToNext();
        assertEquals(3, mCursor.getInt(itemIndex));
        assertEquals(2, mCursor.getInt(quantityIndex));
        mCursor.moveToNext();
        assertEquals(1, mCursor.getInt(itemIndex));
        assertEquals(2, mCursor.getInt(quantityIndex));
    }
}
