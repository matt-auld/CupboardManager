package com.mobile.cupboardmanager.DBContentProviderTest;

import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class DeleteItemsTest extends ItemsBaseTest{

    private int rowsDeleted;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        rowsDeleted = provider.delete(DBContentProvider.ITEMS.CONTENT_URI,
                DBContentProvider.ITEMS.Name + " LIKE 'B%'", null);
    }

    public void test2RowsShouldHaveBeenDeleted() throws Exception {
        assertEquals(2, rowsDeleted);
    }

    public void testResultShouldOnlyHave1Record() throws Exception {
        Cursor cursor = provider.query(DBContentProvider.ITEMS.CONTENT_URI,
                null, null, null, null);

        assertEquals(1, cursor.getCount());
    }

    public void testTheOnlyResultRecordShouldBeCheese() throws Exception {
        Cursor cursor = provider.query(DBContentProvider.ITEMS.CONTENT_URI,
                null, null, null, null);

        int columnId = cursor.getColumnIndex(DBContentProvider.ITEMS.Name);

        cursor.moveToNext();
        assertEquals("Cheese", cursor.getString(columnId));
    }
}
