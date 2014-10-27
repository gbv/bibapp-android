package de.eww.bibapp.fragment.info;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationsFragment extends RoboFragment implements AdapterView.OnItemClickListener {

    // The listener we are to notify when a location is selected
    OnLocationSelectedListener mLocationSelectedListener = null;

    /**
     * Represents a listener that will be notified of location selections.
     */
    public interface OnLocationSelectedListener {
        /**
         * Call when a given location is selected.
         *
         * @param index the index of the selected location.
         */
        public void onLocationSelected(int index);
    }

    /**
     * Sets the listener that should be notified of location selection events.
     *
     * @param listener the listener to notify.
     */
    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        mLocationSelectedListener = listener;
    }
}
