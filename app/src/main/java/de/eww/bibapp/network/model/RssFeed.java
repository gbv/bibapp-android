package de.eww.bibapp.network.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by christoph on 24.10.14.
 */
@Root(name="rss", strict=false)
public class RssFeed {

    @ElementList(entry="item", inline=true)
    @Path("channel")
    private List<RssItem> mItems;

    public List<RssItem> getItems() {
        return mItems;
    }

    public void setItems(List<RssItem> items) {
        mItems = items;
    }
}
