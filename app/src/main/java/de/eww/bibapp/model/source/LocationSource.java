package de.eww.bibapp.model.source;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.model.LocationItem;

/**
 * Created by christoph on 03.11.14.
 */
public class LocationSource {

    private static List<LocationItem> mLocationItems = new ArrayList<LocationItem>();

    public static void addLocations(List<LocationItem> itemList) {
        mLocationItems.addAll(itemList);
    }

    public static void addLocation(LocationItem locationItem) {
        mLocationItems.add(locationItem);
    }

    public static LocationItem getLocation(int position) {
        return mLocationItems.get(position);
    }

    public static void clear() {
        mLocationItems.clear();
    }
}
