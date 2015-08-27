package de.eww.bibapp.model.source;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.model.ModsItem;

/**
 * Created by christoph on 04.11.14.
 */
@Singleton
public class ModsSource {

    private HashMap<String, List<ModsItem>> mHashMap = new HashMap<>();
    private HashMap<String, Integer> mTotalItemsMap = new HashMap<>();

    public void addModsItems(String hashMap, List<ModsItem> itemList) {
        if (!mHashMap.containsKey(hashMap)) {
            mHashMap.put(hashMap, new ArrayList<ModsItem>());
        }

        mHashMap.get(hashMap).addAll(itemList);
    }

    public ModsItem getModsItem(String hashMap, int position) {
        return mHashMap.get(hashMap).get(position);
    }

    public List<ModsItem> getModsItems(String hashMap) {
        return mHashMap.get(hashMap);
    }

    public void clear(String hashMap) {
        if (mHashMap.containsKey(hashMap)) {
            mHashMap.get(hashMap).clear();
        }
    }

    public int getLoadedItems(String hashMap) {
        return mHashMap.get(hashMap).size();
    }

    public void setTotalItems(String hashMap, int totalItems) {
        mTotalItemsMap.put(hashMap, totalItems);
    }

    public int getTotalItems(String hashMap) {
        if (mTotalItemsMap.containsKey(hashMap)) {
            return mTotalItemsMap.get(hashMap);
        }

        return 0;
    }
}