package de.eww.bibapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.info.LocationFragment;
import de.eww.bibapp.fragment.info.LocationsFragment;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */
@ContentView(R.layout.activity_locations)
public class LocationsActivity extends RoboFragmentActivity implements
        LocationsFragment.OnLocationSelectedListener {

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    @InjectFragment(R.id.locations) LocationsFragment mLocationsFragment;
    @InjectFragment(R.id.location) LocationFragment mLocationFragment;

    @InjectView(R.id.location) View mLocationView;

    // The location index currently being displayed
    int mCurrentLocationIndex = 0;

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
     * Restore location selection from saved state
     */
    private void restoreSelection(Bundle savedInstancteState) {
        if (savedInstancteState != null) {
            if (mIsDualPane) {
                int locationIndex = savedInstancteState.getInt("locationIndex", 0);
                mLocationsFragment.setSelection(locationIndex);
                onLocationSelected(locationIndex);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        restoreSelection(savedInstanceState);
    }

    /**
     * Called when a location is selected.
     *
     * @param index the index of the selected location.
     */
    @Override
    public void onLocationSelected(int index) {
        mCurrentLocationIndex = index;

        if (mIsDualPane) {
            // display it on the location fragment
            mLocationFragment.displayLocation();
        } else {
            // use separate activity
            Intent locationIntent = new Intent(this, LocationActivity.class);
            locationIntent.putExtra("locationIndex", index);
            startActivity(locationIntent);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("locationIndex", mCurrentLocationIndex);

        super.onSaveInstanceState(outState);
    }
}
