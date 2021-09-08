package de.eww.bibapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.iconics.IconicsDrawable;

import de.eww.bibapp.typeface.BeluginoFont;
import de.eww.bibapp.util.LocaleManager;
import de.eww.bibapp.util.PrefUtils;

public class MainActivity extends AppCompatActivity {

    private NavController navController;

    private AppBarConfiguration appBarConfiguration;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrefUtils.init(this);

        toolbar = findViewById(R.id.top_appbar);
        setSupportActionBar(toolbar);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_search, R.id.nav_account, R.id.nav_watchlist, R.id.nav_info).build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
        setupBottomMenu();

        // set user language
        String userLanguage = PrefUtils.getUserLanguage(this);
        if (userLanguage != null && !userLanguage.equals("device")) {
            LocaleManager.changeLanguage(this, userLanguage);
        } else {
            LocaleManager.changeLanguage(this, LocaleManager.getDefaultLanguage(this));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);

        // homepage
        MenuItem homepageItem = menu.findItem(R.id.nav_homepage);
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(this);
        if (getResources().getStringArray(R.array.bibapp_homepage_urls).length >= localCatalogIndex + 1) {
            homepageItem.setVisible(true);
        }
        homepageItem.setIcon(new IconicsDrawable(this)
                .icon(BeluginoFont.Icon.bel_world)
                .color(Color.WHITE)
                .sizeDp(24));

        // settings
        MenuItem settingsItem = menu.findItem(R.id.nav_settings);
        settingsItem.setIcon(new IconicsDrawable(this)
                .icon(BeluginoFont.Icon.bel_cog)
                .color(Color.WHITE)
                .sizeDp(24));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle item selection
        if (id == R.id.nav_homepage) {
            Uri homepageUrl = Uri.parse(getResources().getStringArray(R.array.bibapp_homepage_urls)[PrefUtils.getLocalCatalogIndex(getApplicationContext())]);
            Intent intent = new Intent(Intent.ACTION_VIEW, homepageUrl);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

            return NavigationUI.onNavDestinationSelected(item, navController);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBottomMenu()
    {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        // Search
        menu.findItem(R.id.nav_search).setIcon(new IconicsDrawable(this)
            .icon(BeluginoFont.Icon.bel_magnifier));

        // Account
        menu.findItem(R.id.nav_account).setIcon(new IconicsDrawable(this)
            .icon(BeluginoFont.Icon.bel_account));

        // Watchlist
        menu.findItem(R.id.nav_watchlist).setIcon(new IconicsDrawable(this)
                .icon(BeluginoFont.Icon.bel_content));

        // Info
        menu.findItem(R.id.nav_info).setIcon(new IconicsDrawable(this)
                .icon(BeluginoFont.Icon.bel_info));
    }
}