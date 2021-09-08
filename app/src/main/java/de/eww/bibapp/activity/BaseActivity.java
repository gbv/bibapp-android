package de.eww.bibapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import de.eww.bibapp.R;
import de.eww.bibapp.typeface.BeluginoFont;
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
     * The {@link DrawerLayout} that represents the top-level content view,
     * separating the content and navigation drawer.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * The {@link androidx.appcompat.widget.Toolbar} we will use as our action bar.
     */
    Toolbar mToolbar;

    NavigationView mNavigationView;
    TextView mVersionView;

    private ActionBarDrawerToggle mDrawerToggle;
//    protected static int mNextNavigationIndex = R.id.nav_search;

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


    }

    @Override
    public void setContentView(int layoutResId) {
        //mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        mToolbar = (Toolbar) mDrawerLayout.findViewById(R.id.toolbar);
//        mNavigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_view);
//        mVersionView = (TextView) mDrawerLayout.findViewById(R.id.drawer_version);

        //FrameLayout frameLayout = (FrameLayout) mDrawerLayout.findViewById(R.id.content_frame);

//        setupNavigation();

        //getLayoutInflater().inflate(layoutResId, frameLayout, true);
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
//        navigate(mNavigationView.getMenu().getItem(position));
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
//                navigate(menuItem);
            }
        }, DRAWER_CLOSE_DELAY_MS);

        return true;
    }

    public void selectSearch() {
        mForceSelectSearch = true;
    }
}
