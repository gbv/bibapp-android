package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.DaiaItems;

public class AvailabilityResult {

    private DaiaItems success;

    private Integer error;

    public AvailabilityResult(Integer error) {
        this.error = error;
    }

    public AvailabilityResult(DaiaItems success) {
        this.success = success;
    }

    public DaiaItems getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
