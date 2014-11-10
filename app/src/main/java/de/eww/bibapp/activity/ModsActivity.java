package de.eww.bibapp.activity;

/**
 * Created by christoph on 08.11.14.
 */

import android.os.Bundle;

import com.google.inject.Inject;

import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
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

    @Inject ModsSource mModsSource;

    // The mods item index we are to display
    int mModsItemIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModsItemIndex = getIntent().getExtras().getInt("modsItemIndex", 0);

        // Place a ModsFragment as our content pane
        ModsPagerFragment modsPagerFragment = new ModsPagerFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, modsPagerFragment).commit();

        // Display the correct mods item on the fragment
        modsPagerFragment.setModsItem(mModsItemIndex);
    }
}
