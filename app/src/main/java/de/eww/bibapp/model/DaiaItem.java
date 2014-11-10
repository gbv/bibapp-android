package de.eww.bibapp.model;

public class DaiaItem implements Comparable<DaiaItem>
{
	public String label;
	public final String uriUrl;
	public final String status;
	public final String statusColor;
	public final String statusInfo;
	public String department;
	public String actions;
	public LocationItem locationsEntry = null;
	public Double distance = null;
	public String itemUriUrl;
	public final String storage;
	
	public DaiaItem(String label, String uriUrl, String status, String statusColor, String statusInfo, String actions, String storage)
	{
		this.label = label;
		this.uriUrl = uriUrl;
		this.status = status;
		this.statusColor = statusColor;
		this.statusInfo = statusInfo;
		this.actions = actions;
		this.storage = storage;
	}
	
	public void setDepartment(String department)
	{
		this.department = department;
	}
	
	public void setItemUriUrl(String itemUriUrl)
	{
		this.itemUriUrl = itemUriUrl;
	}
	
	public void setLocation(LocationItem entry)
	{
		this.locationsEntry = entry;
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
