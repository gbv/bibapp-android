package de.eww.bibapp.network.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SruResult {

    private HashMap<String, Object> result = new HashMap<>();

    public List<ModsItem> getItems() {
        if (result.containsKey("list")) {
            return (List<ModsItem>) result.get("list");
        }

        return new ArrayList<>();
    }

    public void setItems(List<ModsItem> items) {
        result.put("list", items);
    }

    public int getNumberOfRecords() {
        if (result.containsKey("numberOfRecords")) {
            return (Integer) result.get("numberOfRecords");
        }

        return 0;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        result.put("numberOfRecords", numberOfRecords);
    }
}
