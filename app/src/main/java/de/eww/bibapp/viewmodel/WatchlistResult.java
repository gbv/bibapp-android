package de.eww.bibapp.viewmodel;

import java.util.List;

import de.eww.bibapp.network.model.ModsItem;

public class WatchlistResult {

    private List<ModsItem> success;

    private Integer error;

    public WatchlistResult(Integer error) {
        this.error = error;
    }

    public WatchlistResult(List<ModsItem> success) {
        this.success = success;
    }

    public List<ModsItem> getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
