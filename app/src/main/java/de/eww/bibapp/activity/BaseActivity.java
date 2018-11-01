package de.eww.bibapp.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.util.LocaleManager;
import de.eww.bibapp.util.PrefUtils;

/**
 * Created by christoph on 10.11.14.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private final Handler mDrawerActionHandler = new Handler();

    /**
     * The {@link android.support.v4.widget.DrawerLayout} that represents the top-level content view,
     * separating the content and navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The {@link android.support.v7.widget.Toolbar} we will use as our action bar.
     */
    Toolbar mToolbar;

    NavigationView mNavigationView;
    TextView mVersionView;

    private ActionBarDrawerToggle mDrawerToggle;
    protected static int mNextNavigationIndex = R.id.nav_search;

    public static BaseActivity instance;
    private boolean mForceSelectSearch = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefUtils.init(this);

        // set screen orientation
        Resources resources = getResources();
        boolean isLandscape = resources.getBoolean(R.bool.landscape);
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // set user language
        String userLanguage = PrefUtils.getUserLanguage(this);
        if (userLanguage != null && !userLanguage.equals("device")) {
            LocaleManager.changeLanguage(this, userLanguage);
        } else {
            LocaleManager.changeLanguage(this, LocaleManager.getDefaultLanguage(this));
        }
    }

    @Override
    public void setContentView(int layoutResId) {
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        mToolbar = (Toolbar) mDrawerLayout.findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);
        mVersionView = (TextView) mDrawerLayout.findViewById(R.id.drawer_version);

        FrameLayout frameLayout = (FrameLayout) mDrawerLayout.findViewById(R.id.content_frame);

        setupNavigation();

        getLayoutInflater().inflate(layoutResId, frameLayout, true);
        super.setContentView(mDrawerLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // pass events to the ActionBarDrawerToggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setActiveNavigationItem(MenuItem menuItem) {
        // Update selected item and title
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
    }

    protected void setActiveNavigationItem(int position) {
        setActiveNavigationItem(mNavigationView.getMenu().getItem(position));
    }

    public void selectItem(int position) {
        navigate(mNavigationView.getMenu().getItem(position));
    }

    public void navigate(MenuItem menuItem) {
        // Only navigate, if the user selected a different item
        if (menuItem.getItemId() == mNextNavigationIndex) {
            return;
        }

        mNextNavigationIndex = menuItem.getItemId();

        Intent intent;

        switch (menuItem.getItemId()) {
                case R.id.nav_account:
                intent = new Intent(this, AccountActivity.class);
                break;
            case R.id.nav_watchlist:
                intent = new Intent(this, WatchlistActivity.class);
                break;
            case R.id.nav_info:
                intent = new Intent(this, InfoActivity.class);
                break;
            case R.id.nav_homepage:
                Uri homepageUrl = Uri.parse(Constants.HOMEPAGE_URLS[PrefUtils.getLocalCatalogIndex(this)]);
                intent = new Intent(Intent.ACTION_VIEW, homepageUrl);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                break;
            default:
                intent = new Intent(this, SearchActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish();

        // Close the drawer
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle("");
    }

    public void showToolbar(boolean showToolbar) {
        if (showToolbar) {
            mToolbar.setVisibility(View.VISIBLE);
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        // allow some time after closing the drawer before performing real navigation
        // so the user can see what is happening
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem);
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;
    }

    private void setupNavigation() {
        // Set a Toolbar to replace the ActionBar.
        setSupportActionBar(mToolbar);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setActiveNavigationItem(mNavigationView.getMenu().findItem(mNextNavigationIndex));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // Set up the navigation listener
        mNavigationView.setNavigationItemSelectedListener(this);

        // Show homepage menu item, if url is set
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(this);
        if (Constants.HOMEPAGE_URLS.length >= localCatalogIndex + 1) {
            MenuItem homepageItem = mNavigationView.getMenu().findItem(R.id.nav_homepage);
            homepageItem.setVisible(true);
        }

        // Get the version name and set it in the layout
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionView.setText("v" + packageInfo.versionName);
        } catch(PackageManager.NameNotFoundException e) {
        }
    }

    public void selectSearch() {
        mForceSelectSearch = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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

    @Override
    protected void onResume() {
        super.onResume();

        if (mForceSelectSearch) {
            mForceSelectSearch = false;
            navigate(mNavigationView.getMenu().getItem(0));
        }
    }
}
