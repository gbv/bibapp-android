package de.eww.bibapp.activity;

import android.os.Bundle;
import android.view.View;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.info.LocationFragment;
import de.eww.bibapp.fragment.info.LocationsFragment;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */
@ContentView(R.layout.activity_locations)
public class LocationsActivity extends RoboActivity implements
        LocationsFragment.OnLocationSelectedListener {

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    @InjectFragment(R.id.locations) LocationsFragment mLocationsFragment;
    @InjectFragment(R.id.location) LocationFragment mLocationFragment;

    @InjectView(R.id.location) View mLocationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determe whether we are in single-pane or dual-pane mode by testing the visibility
        // of the location view
        mIsDualPane = mLocationView != null && mLocationView.getVisibility() == View.VISIBLE;

        // Register ourselves as the listener for the locations fragment events.
        mLocationsFragment.setOnLocationSelectedListener(this);

        // Set up locations fragment
        //mHeadlinesFragment.setSelectable(mIsDualPane);
        //restoreSelection(savedInstanceState);
    }

    /**
     * Called when a location is selected.
     *
     * @param index the index of the selected location.
     */
    @Override
    public void onLocationSelected(int index) {
        
    }
}
