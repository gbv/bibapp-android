package de.eww.bibapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.info.LocationFragment;
import de.eww.bibapp.fragment.info.LocationsFragment;
import de.eww.bibapp.model.source.LocationSource;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationsActivity extends DrawerActivity implements
        LocationsFragment.OnLocationSelectedListener {

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    LocationsFragment mLocationsFragment;
    LocationFragment mLocationFragment;

    @Inject LocationSource mLocationSource;

    // The location index currently being displayed
    int mCurrentLocationIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        // Find our fragments
        mLocationFragment = (LocationFragment) getSupportFragmentManager().findFragmentById(R.id.location);
        mLocationsFragment = (LocationsFragment) getSupportFragmentManager().findFragmentById(R.id.locations);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the location view
        View locationView = findViewById(R.id.location);
        mIsDualPane = locationView != null && locationView.getVisibility() == View.VISIBLE;

        // Register ourselves as the listener for the locations fragment events.
        mLocationsFragment.setOnLocationSelectedListener(this);

        setActiveNavigationItem(3);

        // Set up locations fragment
        restoreSelection(savedInstanceState);
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
            mLocationFragment.setLocation(mLocationSource.getLocation(index));
        } else {
            // use separate activity
            Intent locationIntent = new Intent(this, LocationActivity.class);
            locationIntent.putExtra("locationIndex", index);
            startActivity(locationIntent);
        }
    }

    @Override
    public void onLocationsLoaded() {
        // If we are displaying the location on the right, we have to update it
        if (mIsDualPane) {
            mLocationFragment.setLocation(mLocationSource.getLocation(0));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("locationIndex", mCurrentLocationIndex);

        super.onSaveInstanceState(outState);
    }
}
