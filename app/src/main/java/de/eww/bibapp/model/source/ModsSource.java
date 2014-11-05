package de.eww.bibapp.model.source;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.model.ModsItem;

/**
 * Created by christoph on 04.11.14.
 */
@Singleton
public class ModsSource {

    private List<ModsItem> mModsItems = new ArrayList<ModsItem>();

    public void addModsItems(List<ModsItem> itemList) {
        mModsItems.addAll(itemList);
    }

    public ModsItem getModsItem(int position) {
        return mModsItems.get(position);
    }

    public void clear() {
        mModsItems.clear();
    }
}