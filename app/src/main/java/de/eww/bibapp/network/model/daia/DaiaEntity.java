package de.eww.bibapp.network.model.daia;

import com.google.gson.annotations.SerializedName;

public class DaiaEntity {

    @SerializedName("id")
    private String id;

    @SerializedName("href")
    private String href;

    @SerializedName("content")
    private String content;

    public String getId() {
        return id;
    }

    public String getHref() {
        return href;
    }

    public String getContent() {
        return content;
    }
}
