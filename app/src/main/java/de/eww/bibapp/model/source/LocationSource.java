package de.eww.bibapp.model.source;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.model.LocationItem;

/**
 * Created by christoph on 03.11.14.
 */
@Singleton
public class LocationSource {

    private List<LocationItem> mLocationItems = new ArrayList<LocationItem>();

    public void addLocations(List<LocationItem> itemList) {
        mLocationItems.addAll(itemList);
    }

    public LocationItem getLocation(int position) {
        return mLocationItems.get(position);
    }

    public void clear() {
        mLocationItems.clear();
    }
}
