package de.eww.bibapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.CustomDrawerAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DrawerItem;
import roboguice.activity.RoboActionBarActivity;

/**
 * Created by christoph on 10.11.14.
 */
public class BaseActivity extends RoboActionBarActivity {

    /**
     * The {@link android.support.v4.widget.DrawerLayout} that represents the top-level content view,
     * separating the content and navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    private FrameLayout mFrameLayout;

    /**
     * The {@link android.support.v7.widget.Toolbar} we will use as our action bar.
     */
    Toolbar mToolbar;

    Spinner mSpinner;

    /**
     * The {@link android.widget.ListView} that containts the navigation drawer content.
     */
    ListView mDrawerList;

    LinearLayout mDrawerContainer;
    TextView mVersionView;

    private SpinnerAdapter mSpinnerAdapter;
    private AdapterView.OnItemSelectedListener mOnNavigationListener;

    private int mCurrentSpinnerIndex = 0;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CustomDrawerAdapter mCustomDrawerAdapter;
    protected List<DrawerItem> mNavigationItems;

    private ActionBarDrawerToggle mDrawerToggle;
    protected static int mCurrentNavigationIndex = -1;

    // symbols for navigation
    protected static final int NAV_ITEM_SEARCH = 0;
    protected static final int NAV_ITEM_ACCOUNT = 1;
    protected static final int NAV_ITEM_WATCHLIST = 2;
    protected static final int NAV_ITEM_INFO = 3;
    // index 4 is empty
    protected static final int NAV_ITEM_WEBSITE = 5;
    protected static final int NAV_ITEM_SETTINGS = 6;

    public static BaseActivity instance;
    private boolean mForceSelectSearch = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle = mDrawerTitle = getTitle();
        mNavigationItems = new ArrayList<DrawerItem>();

        // force portrait mode on non large devices
        Resources resources = getResources();
        boolean isLandscape = resources.getBoolean(R.bool.landscape);
        if (!isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Set default values for our preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void setContentView(int layoutResId) {
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        mFrameLayout = (FrameLayout) mDrawerLayout.findViewById(R.id.content_frame);
        mToolbar = (Toolbar) mDrawerLayout.findViewById(R.id.toolbar);
        mSpinner = (Spinner) mDrawerLayout.findViewById(R.id.toolbar_spinner);
        mDrawerContainer = (LinearLayout) mDrawerLayout.findViewById(R.id.drawer_container);
        mDrawerList = (ListView) mDrawerLayout.findViewById(R.id.drawer_list);
        mVersionView = (TextView) mDrawerLayout.findViewById(R.id.drawer_version);

        setupNavigation();

        if (mCurrentNavigationIndex == BaseActivity.NAV_ITEM_SEARCH) {
            // setup the search spinner
            setupSearchSpinner();
            mSpinner.setVisibility(View.VISIBLE);
        } else {
            mSpinner.setVisibility(View.GONE);
        }

        getLayoutInflater().inflate(layoutResId, mFrameLayout, true);
        super.setContentView(mDrawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The click listener for ListView in the navigation drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mNavigationItems.get(position).getItemHeading() == null) {
                selectItem(position);
            } else {
                // If we select a heading, keep the previous selection intact
                setActiveNavigationItem(mCurrentNavigationIndex);
            }
        }
    }

    public void selectItem(int position) {
        if (position == mCurrentNavigationIndex && position != 0) {
            return;
        }

        mSpinner.setVisibility(View.GONE);

        setActiveNavigationItem(position);

        Intent intent;

        switch (position) {
            case NAV_ITEM_SEARCH:
                // setup the search spinner
                setupSearchSpinner();
                mSpinner.setVisibility(View.VISIBLE);

                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAV_ITEM_ACCOUNT:
                intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAV_ITEM_WATCHLIST:
                intent = new Intent(this, WatchlistActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAV_ITEM_INFO:
                intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case NAV_ITEM_WEBSITE:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
                int localCatalogIndex = 0;
                if (!localCatalogPreference.isEmpty()) {
                    localCatalogIndex = Integer.valueOf(localCatalogPreference);
                }

                Uri homepageUrl = Uri.parse(Constants.HOMEPAGE_URLS[localCatalogIndex]);
                intent = new Intent(Intent.ACTION_VIEW, homepageUrl);
                startActivity(intent);
                finish();
                break;
            case NAV_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerContainer);
    }

    protected void setActiveNavigationItem(int position) {
        // Update selected item and title
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavigationItems.get(position).getItemName());

        mCurrentNavigationIndex = position;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
        getSupportActionBar().setSubtitle("");
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

    public void showToolbar(boolean showToolbar) {
        if (showToolbar) {
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }

    private void setupSearchSpinner() {
        // Do not setup twice
        if (mSpinner.getAdapter() != null) {
            return;
        }

        Resources resources = getResources();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

        // SpinnerAdapter for search fragment
        String[] searchSpinnerList = resources.getStringArray(R.array.search_spinner_list);

        // If our current local catalog contains a short title, we append it to the default title
        if (Constants.LOCAL_CATALOGS[localCatalogIndex].length > 2) {
            searchSpinnerList[0] += " " + Constants.LOCAL_CATALOGS[localCatalogIndex][2];
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getSupportActionBar().getThemedContext(),
                R.layout.support_simple_spinner_dropdown_item,
                searchSpinnerList
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAdapter = spinnerArrayAdapter;

        // It's important to follow this exact sequence:
        // .setAdapter
        // .setSelection
        // .setOnItemSelectedListener
        //
        // setSelection(..., false) will set the initial selection before the listener kicks in.
        // This way, the selection is set without animation which causes the listener to be called.
        // But the listener is null so it will not run.
        // This prevents the listener from firing on the initial state.
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(mCurrentSpinnerIndex, false);
        mOnNavigationListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentSpinnerIndex = position;

                // local or gvk search
                if (mCurrentSpinnerIndex == 0) {
                    ((SearchActivity) BaseActivity.this).setSearchMode(SearchActivity.SEARCH_MODE.LOCAL);
                } else {
                    ((SearchActivity) BaseActivity.this).setSearchMode(SearchActivity.SEARCH_MODE.GVK);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mSpinner.setOnItemSelectedListener(mOnNavigationListener);
    }

    private void setupNavigation() {
        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        Resources resources = getResources();

        // Set up the drawer's list view with items and click listener
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_search), R.drawable.ic_action_search));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_account), R.drawable.ic_action_person));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_watchlist), R.drawable.ic_action_view_as_list));
        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_info), R.drawable.ic_action_about));
        mNavigationItems.add(new DrawerItem(""));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

        if (Constants.HOMEPAGE_URLS.length >= localCatalogIndex + 1) {
            mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_homepage), R.drawable.ic_action_web_site));
        }

        mNavigationItems.add(new DrawerItem(resources.getString(R.string.drawer_navigation_settings), R.drawable.ic_action_settings));

        mCustomDrawerAdapter = new CustomDrawerAdapter(this, R.layout.drawer_list_item, mNavigationItems);
        mDrawerList.setAdapter(mCustomDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // Get the version name and set it in the layout
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionView.setText("v" + packageInfo.versionName);
        } catch(PackageManager.NameNotFoundException e) {
        }

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mForceSelectSearch) {
            mForceSelectSearch = false;
            selectItem(0);
        }
    }

    public void selectSearch() {
        mForceSelectSearch = true;
    }
}
