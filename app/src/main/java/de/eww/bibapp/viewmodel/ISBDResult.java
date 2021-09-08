package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.ISBD;

public class ISBDResult {

    private String success;

    private Integer error;

    public ISBDResult(Integer error) {
        this.error = error;
    }

    public ISBDResult(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
