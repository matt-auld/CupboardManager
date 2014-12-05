package com.mobile.cupboardmanager.DBContentProviderTest;

import android.database.Cursor;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

/**
 * Created by Paul on 02/12/2014.
 */
public class DeleteCupboardItemsTest extends ItemsBaseTest {
    private int rowsDeleted;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        rowsDeleted = provider.delete(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI,
                DBContentProvider.CUPBOARD_ITEMS.Quantity + " > 1", null);
    }

    public void test1RowShouldHaveBeenDeleted() throws Exception {
        assertEquals(1, rowsDeleted);
    }

    public void testResultShouldHave2Records() throws Exception {
        Cursor cursor = provider.query(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI,
                null, null, null, null);

        assertEquals(2, cursor.getCount());
    }

    public void testRecordShouldBeCheese() throws Exception {
        Cursor cursor = provider.query(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI,
                null, null, null, null);


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
        assertEquals(1, cursor.getInt(quantityIndex));
    }
}
