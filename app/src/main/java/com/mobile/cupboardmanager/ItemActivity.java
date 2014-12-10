package com.mobile.cupboardmanager;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Support the creation/review/deletion of shopping and cupboard items
 */
public class ItemActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "cupboardmanager.ItemActivity";

    /** Intent extra DBContentProvider CONTENT_URI **/
    public static final String INTENT_ITEM_URI = "item_uri";
    /** Intent extra Item _ID **/
    public static final String INTENT_ITEM_ID = "item_id";

    // Min-value for item quantity seek bar
    private static final int SEEK_OFFSET = 1;

    // Hold the value of the expiry date throughout the lifetime of the activity
    private boolean mIsEditMode;
    private DatePicker mDatePicker;
    private TextView mTextViewName;
    private Uri itemUri;
    private long mItemId;

    // OnSubmit we write the form-data to its representative object and save it to the db
    private View.OnClickListener mOnSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveData();
        }
    };

    private View.OnClickListener mOnCancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsEditMode) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_item, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.delete_item:
                getContentResolver().delete(ContentUris.withAppendedId(itemUri, mItemId), null, null);
                Toast.makeText(ItemActivity.this, getString(R.string.item_deleted),
                        Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity);

        mDatePicker = (DatePicker)findViewById(R.id.datePicker);

        // TODO: need to fix this up
        List<String> items = new ArrayList<String>();
        AutoCompleteItemAdapter autoCompleteItemAdapter = new AutoCompleteItemAdapter(this,
                R.layout.layout_auto_complete_item, items);
        AutoCompleteTextView autoCompleteTextView =
                (AutoCompleteTextView) findViewById(R.id.item_name);
        autoCompleteTextView.setAdapter(autoCompleteItemAdapter);

        Intent intent = getIntent();
        itemUri = intent.getExtras().getParcelable(INTENT_ITEM_URI);
        // Attempt to retrieve the item ID, i.e cupboard or shopping item
        mItemId = intent.getExtras().getLong(INTENT_ITEM_ID, -1);
        // If were passed an existing item ID then we must be in edit mode
        mIsEditMode = (mItemId != -1);

        String actionContext = (mIsEditMode ? getString(R.string.activity_item_title_edit) :
                getString(R.string.activity_item_title_create));
        StringBuilder activityTitleBuilder = new StringBuilder(actionContext);

        final Button submitBtn = (Button)findViewById(R.id.submit);
        submitBtn.setText(actionContext);
        submitBtn.setOnClickListener(mOnSubmitClickListener);

        findViewById(R.id.cancel).setOnClickListener(mOnCancelOnClickListener);

        mTextViewName = ((TextView) findViewById(R.id.item_name));
        SeekBar mSeekBarQuantity = ((SeekBar) findViewById(R.id.item_quantity_seek_bar));
        mSeekBarQuantity.setOnSeekBarChangeListener(this);

        if (itemUri.compareTo(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI) == 0) {
            activityTitleBuilder.append(getResources().getString(R.string.title_cupboard_item));
        } else {
            activityTitleBuilder.append(getResources().getString(R.string.title_shopping_item));
            // We don't need the expiry related view's for shopping items
            findViewById(R.id.item_expiry_date).setVisibility(View.GONE);
            findViewById(R.id.item_notify_me).setVisibility(View.GONE);
            findViewById(R.id.datePicker).setVisibility(View.GONE);
        }
        getActionBar().setTitle(activityTitleBuilder.toString());

        if (mIsEditMode) {
            fillData();
        } else {
            // Set the calender view to today
            initializeCalendarView(null);
        }
    }

    /**
     * Given the item id fetch the corresponding item name
     * @param id of the item
     * @return the string name for the item
     */
    private String getItemName(long id) {
        String name = null;
        Cursor c = getContentResolver().query(ContentUris.withAppendedId(
                        DBContentProvider.ITEMS.CONTENT_URI, id), null, null, null, null);
        if (c.moveToFirst()) {
            name = c.getString(c.getColumnIndex(DBContentProvider.ITEMS.Name));
        }
        c.close();
        return name;
    }

    /**
     * Load the data from the URI and _ID and fill the form
     */
    private void fillData() {
        Cursor c = getContentResolver().query(ContentUris.withAppendedId(itemUri, mItemId), null,
                null, null, null);
        if (c.moveToFirst()) {
            String itemName = getItemName(c.getLong(c.getColumnIndex(DatabaseHandler.SHOPPING_ITEM)));
            int itemQuantity = c.getInt(c.getColumnIndex(DatabaseHandler.SHOPPING_QUANTITY));

            mTextViewName.setText(itemName);
            setItemQuantity(itemQuantity);

            if (itemUri.compareTo(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI) == 0) {
                initializeCalendarView(c);
            }
            c.close();
        }
    }

    /**
     * Initialize the calendar view to the min value of the current-time and the expiry-time of the
     * Cupboard item
     * @param c cursor for the cupboard item
     */
    private void initializeCalendarView(Cursor c) {
        // We need to subtract 1 second here otherwise DatePicker will throw an exception
        // since the minimum date cannot be set to now
        long currentTimeMs = Calendar.getInstance().getTimeInMillis() - TimeUnit.SECONDS.toMillis(1);
        long itemExpiryMs = (c == null) ? 0 : c.getLong(c.getColumnIndex(DatabaseHandler.CUPBOARD_EXPIRY_TIME));
        if (itemExpiryMs > 0) {
            mDatePicker.setMinDate((itemExpiryMs < currentTimeMs) ? itemExpiryMs : currentTimeMs);
            mDatePicker.getCalendarView().setDate(itemExpiryMs);
        } else {
            mDatePicker.setMinDate(currentTimeMs);
        }
    }

    /**
     * This will query for an existing Item with matching itemName, otherwise it will create it
     * @param itemName the item name
     * @return the row ID of the existing/new item
     */
    private long saveItem(String itemName) {
        // Query for existence of Item with name equal to itemName
        Cursor c = getContentResolver().query(DBContentProvider.ITEMS.CONTENT_URI,
                new String[] {DBContentProvider.ITEMS._ID},
                DBContentProvider.ITEMS.Name + " ='" + itemName + "'", null, null);
        long itemId;
        // If the item already exits we grab its ID otherwise we create it and get its ID
        if (c.moveToFirst()) {
            itemId = c.getLong(c.getColumnIndex(DatabaseHandler.ITEM_ID));
        } else {
            ContentValues itemValues = new ContentValues();
            itemValues.put(DBContentProvider.ITEMS.Name, itemName);
            Uri uri = getContentResolver().insert(DBContentProvider.ITEMS.CONTENT_URI, itemValues);
            itemId = Long.parseLong(uri.getPathSegments().get(1));
        }
        c.close();
        return itemId;
    }

    /**
     * Write to the DB the state of the form
     */
    private void saveData() {
        String itemName = mTextViewName.getText().toString();
        if (itemName.isEmpty()) {
            Toast.makeText(ItemActivity.this, getString(R.string.item_name_empty_error),
                        Toast.LENGTH_SHORT).show();
            return;
        }
        final int itemQuantity = Integer.parseInt(((TextView) findViewById(R.id.item_quantity)).
                getText().toString());

        // Create/Query the item returning its ID
        long itemId = saveItem(itemName);
        // Save common data
        ContentValues itemValues = new ContentValues();
        itemValues.put(DBContentProvider.SHOPPING_ITEMS.Item, itemId);
        itemValues.put(DBContentProvider.SHOPPING_ITEMS.Quantity, itemQuantity);

        if (itemUri.compareTo(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI) == 0) {
            itemValues.put(DBContentProvider.CUPBOARD_ITEMS.Expiry_Time,
                    mDatePicker.getCalendarView().getDate());
        }

        if (mIsEditMode) {
            getContentResolver().update(ContentUris.withAppendedId(itemUri, mItemId), itemValues,
                    null, null);
        } else {
            getContentResolver().insert(itemUri, itemValues);
        }

        finish();
    }

    /**
     * Update the progress of the quantity seek bar and quantity textview
     * @param quantity the progress value
     */
    private void setItemQuantity(int quantity) {
        if (quantity > 0) {
            ((SeekBar)findViewById(R.id.item_quantity_seek_bar)).setProgress(quantity
                    - SEEK_OFFSET);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ((TextView) findViewById(R.id.item_quantity)).setText(Integer.toString(progress
                + SEEK_OFFSET));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
