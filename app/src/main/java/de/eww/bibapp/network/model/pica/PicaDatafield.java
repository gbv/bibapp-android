package de.eww.bibapp.network.model.pica;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root(name = "datafield", strict = false)
public class PicaDatafield {

    @Attribute(name = "tag")
    private String tag;

    @Attribute(name = "occurrence", required = false)
    private String occurrence;

    @ElementMap(entry = "subfield", key = "code", attribute = true, inline = true)
    private Map<String, String> subfields;

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOccurrence() {
        return this.occurrence;
    }

    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }

    public Map<String, String> getSubfields() {
        return this.subfields;
    }

    public void setSubfields(Map<String, String> subfields) {
        this.subfields = subfields;
    }
}
