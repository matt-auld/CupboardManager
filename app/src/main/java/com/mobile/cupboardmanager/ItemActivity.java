package com.mobile.cupboardmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Support the creation/review/deletion of shopping and cupboard items
 */
public class ItemActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "cupboardmanager.ItemActivity";

    /** Intent extra name for item Object **/
    public static final String ITEM_INTENT_EXTRA = "item_object";
    /** Intent extra name for item Object type **/
    public static final String ITEM_TYPE_INTENT_EXTRA = "item_type";
    /** Standard Item types to be used for: ITEM_TYPE_INTENT_EXTRA **/
    public static final int ITEM_SHOPPING_TYPE = 666;
    public static final int ITEM_CUPBOARD_TYPE = 999;

    // Min-value for item quantity seek bar
    private static final int SEEK_OFFSET = 1;

    // Hold the value of the expiry date throughout the lifetime of the activity
    private boolean mIsEditMode;
    private int mObjectType;
    private ShoppingItem mShoppingItem;
    private CupboardItem mCupboardItem;
    private DatabaseHandler mDatabaseHandler;
    private DatePicker mDatePicker;

    // OnSubmit we write the form-data to its representative object and save it to the db
    private View.OnClickListener mOnSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String itemName = ((TextView)findViewById(R.id.item_name)).getText().toString().trim();
            if (itemName.isEmpty()) {
                Toast.makeText(ItemActivity.this, getString(R.string.item_name_empty_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            final int itemQuantity = Integer.parseInt(((TextView) findViewById(R.id.item_quantity)).
                    getText().toString());

            Item newItem = new Item(itemName);
            final DatabaseHandler databaseHandler = new DatabaseHandler(ItemActivity.this);
            databaseHandler.insertItem(newItem);

            if (mObjectType == ITEM_SHOPPING_TYPE) {
                mShoppingItem.setName(itemName);
                mShoppingItem.setQuantity(itemQuantity);
                if (mIsEditMode) {
                    databaseHandler.updateItem(mShoppingItem);
                } else {
                    databaseHandler.insertItem(mShoppingItem);
                }
            } else {
                mCupboardItem.setName(itemName);
                mCupboardItem.setQuantity(itemQuantity);
                mCupboardItem.setExpiry_time_ms(mDatePicker.getCalendarView().getDate());
                if (mIsEditMode) {
                    databaseHandler.updateItem(mCupboardItem);
                } else {
                    databaseHandler.insertItem(mCupboardItem);
                }
            }
            finish();
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
                if (mObjectType == ITEM_CUPBOARD_TYPE) {
                    mDatabaseHandler.deleteItem(mCupboardItem);
                } else {
                    mDatabaseHandler.deleteItem(mShoppingItem);
                }
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

        mDatabaseHandler = new DatabaseHandler(ItemActivity.this);
        mDatePicker = (DatePicker)findViewById(R.id.datePicker);

        SeekBar seekBar = ((SeekBar)findViewById(R.id.item_quantity_seek_bar));
        seekBar.setOnSeekBarChangeListener(this);

        Intent intent = getIntent();
        // Attempt to retrieve the item, i.e cupboard or shopping item
        final Object item = intent.getExtras().get(ITEM_INTENT_EXTRA);
        // If were passed an existing item object then we must be in edit mode
        mIsEditMode = (item != null);

        String actionContext = (mIsEditMode ? getString(R.string.activity_item_title_edit) :
                getString(R.string.activity_item_title_create));
        StringBuilder activityTitleBuilder = new StringBuilder(actionContext);

        mObjectType = intent.getExtras().getInt(ITEM_TYPE_INTENT_EXTRA);
        switch (mObjectType) {
            case ITEM_CUPBOARD_TYPE:
                if (mIsEditMode) {
                    mCupboardItem = (CupboardItem) item;
                } else {
                    mCupboardItem = new CupboardItem();
                }
                loadUIForItem(mCupboardItem);
                activityTitleBuilder.append(getString(R.string.title_cupboard_item));
                break;
            case ITEM_SHOPPING_TYPE:
                if (mIsEditMode) {
                    mShoppingItem = (ShoppingItem) item;
                } else {
                    mShoppingItem = new ShoppingItem();
                }
                loadUIForItem(mShoppingItem);
                activityTitleBuilder.append(getString(R.string.title_shopping_item));
                break;
            default:
                throw new RuntimeException("Unknown item type");
        }

        final Button submitBtn = (Button)findViewById(R.id.submit);
        submitBtn.setText(actionContext);
        submitBtn.setOnClickListener(mOnSubmitClickListener);

        findViewById(R.id.cancel).setOnClickListener(mOnCancelOnClickListener);

        getActionBar().setTitle(activityTitleBuilder.toString());
    }

    /**
     * Load the UI with the data from the item and set up the necessary event handlers
     * @param item the shopping item to be edited
     */
    private void loadUIForItem(ShoppingItem item) {
        // We don't need the expiry related view's for shopping items
        findViewById(R.id.item_expiry_date).setVisibility(View.GONE);
        findViewById(R.id.item_notify_me).setVisibility(View.GONE);
        findViewById(R.id.datePicker).setVisibility(View.GONE);

        // Load fields with object data
        ((TextView)findViewById(R.id.item_name)).setText(item.getName());
        setItemQuantity(item.getQuantity());
    }

    /**
     * Load the UI with the data from the item and set up the necessary event handlers
     * @param item the cupboard item to be edited
     */
    private void loadUIForItem(CupboardItem item) {
        // Load fields with object data
        ((TextView)findViewById(R.id.item_name)).setText(item.getName());

        // We need to subtract 1 second here otherwise DatePicker will throw an exception
        // since the minimum date cannot be set to now
        long currentTimeMs = Calendar.getInstance().getTimeInMillis() - TimeUnit.SECONDS.toMillis(1);
        long itemExpiryMs = item.getExpiry_time_ms();
        if (itemExpiryMs > 0) {
            mDatePicker.setMinDate((itemExpiryMs < currentTimeMs) ? itemExpiryMs : currentTimeMs);
            mDatePicker.getCalendarView().setDate(itemExpiryMs);
        } else {
            mDatePicker.setMinDate(currentTimeMs);
        }
        setItemQuantity(item.getQuantity());
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
