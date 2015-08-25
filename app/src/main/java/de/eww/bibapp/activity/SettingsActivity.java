package de.eww.bibapp.activity;

import android.os.Bundle;

import de.eww.bibapp.R;

public class SettingsActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //setActiveNavigationItem(BaseActivity.NAV_ITEM_SETTINGS);
    }
}
