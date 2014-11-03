package de.eww.bibapp.model;

import java.util.ArrayList;
import java.util.List;

public class LocationItem
{
	public final String name;
	public final String listName;
	public final String address;
	public final ArrayList<String> openingHours;
	public final String email;
	public final String url;
	public final String phone;
	public final String posLong;
	public final String posLat;
	public final String description;
	
	public LocationItem(String name, String listName, String address, ArrayList<String> openingHours, String email, String url, String phone, String posLong, String posLat, String description)
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

    public String getName() {
        return listName;
    }

    public boolean hasOpeningHours() {
        return !openingHours.isEmpty();
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public boolean hasAddress() {
        return !address.isEmpty();
    }

    public String getAddress() {
        return address;
    }

    public boolean hasEmail() {
        return !email.isEmpty();
    }

    public String getEmail() {
        return email;
    }

    public boolean hasUrl() {
        return !url.isEmpty();
    }

    public String getUrl() {
        return url;
    }

    public boolean hasPhone() {
        return !phone.isEmpty();
    }

    public String getPhone() {
        return phone;
    }

    public boolean hasDescription() {
        return !description.isEmpty();
    }

    public String getDescription() {
        return description;
    }

    public boolean hasPosition() {
        return !posLong.isEmpty() && !posLat.isEmpty();
    }

    public String getLong() {
        return posLong;
    }

    public String getLat() {
        return posLat;
    }
}
