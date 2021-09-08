package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaFees;

public class FeesResult {

    private PaiaFees success;

    private Integer error;

    public FeesResult(Integer error) {
        this.error = error;
    }

    public FeesResult(PaiaFees success) {
        this.success = success;
    }

    public PaiaFees getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
