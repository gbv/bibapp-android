package de.eww.bibapp.activity;

import android.os.Bundle;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.settings.SettingsFragment;

public class SettingsActivity extends DrawerActivity {

    public static final String KEY_PREF_STORE_LOGIN = "pref_storeLogin";
    public static final String KEY_PREF_LOCAL_CATALOG = "pref_localCatalog";
    public static final String KEY_PREF_DATA_PRIVACY = "pref_dataPrivacy";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setActiveNavigationItem(DrawerActivity.NAV_ITEM_SETTINGS);
    }
}