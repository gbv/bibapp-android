package de.eww.bibapp.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import de.eww.bibapp.R;

/**
* Created by christoph on 22.10.14.
*/
public class MainActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();

        // Set orientation
        boolean isLandscape = resources.getBoolean(R.bool.landscape);
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Are we recreating a previously destroyed instance?
        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Set default values for our preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
