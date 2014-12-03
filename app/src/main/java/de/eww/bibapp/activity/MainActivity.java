package de.eww.bibapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.eww.bibapp.R;

/**
* Created by christoph on 22.10.14.
*/
public class MainActivity extends DrawerActivity {

    public static MainActivity instance;
    private boolean mForceSelectSearch = false;

    public static final int NAVI_SEARCH = 0;
    public static final int NAVI_ACCOUNT = 1;
    public static final int NAVI_WATCHLIST = 2;
    public static final int NAVI_INFO = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        // Are we recreating a previously destroyed instance?
        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Set default values for our preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
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
