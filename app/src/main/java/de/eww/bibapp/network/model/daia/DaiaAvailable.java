package de.eww.bibapp.network.model.daia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DaiaAvailable {

    @SerializedName("service")
    private String service;

    @SerializedName("href")
    private String href;

    @SerializedName("title")
    private String title;

    @SerializedName("duration")
    private String duration;

    @SerializedName("limitation")
    private final List<DaiaEntity> limitations = new ArrayList<>();

    public String getService() {
        return service;
    }

    public String getHref() {
        return href;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public List<DaiaEntity> getLimitations() {
        return limitations;
    }
}
