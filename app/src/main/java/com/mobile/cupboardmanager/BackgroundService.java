package com.mobile.cupboardmanager;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.mobile.cupboardmanager.contentprovider.DBContentProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BackgroundService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //Toast.makeText(this, "service created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service started", Toast.LENGTH_LONG).show();
        // stopSelf(); one way to stop the service - calls the on-destroy method
        // if service is already created it won't call the on-create method again

        //final DatabaseHandler databaseHandler = new DatabaseHandler(BackgroundService.this);
        //databaseHandler.ItemsOutOfDate();

        List<String> items = getItemNames();

        /*List<String> test = new ArrayList<String>();
        test.add(0,"one");
        test.add(1,"two");
        test.add(2,"three");*/

        if (items.size()>0) {
            SendNotification.notify(this, "Cupboard Manager", 0,items);
        }

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private List getItemNames() {

        List<String> Items = new ArrayList<String>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String d = sdf.format(date);


        String name = null;
        Cursor c = getContentResolver().query(DBContentProvider.CUPBOARD_ITEMS.CONTENT_URI, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                long expire_date = c.getLong(c.getColumnIndex(DatabaseHandler.CUPBOARD_EXPIRY_TIME));
                String dateString= DateFormat.format("dd-MM-yyyy", new Date(expire_date)).toString();
                String notification_flag = c.getString(c.getColumnIndex(DatabaseHandler.CUPBOARD_NOTIFICATION_ID));
                //System.out.println("Current datestring =; "+ dateString);
                if (dateString.equals(d) && notification_flag != "0") {
                    String record = c.getString(c.getColumnIndex(DatabaseHandler.ITEM_NAME));
                    //System.out.println("Success =; ");
                    Items.add(record);
                }
            } while (c.moveToNext());
        }
        c.close();
        return Items;
    }

}
