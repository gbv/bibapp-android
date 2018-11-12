package de.eww.bibapp.network.model;

import java.util.ArrayList;
import java.util.List;

public class DaiaItems {
    private List<DaiaItem> items = new ArrayList<>();

    public List<DaiaItem> getItems() {
        return this.items;
    }

    public void setItems(List<DaiaItem> items) {
        this.items = items;
    }

    public void addItems(List<DaiaItem> items) {
        this.items.addAll(items);
    }

    public void addItem(DaiaItem daiaItem) {
        this.items.add(daiaItem);
    }
}
