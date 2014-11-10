package de.eww.bibapp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.DrawerItem;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.CustomDrawerAdapter;
import de.eww.bibapp.fragment.account.AccountFragment;
import de.eww.bibapp.fragment.info.InfoFragment;
import de.eww.bibapp.fragment.search.GVKSearchFragment;
import de.eww.bibapp.fragment.search.SearchFragment;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
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

    @InjectView(R.id.toolbar_spinner) Spinner mSpinner;

    private ActionBarDrawerToggle mDrawerToggle;
    private SpinnerAdapter mSpinnerAdapter;
    private AdapterView.OnItemSelectedListener mOnNavigationListener;

    private int mCurrentSpinnerIndex;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CustomDrawerAdapter mCustomDrawerAdapter;

    private List<DrawerItem> mNavigationItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Resources resources = getResources();

        // Set orientation
        boolean isLandscape = resources.getBoolean(R.bool.landscape);
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mTitle = mDrawerTitle = getTitle();
        mNavigationItems = new ArrayList<DrawerItem>();

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set up the drawer's list view with items and click listener
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_heading_general)));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_search), R.drawable.ic_action_search));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_account), R.drawable.ic_action_person));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_watchlist), R.drawable.ic_action_view_as_list));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_info), R.drawable.ic_action_about));
        mCustomDrawerAdapter = new CustomDrawerAdapter(this, R.layout.drawer_list_item, mNavigationItems);
        mDrawerList.setAdapter(mCustomDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Set the toolbar as our action bar
        setSupportActionBar(mToolbar);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions between
        // the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
            this,                       // host activitiy
            mDrawerLayout,              // DrawerLayout object
            mToolbar,                   // Toolbar
            R.string.drawer_open,        // "open drawer" description for accessibility
            R.string.drawer_close      // "close drawer" description for accessibility
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

        // SpinnerAdapter for search fragment
        mSpinnerAdapter = ArrayAdapter.createFromResource(
            getSupportActionBar().getThemedContext(),
            R.array.search_spinner_list,
            android.R.layout.simple_spinner_dropdown_item
        );
        mSpinner.setAdapter(mSpinnerAdapter);

        mOnNavigationListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSpinnerIndex = position;
                selectItem(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mSpinner.setOnItemSelectedListener(mOnNavigationListener);

        if (savedInstanceState == null) {
            selectItem(1);
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
            if (mNavigationItems.get(position).getItemHeading() == null) {
                selectItem(position);
            }
        }
    }

    private void selectItem(int position) {
        // determine spinner visibility
        boolean showSpinner = false;

        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 1:         // Search
                // show local or gvk search
                if (mCurrentSpinnerIndex == 0) {
                    fragment = new SearchFragment();
                } else {
                    fragment = new GVKSearchFragment();
                }

                showSpinner = true;
                break;
            case 2:         // Account
                fragment = new AccountFragment();
                break;
            case 3:         // Watch list
                fragment = new InfoFragment();
                break;
            case 4:         // Info
                fragment = new InfoFragment();
                break;
        }

        // show/hide spinner
        if (showSpinner) {
            mSpinner.setVisibility(View.VISIBLE);
        } else {
            mSpinner.setVisibility(View.GONE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Update selected item and title and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationItems.get(position).getItemName());
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
