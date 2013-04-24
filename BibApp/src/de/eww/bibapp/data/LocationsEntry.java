package de.eww.bibapp.data;

public class LocationsEntry
{
	public final String name;
	public final String listName;
	public final String address;
	public final String openingHours;
	public final String email;
	public final String url;
	public final String phone;
	public final String posLong;
	public final String posLat;
	public final String description;
	
	public LocationsEntry(String name, String listName, String address, String openingHours, String email, String url, String phone, String posLong, String posLat, String description)
	{
		this.name = name;
		this.listName = listName;
		this.address = address;
		this.openingHours = openingHours;
		this.email = email;
		this.url = url;
		this.phone = phone;
		this.posLong = posLong;
		this.posLat = posLat;
		this.description = description;
	}
}
