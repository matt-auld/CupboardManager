package com.mobile.cupboardmanager;

import com.astuetz.PagerSlidingTabStrip;
import com.mobile.cupboardmanager.TabsPagerAdapter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        // If there exists a pager view then we must have loaded the phone layout
        if (pager != null) {
            pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
            // Bind the tabs to the ViewPager
            PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabs.setViewPager(pager);
        } else {
            ShoppingFragment shoppingFragment = new ShoppingFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.shopping_list_container,
                    shoppingFragment).commit();

            CupboardFragment cupboardFragment = new CupboardFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.cupboard_list_container,
                    cupboardFragment).commit();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    }*/
}
