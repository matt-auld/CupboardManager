package com.mobile.cupboardmanager.DBContentProviderTest;

import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 01/12/2014.
 */
public class QueryItemsTest extends ItemsBaseTest {

    protected Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mCursor = provider.query(DBContentProvider.ITEMS.CONTENT_URI,
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
        int columnIndex = mCursor.getColumnIndex(DBContentProvider.ITEMS.Name);

        mCursor.moveToNext();
        assertEquals("Banana", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Bread", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Cheese", mCursor.getString(columnIndex));
    }
}
