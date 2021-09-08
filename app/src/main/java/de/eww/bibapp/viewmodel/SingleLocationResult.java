package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.LocationItem;

public class SingleLocationResult {

    private LocationItem success;

    private Integer error;

    public SingleLocationResult(Integer error) {
        this.error = error;
    }

    public SingleLocationResult(LocationItem success) {
        this.success = success;
    }

    public LocationItem getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
