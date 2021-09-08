package de.eww.bibapp.ui.account;

public class LoginFormState {

    private Integer usernameError;

    private Integer passwordError;

    private boolean isDataValid;

    public LoginFormState(Integer usernameError, Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    public LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    public Integer getUsernameError() {
        return usernameError;
    }

    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
