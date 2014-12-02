package com.mobile.cupboardmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 *  Database helper-class implementing CRUD(Create, Read, Update, Delete)
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cupboard_manager_db";
    private static final int DATABASE_VERSION = 1;

    // Item table name
    private static final String ITEM_TABLE = "Item";
    // Item table column names
    private static final String ITEM_ID = "_ID";
    private static final String ITEM_TAGS = "tags";

    // Shopping table name
    private static final String SHOPPING_TABLE = "shopping";
    // Shopping table columns
    private static final String SHOPPING_ID = "_ID";
    private static final String SHOPPING_ITEM = "item_fk";
    private static final String SHOPPING_QUANTITY = "quantity";

    // Cupboard table name
    private static final String CUPBOARD_TABLE = "cupboard";
    // Cupboard table columns
    private static final String CUPBOARD_ID = "_ID";
    private static final String CUPBOARD_ITEM = "item_fk";
    private static final String CUPBOARD_QUANTITY = "quantity";
    private static final String CUPBOARD_EXPIRY_TIME = "expiry_time_ms";
    private static final String CUPBOARD_NOTIFICATION_ID = "notification_id";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     * Creates each of our tables
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_ITEM_TABLE = "CREATE TABLE " + ITEM_TABLE + "(" +
                ITEM_ID + " TEXT PRIMARY KEY, " +
                ITEM_TAGS + " TEXT" + ")";

        final String CREATE_SHOPPING_TABLE = "CREATE TABLE " + SHOPPING_TABLE + "(" +
                SHOPPING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SHOPPING_ITEM + " INTEGER, " +
                SHOPPING_QUANTITY + " INTEGER, FOREIGN KEY(" + SHOPPING_ITEM + ") REFERENCES " +
                ITEM_TABLE + "(" + ITEM_ID + ")" + ")";

        final String CREATE_CUPBOARD_TABLE = "CREATE TABLE " + CUPBOARD_TABLE + "(" +
                CUPBOARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + SHOPPING_ITEM + " INTEGER, " +
                CUPBOARD_QUANTITY + " INTEGER, " + CUPBOARD_EXPIRY_TIME + " INTEGER, " + CUPBOARD_NOTIFICATION_ID +
                " INTEGER" + CUPBOARD_ITEM + " INTEGER, FOREIGN KEY (" + CUPBOARD_ITEM + ") REFERENCES " +
                ITEM_TABLE + "(" + ITEM_ID + ")" + ")";

        db.execSQL(CREATE_ITEM_TABLE);
        db.execSQL(CREATE_SHOPPING_TABLE);
        db.execSQL(CREATE_CUPBOARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //insert item into database
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_ID, item.getName());

        long item_id = db.insert(ITEM_TABLE, null, values);

        return item_id;
    }

    //fetch single item from database, given an item name
    public Item fetchItem(String item_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + ITEM_TABLE + " WHERE "
                + ITEM_ID + " = " + item_name;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
            //sensible thing to do if c is null?
        }
        c.moveToFirst();

        Item item = new Item();
        item.setName(c.getString(c.getColumnIndex(ITEM_ID)));

        return item;
    }

    //update item in database. returns number of updated rows. not sure this even makes sense
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_ID, item.getName());

        return db.update(ITEM_TABLE, values, ITEM_ID + " = ?", new String[] {item.getName()});
    }

    //delete item from database. returns number of deleted rows
    public int deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ITEM_TABLE, ITEM_ID + " = ?", new String[] {item.getName()});
    }

    //insert shopping item into database
    public long insertItem(ShoppingItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SHOPPING_QUANTITY, shoppingItem.getQuantity());
        values.put(SHOPPING_ITEM, shoppingItem.getName());

        long shopping_item_id = db.insert(SHOPPING_TABLE, null, values);

        return shopping_item_id;
    }

    //fetch shopping item from database, using the name (foreign key from item)
    public ShoppingItem fetchShoppingItem(String shopping_item_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + SHOPPING_TABLE + " WHERE "
                + SHOPPING_ITEM + " = " + shopping_item_name;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
            //sensible thing to do if c is null?
        }
        c.moveToFirst();

        ShoppingItem item = new ShoppingItem();
        item.setName(c.getString(c.getColumnIndex(ITEM_ID)));
        item.setQuantity(c.getInt(c.getColumnIndex(CUPBOARD_QUANTITY)));

        return item;
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

                // adding to todo list
                shoppingItems.add(item);
            } while (c.moveToNext());
        }

        return shoppingItems;
    }

    //update shopping item in database. returns number of updated rows. not sure this even makes sense
    public int updateItem(ShoppingItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SHOPPING_ITEM, shoppingItem.getName());
        values.put(SHOPPING_QUANTITY, shoppingItem.getQuantity());

        return db.update(SHOPPING_TABLE, values, SHOPPING_ITEM + " = ?", new String[] {shoppingItem.getName()});
    }

    //delete shoppingitem from database. returns number of deleted rows
    public int deleteItem(ShoppingItem shoppingItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(SHOPPING_TABLE, SHOPPING_ITEM + " = ?", new String[] {shoppingItem.getName()});
    }

    //insert shopping item into database
    public long insertItem(CupboardItem cupboardItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CUPBOARD_ITEM, cupboardItem.getName());
        values.put(CUPBOARD_QUANTITY, cupboardItem.getQuantity());
        values.put(CUPBOARD_EXPIRY_TIME, cupboardItem.getExpiry_time_ms());
        values.put(CUPBOARD_NOTIFICATION_ID, cupboardItem.getNotification_id());

        long cupboard_item_id = db.insert(CUPBOARD_TABLE, null, values);

        return cupboard_item_id;
    }

    //fetch cupboard item from database, using the name (foreign key from item)
    public CupboardItem fetchCupboardItem(String cupboard_item_name) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + CUPBOARD_TABLE + " WHERE "
                + CUPBOARD_ITEM+ " = " + cupboard_item_name;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c == null) {
            return null;
            //sensible thing to do if c is null?
        }
        c.moveToFirst();

        CupboardItem item = new CupboardItem();
        item.setName(c.getString(c.getColumnIndex(CUPBOARD_ITEM)));
        item.setQuantity(c.getInt(c.getColumnIndex(CUPBOARD_QUANTITY)));
        item.setExpiry_time_ms(c.getLong(c.getColumnIndex(CUPBOARD_EXPIRY_TIME)));
        item.setNotification_id(c.getInt(c.getColumnIndex(CUPBOARD_NOTIFICATION_ID)));

        return item;
    }

    //fetch all cupboard items, as a list
    public List<CupboardItem> fetchAllCupboardItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CupboardItem> cupboardItems = new ArrayList<CupboardItem>();

        String selectQuery = "SELECT  * FROM " + CUPBOARD_TABLE;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                CupboardItem item = new CupboardItem();
                item.setName((c.getString(c.getColumnIndex(CUPBOARD_ITEM))));
                item.setQuantity(c.getInt((c.getColumnIndex(CUPBOARD_QUANTITY))));
                item.setExpiry_time_ms(c.getLong(c.getColumnIndex(CUPBOARD_EXPIRY_TIME)));
                item.setNotification_id(c.getInt(c.getColumnIndex(CUPBOARD_NOTIFICATION_ID)));

                // adding to todo list
                cupboardItems.add(item);
            } while (c.moveToNext());
        }

        return cupboardItems;
    }

    //update cupboard item
    public int updateItem(CupboardItem cupboardItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CUPBOARD_ITEM, cupboardItem.getName());
        values.put(CUPBOARD_QUANTITY, cupboardItem.getQuantity());
        values.put(CUPBOARD_EXPIRY_TIME, cupboardItem.getExpiry_time_ms());
        values.put(CUPBOARD_NOTIFICATION_ID, cupboardItem.getNotification_id());

        return db.update(SHOPPING_TABLE, values, SHOPPING_ITEM + " = ?", new String[] {cupboardItem.getName()});
    }

    //delete cupboard item from database. returns number of deleted rows
    public int deleteItem(CupboardItem cupboardItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CUPBOARD_TABLE, CUPBOARD_ITEM + " = ?", new String[] {cupboardItem.getName()});
    }
}

