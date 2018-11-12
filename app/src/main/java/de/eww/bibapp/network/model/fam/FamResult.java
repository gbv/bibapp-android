package de.eww.bibapp.network.model.fam;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "RESULTS", strict = false)
public class FamResult {

    @Element(name = "SET", required = false)
    private FamSet set;

    public FamSet getSet() {
        return this.set;
    }

    public void setSet(FamSet set) {
        this.set = set;
    }
}
