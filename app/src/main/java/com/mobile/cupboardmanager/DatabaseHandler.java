package com.mobile.cupboardmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *  Database helper-class implementing CRUD(Create, Read, Update, Delete)
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";

    public static final String DATABASE_NAME = "cupboard_manager_db";
    private static final int DATABASE_VERSION = 2;

    // Item table name
    public static final String ITEM_TABLE = "Item";
    // Item table column names
    public static final String ITEM_ID = "_id";
    public static final String ITEM_NAME = "name";
    private static final String ITEM_TAGS = "tags";

    // Shopping table name
    public static final String SHOPPING_TABLE = "shopping";
    // Shopping table columns
    public static final String SHOPPING_ID = "_id";
    public static final String SHOPPING_ITEM = "item_fk";
    public static final String SHOPPING_QUANTITY = "quantity";

    // Cupboard table name
    public static final String CUPBOARD_TABLE = "cupboard";
    // Cupboard table columns
    public static final String CUPBOARD_ID = "_id";
    public static final String CUPBOARD_ITEM = "item_fk";
    public static final String CUPBOARD_QUANTITY = "quantity";
    public static final String CUPBOARD_EXPIRY_TIME = "expiry_time_ms";
    public static final String CUPBOARD_NOTIFICATION_ID = "notification_id";

    private Context mContext;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    /**
     * Called when the database is created for the first time.
     * Creates each of our tables
     *
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_ITEM_TABLE = "CREATE TABLE " + ITEM_TABLE + "(" +
                ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ITEM_NAME + " TEXT, " +
                ITEM_TAGS + " TEXT" + ")";

        final String CREATE_SHOPPING_TABLE = "CREATE TABLE " + SHOPPING_TABLE + "(" +
                SHOPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SHOPPING_ITEM + " INTEGER, " +
                SHOPPING_QUANTITY + " INTEGER, FOREIGN KEY(" + SHOPPING_ITEM + ") REFERENCES " +
                ITEM_TABLE + "(" + ITEM_ID + ")" + ")";

        final String CREATE_CUPBOARD_TABLE = "CREATE TABLE " + CUPBOARD_TABLE + "(" +
                CUPBOARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SHOPPING_ITEM + " INTEGER, " +
                CUPBOARD_QUANTITY + " INTEGER, " + CUPBOARD_EXPIRY_TIME + " INTEGER, " + CUPBOARD_NOTIFICATION_ID +
                " INTEGER" + CUPBOARD_ITEM + " INTEGER, FOREIGN KEY (" + CUPBOARD_ITEM + ") REFERENCES " +
                ITEM_TABLE + "(" + ITEM_ID + ")" + ")";

        db.execSQL(CREATE_ITEM_TABLE);
        db.execSQL(CREATE_SHOPPING_TABLE);
        db.execSQL(CREATE_CUPBOARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    //insert item into database
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, item.getName());

        long item_id = db.insert(ITEM_TABLE, null, values);

        return item_id;
    }

    //insert shopping item into database
    public long insertItem(ShoppingItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        // We need to find the ID for the item in the items table

        // Create a builder to query database
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(ITEM_TABLE);
        qBuilder.appendWhere(ITEM_NAME + "=\"" + shoppingItem.getName() + "\"");
        String[] projection = {ITEM_ID};

        Cursor cursor = qBuilder.query(db, projection, null, null, null, null, null);


        if (cursor.getCount() < 1) {
            throw new NoSuchElementException("No item found in the Items table with the name of: " +
                    shoppingItem.getName() + "\" when trying to insert a shopping item");
        } else if (cursor.getCount() > 1) {
            Log.w(TAG, "Multiple items found for name: " + shoppingItem.getName() +
                    "\" when trying to insert a shopping item");
        }

        cursor.moveToNext();
        int idIndex = cursor.getColumnIndex(ITEM_ID);

        int itemID = cursor.getInt(idIndex);

        ContentValues values = new ContentValues();
        values.put(SHOPPING_QUANTITY, shoppingItem.getQuantity());
        values.put(SHOPPING_ITEM, itemID);

        long shopping_item_id = db.insert(SHOPPING_TABLE, null, values);

        return shopping_item_id;
    }

    //fetch all shopping items, as a list
    public List<ShoppingItem> fetchAllShoppingItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<ShoppingItem> shoppingItems = new ArrayList<ShoppingItem>();

        String selectQuery = "SELECT  * FROM " + SHOPPING_TABLE;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                ShoppingItem item = new ShoppingItem();
                item.setQuantity(c.getInt((c.getColumnIndex(SHOPPING_QUANTITY))));
                item.setName((c.getString(c.getColumnIndex(SHOPPING_ITEM))));
                shoppingItems.add(item);
            } while (c.moveToNext());
        }

        return shoppingItems;
    }

    //insert shopping item into database
    public long insertItem(CupboardItem cupboardItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        // We need to find the ID for the item in the items table

        // Create a builder to query database
        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(ITEM_TABLE);
        qBuilder.appendWhere(ITEM_NAME + "=\"" + cupboardItem.getName() + "\"");
        String[] projection = {ITEM_ID};

        Cursor cursor = qBuilder.query(db, projection, null, null, null, null, null);

        if (cursor.getCount() < 1) {
            throw new NoSuchElementException("No item found in the Items table with the name of: \"" +
                    cupboardItem.getName() + "\" when trying to insert a cupboard item");
        } else if (cursor.getCount() > 1) {
            Log.w(TAG, "Multiple items found for name: " + cupboardItem.getName() +
                    "\" when trying to insert a cupboard item");
        }

        cursor.moveToNext();
        int idIndex = cursor.getColumnIndex(ITEM_ID);

        int itemID = cursor.getInt(idIndex);

        ContentValues values = new ContentValues();
        values.put(CUPBOARD_ITEM, itemID);
        values.put(CUPBOARD_QUANTITY, cupboardItem.getQuantity());
        values.put(CUPBOARD_EXPIRY_TIME, cupboardItem.getExpiry_time_ms());
        values.put(CUPBOARD_NOTIFICATION_ID, cupboardItem.getNotification_id());

        long cupboard_item_id = db.insert(CUPBOARD_TABLE, null, values);

        return cupboard_item_id;
    }
}

