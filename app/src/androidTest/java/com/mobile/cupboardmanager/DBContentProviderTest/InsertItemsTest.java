package com.mobile.cupboardmanager.DBContentProviderTest;

import android.content.ContentValues;
import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class InsertItemsTest extends ItemsBaseTest {

    private Cursor mCursor;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues newValues = new ContentValues();
        newValues.put(DBContentProvider.ITEMS.Name, "Apple");

        provider.insert(DBContentProvider.ITEMS.CONTENT_URI, newValues);


        newValues = new ContentValues();
        newValues.put(DBContentProvider.ITEMS.Name, "Milk");
        provider.insert(DBContentProvider.ITEMS.CONTENT_URI, newValues);

        mCursor = provider.query(DBContentProvider.ITEMS.CONTENT_URI, null, null, null, null);
    }

    public void test2ItemsWereAddedto3AlreadyThere() throws Exception {

        assertEquals(5, mCursor.getCount());
    }

    public void testAllItemsAreCorrect() throws Exception {
        int columnIndex = mCursor.getColumnIndex(DBContentProvider.ITEMS.Name);

        mCursor.moveToNext();
        assertEquals("Banana", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Bread", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Cheese", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Apple", mCursor.getString(columnIndex));
        mCursor.moveToNext();
        assertEquals("Milk", mCursor.getString(columnIndex));
    }
}