package com.mobile.cupboardmanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.util.Calendar;
import java.util.NoSuchElementException;


public class ShoppingReminderActivity extends Activity {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText eventEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_reminder);

        // Get event name edit text
        eventEditText = (EditText) findViewById(R.id.reminderName);

        // Get pointer to date and time pickers
        datePicker = (DatePicker) findViewById(R.id.reminder_date_picker);
        timePicker = (TimePicker) findViewById(R.id.reminder_time_picker);

        // Set default values for time and date as this hour the next day
        final Calendar c = Calendar.getInstance();
        timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(0);


        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        datePicker.updateDate(tomorrow.get(Calendar.YEAR), tomorrow.get(Calendar.MONTH), tomorrow.get(Calendar.DAY_OF_MONTH));

        // Set OnClick function
        findViewById(R.id.add_shopping_reminder_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddShoppingReminder();
            }
        });
    }

    private void onAddShoppingReminder() {
        // Get start time in milli seconds using Calendar
        Calendar startDate = Calendar.getInstance();

        startDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);

        long eventStartInMillis = startDate.getTimeInMillis();

        startDate.add(Calendar.MINUTE, 30);

        long eventEndInMillis = startDate.getTimeInMillis();

        String eventTitle = eventEditText.getText().toString();

        // Create intent to add event to calendar
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, eventTitle);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, getShoppingList());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStartInMillis);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventEndInMillis);
        startActivity(intent);
    }

    private String getShoppingList()
    {
        String s = "Shopping list: " ;

        // Construct cursor containing results from ContentProvider
        Cursor mCursor = getContentResolver().query(
                DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, null, null, null, null);

        int quantityIndex = mCursor.getColumnIndex(DBContentProvider.SHOPPING_ITEMS.Quantity);
        int nameIndex = mCursor.getColumnIndex(DBContentProvider.ITEMS.Name);

        if (mCursor.moveToNext())
        {
            s += mCursor.getString(nameIndex) + " x" + mCursor.getString(quantityIndex);
        }
        else
        {
            s+= "Nothing ...";
        }

        // Get items from cursor
        while (mCursor.moveToNext()) {
            s += ", " + mCursor.getString(nameIndex) + " x" + mCursor.getString(quantityIndex);
        }

        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
