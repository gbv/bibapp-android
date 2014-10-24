package de.eww.bibapp.data;

/**
 * Created by christoph on 24.10.14.
 */
public class RssItem {
    private String mTitle;
    private String mDescription;

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setmDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }
}