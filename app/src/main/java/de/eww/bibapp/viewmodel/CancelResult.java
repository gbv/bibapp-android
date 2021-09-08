package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaItems;

public class CancelResult {

    private PaiaItems success;

    private Integer error;

    public CancelResult(Integer error) {
        this.error = error;
    }

    public CancelResult(PaiaItems success) {
        this.success = success;
    }

    public PaiaItems getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
