package de.eww.bibapp.network.model.fam;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "SHORTTITLE", strict = false)
public class FamItem {

    @Attribute(name = "PPN")
    private String ppn;

    public String getPPN() {
        return this.ppn;
    }

    public void setPPN(String ppn) {
        this.ppn = ppn;
    }
}
