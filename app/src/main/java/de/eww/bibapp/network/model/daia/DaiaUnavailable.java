package de.eww.bibapp.network.model.daia;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DaiaUnavailable {

    @SerializedName("service")
    private String service;

    @SerializedName("href")
    private String href;

    @SerializedName("title")
    private String title;

    @SerializedName("expected")
    private String expected;

    @SerializedName("queue")
    private int queue;

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

    public String getExpected() {
        return expected;
    }

    public int getQueue() {
        return queue;
    }

    public List<DaiaEntity> getLimitations() {
        return limitations;
    }
}
