package com.mobile.cupboardmanager.DBContentProviderTest;

import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class QueryShoppingItemsTest extends ItemsBaseTest {

    protected Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mCursor = provider.query(DBContentProvider.SHOPPING_ITEMS.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public void testCursorIsNotNull() throws Exception {
        assertNotNull(mCursor);
    }

    public void testResultsCountIs3() throws Exception {
        assertEquals(3, mCursor.getCount());
    }

    public void testResultsReturnedAreCorrect() throws Exception {
        int idIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS._ID);
        int itemIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Item);
        int quantityIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Quantity);

        mCursor.moveToNext();
        assertEquals(1, mCursor.getInt(idIndex));
        assertEquals(1, mCursor.getInt(itemIndex));
        assertEquals(0, mCursor.getInt(quantityIndex));
        mCursor.moveToNext();
        assertEquals(2, mCursor.getInt(idIndex));
        assertEquals(2, mCursor.getInt(itemIndex));
        assertEquals(1, mCursor.getInt(quantityIndex));
        mCursor.moveToNext();
        assertEquals(3, mCursor.getInt(idIndex));
        assertEquals(3, mCursor.getInt(itemIndex));
        assertEquals(2, mCursor.getInt(quantityIndex));
    }
}
