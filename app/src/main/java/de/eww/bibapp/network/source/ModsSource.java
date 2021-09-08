package de.eww.bibapp.network.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.network.model.ModsItem;

public class ModsSource {

    private static HashMap<String, List<ModsItem>> mHashMap = new HashMap<>();
    private static HashMap<String, Integer> mTotalItemsMap = new HashMap<>();

    public static void addModsItems(String hashMap, List<ModsItem> itemList) {
        if (!mHashMap.containsKey(hashMap)) {
            mHashMap.put(hashMap, new ArrayList<>());
        }

        mHashMap.get(hashMap).addAll(itemList);
    }

    public static ModsItem getModsItem(String hashMap, int position) {
        return mHashMap.get(hashMap).get(position);
    }

    public static List<ModsItem> getModsItems(String hashMap) {
        return mHashMap.get(hashMap);
    }

    public static void clear(String hashMap) {
        if (mHashMap.containsKey(hashMap)) {
            mHashMap.get(hashMap).clear();
        }
    }

    public static int getLoadedItems(String hashMap) {
        return mHashMap.get(hashMap).size();
    }

    public static void setTotalItems(String hashMap, int totalItems) {
        mTotalItemsMap.put(hashMap, totalItems);
    }

    public static int getTotalItems(String hashMap) {
        if (mTotalItemsMap.containsKey(hashMap)) {
            return mTotalItemsMap.get(hashMap);
        }

        return 0;
    }
}