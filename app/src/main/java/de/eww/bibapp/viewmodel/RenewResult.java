package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaItems;

public class RenewResult {

    private PaiaItems success;

    private Integer error;

    public RenewResult(Integer error) {
        this.error = error;
    }

    public RenewResult(PaiaItems success) {
        this.success = success;
    }

    public PaiaItems getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
