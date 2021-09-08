package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PaiaPatron {

    @SerializedName("name")
    private String name;

    @SerializedName("expires")
    private Date expires;

    @SerializedName("email")
    private String email;

    @SerializedName("note")
    private String note;

    @SerializedName("status")
    private int status;

    @SerializedName("address")
    private String address;

    // type

    public String getName() {
        return name;
    }

    public Date getExpires() {
        return expires;
    }

    public String getEmail() {
        return email;
    }

    public String getNote() {
        return note;
    }

    public int getStatus() {
        return status;
    }

    public String getAddress() {
        return address;
    }
}
