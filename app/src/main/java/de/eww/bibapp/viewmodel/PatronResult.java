package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaPatron;

public class PatronResult {

    private PaiaPatron success;

    private Integer error;

    public PatronResult(Integer error) {
        this.error = error;
    }

    public PatronResult(PaiaPatron success) {
        this.success = success;
    }

    public PaiaPatron getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
