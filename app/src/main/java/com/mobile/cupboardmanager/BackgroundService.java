package com.mobile.cupboardmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

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

        DatabaseHandler db = new DatabaseHandler(BackgroundService.this);
        List<String> items = db.ItemsOutOfDate();
        db.close();

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


}
