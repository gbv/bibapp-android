package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.paia.PaiaLogout;

public class LogoutResult {

    private PaiaLogout success;

    private Integer error;

    public LogoutResult(Integer error) {
        this.error = error;
    }

    public LogoutResult(PaiaLogout success) {
        this.success = success;
    }

    public PaiaLogout getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
