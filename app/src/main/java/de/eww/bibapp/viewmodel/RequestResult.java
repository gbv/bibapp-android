package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaItems;

public class RequestResult {

    private PaiaItems success;

    private Integer error;

    public RequestResult(Integer error) {
        this.error = error;
    }

    public RequestResult(PaiaItems success) {
        this.success = success;
    }

    public PaiaItems getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
