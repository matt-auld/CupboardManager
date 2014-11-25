package com.mobile.cupboardmanager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    /**
     * Create a DatabaseHandler instance testing if we can open it
     * @throws Throwable
     */
    public void testCreateDB() throws Throwable {
        mContext.deleteDatabase(DatabaseHandler.DATABASE_NAME);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        SQLiteDatabase database = databaseHandler.getWritableDatabase();
        assertEquals(true, database.isOpen());
        database.close();
    }
}