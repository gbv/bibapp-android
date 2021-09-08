package de.eww.bibapp.viewmodel;

import de.eww.bibapp.network.model.LoggedInUser;

public class LoginResult {

    private LoggedInUser success;

    private Integer error;

    public LoginResult(Integer error) {
        this.error = error;
    }

    public LoginResult(LoggedInUser success) {
        this.success = success;
    }

    public LoggedInUser getSuccess() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
