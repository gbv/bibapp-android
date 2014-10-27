package de.eww.bibapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.info.InfoFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
* Created by christoph on 22.10.14.
*/
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActionBarActivity {

    /**
     * The {@link android.support.v4.widget.DrawerLayout} that represents the top-level content view,
     * separating the content and navigation drawer.
     */
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    /**
     * The {@link android.support.v7.widget.Toolbar} we will use as our action bar.
     */
    @InjectView(R.id.toolbar) Toolbar mToolbar;

    /**
     * The {@link android.widget.ListView} that containts the navigation drawer content.
     */
    @InjectView(R.id.left_drawer) ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    @InjectResource(R.array.top_navigation_array) String[] mTopNavigationTitles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Request additional window features
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        mTitle = mDrawerTitle = getTitle();

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mTopNavigationTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set the toolbar as our action bar
        setSupportActionBar(mToolbar);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions between
        // the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
            this,                   // host activitiy
            mDrawerLayout,          // DrawerLayout object
            //R.drawable.ic_drawer,   // new drawer image to replace 'Up' caret
            R.string.drawer_open,   // "open drawer" description for accessibility
            R.string.drawer_close   // "close drawer" description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Set default values for our preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id....).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Open the app preferences
                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:         // Search
                fragment = new InfoFragment();
                break;
            case 1:         // Account
                fragment = new InfoFragment();
                break;
            case 2:         // Watch list
                fragment = new InfoFragment();
                break;
            case 3:         // Info
                fragment = new InfoFragment();
                break;
            default:
                fragment = new InfoFragment();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Update selected item and title and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mTopNavigationTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
