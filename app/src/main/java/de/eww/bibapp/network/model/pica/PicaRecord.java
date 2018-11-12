package de.eww.bibapp.network.model.pica;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "record", strict = false)
public class PicaRecord {

    @ElementList(name = "datafield", inline = true)
    private List<PicaDatafield> datafields;

    public List<PicaDatafield> getDatafields() {
        return this.datafields;
    }

    public void setDatafields(List<PicaDatafield> datafields) {
        this.datafields = datafields;
    }
}
