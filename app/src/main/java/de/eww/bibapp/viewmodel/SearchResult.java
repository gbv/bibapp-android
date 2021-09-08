package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.SruResult;

public class SearchResult {

    private SruResult success;

    private Integer error;

    public SearchResult(Integer error) {
        this.error = error;
    }

    public SearchResult(SruResult success) {
        this.success = success;
    }

    public SruResult getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
