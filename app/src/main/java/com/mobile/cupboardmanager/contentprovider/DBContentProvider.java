package com.mobile.cupboardmanager.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.mobile.cupboardmanager.DatabaseHandler;

import java.util.HashMap;

/**
 * Content Provider so we can use the database with a LoaderManager
 *
 * Created by Paul on 01/12/2014.
 */
public class DBContentProvider extends ContentProvider {

    // URI Identifiers
    public static final String AUTHORITY = "com.mobile.cupboardmanager.contentprovider";

    private static final int ITEM = 0;
    private static final int ITEM_ID = 1;

    private static final int SHOPPING_ITEM = 2;
    private static final int SHOPPING_ITEM_ID = 3;

    private static final int CUPBOARD_ITEM = 4;
    private static final int CUPBOARD_ITEM_ID = 5;

    private static final HashMap<String, String> mShoppingProjection = createProjectionMapShoppingJoin();
    private static final HashMap<String, String> mCupboardProjection = createProjectionMapCupboardJoin();

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        sURIMatcher.addURI(AUTHORITY, ITEMS.BASE_PATH, ITEM);
        sURIMatcher.addURI(AUTHORITY, ITEMS.BASE_PATH + "/#", ITEM_ID);

        sURIMatcher.addURI(AUTHORITY, SHOPPING_ITEMS.BASE_PATH, SHOPPING_ITEM);
        sURIMatcher.addURI(AUTHORITY, SHOPPING_ITEMS.BASE_PATH + "/#", SHOPPING_ITEM_ID);

        sURIMatcher.addURI(AUTHORITY, CUPBOARD_ITEMS.BASE_PATH, CUPBOARD_ITEM);
        sURIMatcher.addURI(AUTHORITY, CUPBOARD_ITEMS.BASE_PATH + "/#", CUPBOARD_ITEM_ID);
    }

    public static class ITEMS
    {
        private static final String BASE_PATH = "Items";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
        public static final String _ID = DatabaseHandler.ITEM_ID;
        public static final String Name = DatabaseHandler.ITEM_NAME;
    }

    public static class SHOPPING_ITEMS
    {
        private static final String BASE_PATH = "ShoppingItems";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
        public static final String _ID = DatabaseHandler.SHOPPING_ID;
        public static final String Item = DatabaseHandler.SHOPPING_ITEM;
        public static final String Quantity = DatabaseHandler.SHOPPING_QUANTITY;
    }

    public static class CUPBOARD_ITEMS
    {
        private static final String BASE_PATH = "CupboardItems";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
        public static final String _ID = DatabaseHandler.CUPBOARD_ID;
        public static final String Item = DatabaseHandler.CUPBOARD_ITEM;
        public static final String Expiry_Time = DatabaseHandler.CUPBOARD_EXPIRY_TIME;
        public static final String Notification_Id = DatabaseHandler.CUPBOARD_NOTIFICATION_ID;
        public static final String Quantity = DatabaseHandler.CUPBOARD_QUANTITY;
    }

    public DatabaseHandler getDBHandler() {
        return mDBHandler;
    }

    public void setDBHandler(DatabaseHandler mDBHandler) {
        this.mDBHandler = mDBHandler;
    }

    private DatabaseHandler mDBHandler;


    @Override
    public boolean onCreate() {
        mDBHandler = new DatabaseHandler(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Create a builder to query database
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);

        // Set up correct query depending on URI passed
        switch (uriType)
        {
            case ITEM:
                qBuilder.setTables(DatabaseHandler.ITEM_TABLE);
                break;
            case ITEM_ID:
                qBuilder.setTables(DatabaseHandler.ITEM_TABLE);
                qBuilder.appendWhere(DatabaseHandler.ITEM_ID + "=" + uri.getLastPathSegment());
                break;
            case SHOPPING_ITEM:
                qBuilder.setTables(DatabaseHandler.SHOPPING_TABLE + " LEFT OUTER JOIN "
                        + DatabaseHandler.ITEM_TABLE + " ON " + DatabaseHandler.SHOPPING_ITEM
                        + " = " + DatabaseHandler.ITEM_TABLE + "." + DatabaseHandler.ITEM_ID);
                qBuilder.setProjectionMap(mShoppingProjection);
                break;
            case SHOPPING_ITEM_ID:
                qBuilder.setTables(DatabaseHandler.SHOPPING_TABLE + " LEFT OUTER JOIN "
                        + DatabaseHandler.ITEM_TABLE + " ON " + DatabaseHandler.SHOPPING_ITEM
                        + " = " + DatabaseHandler.ITEM_TABLE + "." + DatabaseHandler.ITEM_ID);
                qBuilder.appendWhere(DatabaseHandler.SHOPPING_TABLE + "." +
                        DatabaseHandler.SHOPPING_ID + "=" + uri.getLastPathSegment());
                qBuilder.setProjectionMap(mShoppingProjection);
                break;
            case CUPBOARD_ITEM:
                qBuilder.setTables(DatabaseHandler.CUPBOARD_TABLE);
                qBuilder.setTables(DatabaseHandler.CUPBOARD_TABLE + " LEFT OUTER JOIN "
                        + DatabaseHandler.ITEM_TABLE + " ON " + DatabaseHandler.CUPBOARD_ITEM
                        + " = " + DatabaseHandler.ITEM_TABLE + "." + DatabaseHandler.ITEM_ID);
                qBuilder.setProjectionMap(mCupboardProjection);
                break;
            case CUPBOARD_ITEM_ID:
                qBuilder.setTables(DatabaseHandler.CUPBOARD_TABLE + " LEFT OUTER JOIN "
                        + DatabaseHandler.ITEM_TABLE + " ON " + DatabaseHandler.CUPBOARD_ITEM
                        + " = " + DatabaseHandler.ITEM_TABLE + "." + DatabaseHandler.ITEM_ID);
                qBuilder.appendWhere(DatabaseHandler.CUPBOARD_TABLE + "." +
                        DatabaseHandler.CUPBOARD_ID+ "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Get DB
        SQLiteDatabase db = mDBHandler.getReadableDatabase();

        // Process query
        Cursor cursor = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Create unambiguous projections for shopping query
     * This will allow table joins to be used
     * @return HashMap of projections
     */
    private static HashMap<String, String> createProjectionMapShoppingJoin() {
        HashMap<String, String> projectionMap = new HashMap<>();

        String itemTab = DatabaseHandler.ITEM_TABLE + ".";
        String shopTab = DatabaseHandler.SHOPPING_TABLE + ".";

        String shopID = shopTab + DatabaseHandler.SHOPPING_ID;
        String shopItem = shopTab + DatabaseHandler.SHOPPING_ITEM;
        String shopQuantity = shopTab + DatabaseHandler.SHOPPING_QUANTITY;

        String itemID = itemTab + DatabaseHandler.ITEM_ID;
        String itemName = itemTab + DatabaseHandler.ITEM_NAME;

        projectionMap.put(shopID, shopID);
        projectionMap.put(shopItem, shopItem);
        projectionMap.put(shopQuantity, shopQuantity);
        projectionMap.put(itemID, itemID + " AS item_ID");
        projectionMap.put(itemName, itemName);

        return projectionMap;
    }

    /**
     * Create unambiguous projections for cupboard query
     * This will allow table joins to be used
     * @return HashMap of projections
     */
    private static HashMap<String, String> createProjectionMapCupboardJoin() {
        HashMap<String, String> projectionMap = new HashMap<>();

        String itemTab = DatabaseHandler.ITEM_TABLE + ".";
        String shopTab = DatabaseHandler.CUPBOARD_TABLE + ".";

        String cupID = shopTab + DatabaseHandler.CUPBOARD_ID;
        String cupItem = shopTab + DatabaseHandler.CUPBOARD_ITEM;
        String cupQuantity = shopTab + DatabaseHandler.CUPBOARD_QUANTITY;
        String cupExpiry = shopTab + DatabaseHandler.CUPBOARD_EXPIRY_TIME;
        String cupNotID = shopTab + DatabaseHandler.CUPBOARD_NOTIFICATION_ID;

        String itemID = itemTab + DatabaseHandler.ITEM_ID;
        String itemName = itemTab + DatabaseHandler.ITEM_NAME;

        projectionMap.put(cupID, cupID);
        projectionMap.put(cupItem, cupItem);
        projectionMap.put(cupQuantity, cupQuantity);
        projectionMap.put(cupExpiry, cupExpiry);
        projectionMap.put(cupNotID, cupNotID);

        projectionMap.put(itemID, itemID + " AS item_ID");
        projectionMap.put(itemName, itemName);

        return projectionMap;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Get DB
        SQLiteDatabase db = mDBHandler.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);

        long id = 0;

        // Insert depending on URI passed
        switch (uriType)
        {
            case ITEM:
                id = db.insert(DatabaseHandler.ITEM_TABLE, null, contentValues);
                break;
            case SHOPPING_ITEM:
                id = db.insert(DatabaseHandler.SHOPPING_TABLE, null, contentValues);
                break;
            case CUPBOARD_ITEM:
                id = db.insert(DatabaseHandler.CUPBOARD_TABLE, null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        switch (uriType)
        {
            case ITEM:
                return Uri.parse(ITEMS.BASE_PATH + "/" + id);
            case SHOPPING_ITEM:
                return Uri.parse(SHOPPING_ITEMS.BASE_PATH + "/" + id);
            case CUPBOARD_ITEM:
                return Uri.parse(CUPBOARD_ITEMS.BASE_PATH + "/" + id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {

        // Get DB
        SQLiteDatabase db = mDBHandler.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);

        int deletedRows;

        // Insert depending on URI passed
        switch (uriType)
        {
            case ITEM:
                deletedRows = db.delete(DatabaseHandler.ITEM_TABLE, where, whereArgs);
                break;
            case SHOPPING_ITEM_ID:
                deletedRows = db.delete(DatabaseHandler.SHOPPING_TABLE,
                        DatabaseHandler.SHOPPING_ID + " =" + uri.getLastPathSegment(), whereArgs);
                break;
            case SHOPPING_ITEM:
                deletedRows = db.delete(DatabaseHandler.SHOPPING_TABLE, where, whereArgs);
                break;
            case CUPBOARD_ITEM_ID:
                deletedRows = db.delete(DatabaseHandler.CUPBOARD_TABLE,
                        DatabaseHandler.CUPBOARD_ID + " =" + uri.getLastPathSegment(), whereArgs);
                break;
            case CUPBOARD_ITEM:
                deletedRows = db.delete(DatabaseHandler.CUPBOARD_TABLE, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
        // Get DB
        SQLiteDatabase db = mDBHandler.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);

        int updatedRows;

        // Insert depending on URI passed
        switch (uriType)
        {
            case ITEM:
                updatedRows = db.update(DatabaseHandler.ITEM_TABLE, contentValues, where, whereArgs);
                break;
            case SHOPPING_ITEM_ID:
                updatedRows = db.update(DatabaseHandler.SHOPPING_TABLE, contentValues,
                        DatabaseHandler.SHOPPING_ID + " =" + uri.getLastPathSegment(), null);
                break;
            case SHOPPING_ITEM:
                updatedRows = db.update(DatabaseHandler.SHOPPING_TABLE, contentValues, where, whereArgs);
                break;
            case CUPBOARD_ITEM_ID:
                updatedRows = db.update(DatabaseHandler.CUPBOARD_TABLE, contentValues,
                        DatabaseHandler.CUPBOARD_ID + " =" + uri.getLastPathSegment(), null);
                break;
            case CUPBOARD_ITEM:
                updatedRows = db.update(DatabaseHandler.CUPBOARD_TABLE, contentValues, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return updatedRows;
    }
}
