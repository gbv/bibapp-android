package de.eww.bibapp.activity;

/**
 * Created by christoph on 08.11.14.
 */

import android.content.Intent;
import android.os.Bundle;

import com.google.inject.Inject;

import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.model.source.WatchlistSource;
import roboguice.activity.RoboFragmentActivity;

/**
 * Activity that displays a particular location onscreen.
 *
 * This activity is started only when the screen in not large enough for a two-pane layout, in
 * which case this separate activity is shown in order to display the location. This activity
 * kills itself if the display is reconfigured into a shape that allows a two-pane layout, since
 * in that case the location article will be displayed by the {@link de.eww.bibapp.activity.LocationsActivity}
 * and this Activity becomes unnecessary.
 */
public class ModsActivity extends RoboFragmentActivity {

    // The mods item index we are to display
    int mModsItemIndex;

    ModsPagerFragment mModsPagerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModsItemIndex = getIntent().getExtras().getInt("modsItemIndex", 0);

        // Place a ModsFragment as our content pane
        mModsPagerFragment = new ModsPagerFragment();

        if (getIntent().hasExtra("modsItemSource")) {
            String modsItemSource = getIntent().getExtras().getString("modsItemSource");
            if (modsItemSource.equals(WatchlistSource.class.getName())) {
                mModsPagerFragment.useWatchlistSource();
            }
        }

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mModsPagerFragment).commit();

        // Display the correct mods item on the fragment
        mModsPagerFragment.setModsItem(mModsItemIndex);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("pagerItemPosition", mModsPagerFragment.getCurrentItemPosition());
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
