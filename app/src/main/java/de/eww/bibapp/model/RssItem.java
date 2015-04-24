package de.eww.bibapp.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by christoph on 24.10.14.
 */
@Root(name="item", strict=false)
public class RssItem {

    @Element(name="title")
    private String mTitle;

    @Element(name="description", required=false)
    private String mDescription;

    @Element(name="encoded", data=true, required=false)
    private String mContent;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}