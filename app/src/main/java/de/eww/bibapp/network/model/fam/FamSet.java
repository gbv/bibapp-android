package de.eww.bibapp.network.model.fam;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "RESULTS", strict = false)
public class FamSet {

    @Attribute()
    private int hits;

    @ElementList(name = "SET", required = false, inline = true)
    private List<FamItem> items;

    public int getHits() {
        return this.hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public List<FamItem> getItems() {
        return this.items;
    }

    public void setItems(List<FamItem> items) {
        this.items = items;
    }
}
