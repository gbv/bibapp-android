package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PaiaItems {

    @SerializedName("doc")
    private List<PaiaItem> items;

    public PaiaItems(List<PaiaItem> items) {
        this.items = items;
    }

    public List<PaiaItem> getItems() {
        return items;
    }

    public List<PaiaItem> getBorrowed() {
        List<PaiaItem> borrowedItems = new ArrayList<>();

        for (PaiaItem paiaItem: items) {
            if (paiaItem.getStatus() >= 2 && paiaItem.getStatus() <= 4) {
                borrowedItems.add(paiaItem);
            }
        }

        // sort by end date
        Collections.sort(borrowedItems, (PaiaItem firstItem, PaiaItem secondItem) -> {
            if (firstItem.getEndTime() == null || secondItem.getEndTime() == null) {
                return 0;
            }

            return firstItem.getEndTime().compareTo(secondItem.getEndTime());
        });

        return borrowedItems;
    }

    public List<PaiaItem> getBooked() {
        List<PaiaItem> bookedItems = new ArrayList<>();

        for (PaiaItem paiaItem: items) {
            if (paiaItem.getStatus() < 2 && paiaItem.getStatus() > 4) {
                bookedItems.add(paiaItem);
            }
        }

        return bookedItems;
    }
}
