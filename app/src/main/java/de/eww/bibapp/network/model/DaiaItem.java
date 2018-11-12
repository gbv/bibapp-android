package de.eww.bibapp.network.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.eww.bibapp.model.LocationItem;

public class DaiaItem implements Comparable<DaiaItem>
{
	public String label = "";
	public String department;
	public LocationItem locationsEntry = null;
	public Double distance = null;
	public String itemUriUrl;
	public String storage = "";

	private String actions;

	private JSONArray availableItems = new JSONArray();
	private JSONArray unavailableItems = new JSONArray();
	private JSONObject departmentObject = new JSONObject();

	public DaiaItem(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("id")) {
            itemUriUrl = jsonObject.getString("id");
        }

        if (jsonObject.has("label")) {
            label = jsonObject.getString("label");
        }

        if (jsonObject.has("storage")) {
            JSONObject storageObject = jsonObject.getJSONObject("storage");

            if (storageObject.has("content")) {
                storage = storageObject.getString("content");
            }
        }

        if (jsonObject.has("available")) {
            this.availableItems = jsonObject.getJSONArray("available");
        }

        if (jsonObject.has("unavailable")) {
            this.unavailableItems = jsonObject.getJSONArray("unavailable");
        }

        if (jsonObject.has("department")) {
            this.departmentObject = jsonObject.getJSONObject("department");
        }
    }

    public JSONArray getAvailableItems() {
	    return this.availableItems;
    }

    public JSONArray getUnavailableItems() {
	    return this.unavailableItems;
    }

    public JSONObject getDepartmentObject() {
	    return this.departmentObject;
    }

    public String getActions() {
	    return this.actions;
    }

    public void setActions(String actions) {
	    this.actions = actions;
    }

    public boolean hasLabel() {
        return !this.label.isEmpty();
    }
	
	public void setDepartment(String department)
	{
		this.department = department;
	}

    public String getDepartment() {
        return this.department;
    }

    public boolean hasDepartment() {
        return (this.department != null && !this.department.isEmpty());
    }
	
	public void setLocation(LocationItem entry)
	{
		this.locationsEntry = entry;
	}

    public boolean hasLocation() {
        return this.locationsEntry != null;
    }

    public LocationItem getLocation() {
        return this.locationsEntry;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

	@Override
	public int compareTo(DaiaItem another)
	{
		if ( this.distance == null && another.distance != null )
		{
			return 1;
		}
		
		if ( this.distance != null && another.distance == null )
		{
			return -1;
		}

		if ( this.distance == null && another.distance == null )
		{
			return 0;
		}
		
		if ( this.distance < another.distance )
		{
			return -1;
		}
		
		if ( this.distance > another.distance )
		{
			return 1;
		}
		
		return 0;
	}
}
