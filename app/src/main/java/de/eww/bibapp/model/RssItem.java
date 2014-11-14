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

    @Element(name="description")
    private String mDescription;

    @Element(name="encoded", data=true, required=false)
    private String mContent;

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getContent() {
        return mContent;
    }
}