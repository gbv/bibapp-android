package de.eww.bibapp.network.model;

public class LoggedInUser {

    private final String username;

    private final int status;

    private final String token;

    public LoggedInUser(String username, String token, int status) {
        this.username = username;
        this.token = token;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public int getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
