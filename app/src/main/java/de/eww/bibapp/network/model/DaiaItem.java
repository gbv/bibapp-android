package de.eww.bibapp.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.network.model.daia.DaiaAvailable;
import de.eww.bibapp.network.model.daia.DaiaEntity;
import de.eww.bibapp.network.model.daia.DaiaUnavailable;

public class DaiaItem implements Comparable<DaiaItem> {

    @SerializedName("id")
    private String id;

    @SerializedName("label")
    private String label;

    @SerializedName("department")
    private DaiaEntity departmentEntity;

    @SerializedName("storage")
    private DaiaEntity storage;

    @SerializedName("available")
    private final List<DaiaAvailable> availables = new ArrayList<>();

    @SerializedName("unavailable")
    private final List<DaiaUnavailable> unavailables = new ArrayList<>();

    private LocationItem locationsEntry;

    private Double distance;

    private String actions;

    private String departmentInfo;

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DaiaEntity getDepartmentEntity() {
        return departmentEntity;
    }

    public String getDepartment() {
        return departmentInfo;
    }

    public boolean hasDepartment() {
        return (departmentInfo != null && !departmentInfo.isEmpty());
    }

    public void setDepartment(String departmentInfo) {
        this.departmentInfo = departmentInfo;
    }

    public DaiaEntity getStorage() {
        return storage;
    }

    public LocationItem getLocation() {
        return this.locationsEntry;
    }

    public boolean hasLocation() {
        return this.locationsEntry != null;
    }

    public void setLocation(LocationItem entry)
    {
        this.locationsEntry = entry;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<DaiaAvailable> getAvailables() {
        return availables;
    }

    public List<DaiaUnavailable> getUnavailables() {
        return unavailables;
    }

    public String getActions() {
	    return this.actions;
    }

    public void setActions(String actions) {
	    this.actions = actions;
    }









    @Override
    public int compareTo(DaiaItem another) {
        if (this.distance == null && another.distance != null) {
            return 1;
        }

        if (this.distance != null && another.distance == null) {
            return -1;
        }

        if (this.distance == null && another.distance == null) {
            return 0;
        }

        if (this.distance < another.distance) {
            return -1;
        }

        if (this.distance > another.distance) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final DaiaItem compare = (DaiaItem) obj;
        return this.id.equals(compare.id);
    }
}
