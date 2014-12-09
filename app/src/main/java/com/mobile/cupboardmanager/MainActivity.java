package com.mobile.cupboardmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.astuetz.PagerSlidingTabStrip;
import com.mobile.cupboardmanager.contentprovider.DBContentProvider;


public class MainActivity extends FragmentActivity {

    ShareActionProvider mShareActionProvider;

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
        Intent intent = new Intent(this, RunService.class);
        sendBroadcast(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem shareItem = menu.findItem(R.id.menu_item_share);

        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        }

        setShareIntent();

        return true;
    }

    //shares the shopping list as a plaintext list of item names
    private void setShareIntent() {

        if (mShareActionProvider != null) {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shopping List");

            //TODO: setShareIntent() whenever the shopping list changes
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            String s = "";

            //construct cursor containing results from contentprovider
            Cursor mCursor = getContentResolver().query(
                    DBContentProvider.SHOPPING_ITEMS.CONTENT_URI, null, null, null, null);

            //loop through cursor
            while (mCursor.moveToNext()) {
                s += mCursor.getString(1) + "\n";
            }

            shareIntent.putExtra(Intent.EXTRA_TEXT, s);
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    /*
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
