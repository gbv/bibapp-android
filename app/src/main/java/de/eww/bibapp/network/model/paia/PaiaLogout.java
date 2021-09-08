package de.eww.bibapp.network.model.paia;

import com.google.gson.annotations.SerializedName;

public class PaiaLogout {

    @SerializedName("patron")
    private String patron;

    public String getPatron() {
        return patron;
    }
}
