package com.mobile.cupboardmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}

