package de.eww.bibapp.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

	public DaiaItem(JSONObject jsonObject, ModsItem modsItem) throws JSONException {
        if (jsonObject.has("id")) {
            itemUriUrl = jsonObject.getString("id");
        }

        label = jsonObject.getString("label");

        if (jsonObject.has("storage")) {
            storage = jsonObject.getString("storage");
        } else {
            storage = "";
        }

        String uriUrl = "";
        String limitation = "";

        HashMap<String, JSONObject> availableItems = new HashMap<>();
        HashMap<String, JSONObject> unavailableItems = new HashMap<>();

        // available
        if (jsonObject.has("available")) {
            JSONArray availableArray = jsonObject.getJSONArray("available");

            for (int i=0; i < availableArray.length(); i++) {
                JSONObject availableObject = availableArray.getJSONObject(i);

                String service = availableObject.getString("service");
                availableItems.put(service, availableObject);

                // read limitation only from the "presentation" attribute
                if (service.equals("presentation")) {
                    if (availableObject.has("limitation")) {
                        JSONArray limitationArray = availableObject.getJSONArray("limitation");

                        for (int j=0; j < limitationArray.length(); j++) {
                            JSONObject limitationObject = limitationArray.getJSONObject(j);

                            if (limitationObject.has("content")) {
                                String content = limitationObject.getString("content");

                                if (!content.isEmpty()) {
                                    limitation = content;
                                }
                            }
                        }
                    }
                }
            }
        }

        // unavailable
        if (jsonObject.has("unavailable")) {
            JSONArray unavailableArray = jsonObject.getJSONArray("unavailable");

            for (int i=0; i < unavailableArray.length(); i++) {
                JSONObject unavailableObject = unavailableArray.getJSONObject(i);

                String service = unavailableObject.getString("service");
                unavailableItems.put(service, unavailableObject);

                // read limitation only from the "presentation" attribute
                if (service.equals("presentation")) {
                    if (unavailableObject.has("limitation")) {
                        JSONArray limitationArray = unavailableObject.getJSONArray("limitation");

                        for (int j=0; j < limitationArray.length(); j++) {
                            JSONObject limitationObject = limitationArray.getJSONObject(j);

                            if (limitationObject.has("content")) {
                                String content = limitationObject.getString("content");

                                if (!content.isEmpty()) {
                                    limitation = content;
                                }
                            }
                        }
                    }
                }
            }
        }

        // department
        if (jsonObject.has("department")) {
            JSONObject departmentObject = jsonObject.getJSONObject("department");

            if (departmentObject.has("id")) {
                uriUrl = departmentObject.getString("id");
            }
        }

        String status = "";
        String statusColor = "#000000";
        String statusInfo = "";

        if (availableItems.containsKey("loan")) {
            status += "ausleihbar";
            statusColor = "#007F00";

            if ((availableItems.containsKey("presentation") || availableItems.containsKey("presentation")) && !limitation.isEmpty()) {
                status += "; " + limitation;
            }

            if (availableItems.containsKey("presentation")) {
                // tag available with service="loan" and href=""
                if (availableItems.get("loan").has("href")) {
                    statusInfo += "Bitte bestellen";
                } else {
                    statusInfo += "Bitte am Standort entnehmen";
                }
            }
        } else {
            if (unavailableItems.containsKey("loan") && unavailableItems.get("loan").has("href")) {
                if (unavailableItems.get("loan").getString("href").contains("loan/RES")) {
                    status += "ausleihbar";
                    statusColor = "#FF7F00";
                } else {
                    status += "nicht ausleihbar";
                    statusColor = "#FF0000";
                }
            } else {
                // if this is not an online resource
                if (modsItem.onlineUrl.isEmpty()) {
                    status += "nicht ausleihbar";
                    statusColor = "#FF0000";
                } else {
                    status += "Online-Ressource im Browser öffnen";
                }
            }

            if ((availableItems.containsKey("presentation") || unavailableItems.containsKey("presentation")) && !limitation.isEmpty()) {
                status += "; " + limitation;
            }

            if (unavailableItems.containsKey("presentation")) {
                if ( unavailableItems.get("loan").has("href") ){
                    if (unavailableItems.get("loan").getString("href").contains("loan/RES")) {
                        if (!unavailableItems.get("loan").has("expected") || unavailableItems.get("loan").getString("expected").equals("unknown")) {
                            statusInfo += "ausgeliehen, Vormerken möglich";
                        } else {
                            String dateString = unavailableItems.get("loan").getString("expected");
                            SimpleDateFormat simpleDateFormat;

                            if (dateString.substring(2, 3).equals("-") && dateString.substring(5, 6).equals("-")) {
                                simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.GERMANY);
                            } else {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
                            }

                            try {
                                Date date = simpleDateFormat.parse(dateString);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                                statusInfo += "ausgeliehen bis " + dateFormat.format(date) + ", Vormerken möglich";
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    statusInfo += "...";
                }
            }
        }

        String actions = "";
        if (availableItems.containsKey("loan")) {
            if (availableItems.containsKey("presentation")) {
                if (availableItems.get("loan").has("href")) {
                    actions = "order";
                }
            }
        } else {
            if (unavailableItems.containsKey("presentation")) {
                if (unavailableItems.get("loan").has("href")) {
                    actions = "request";
                }
            }
        }

        if (    (availableItems.containsKey("loan") && availableItems.get("loan").has("href") && !availableItems.get("loan").getString("href").isEmpty()) ||
                (unavailableItems.containsKey("loan") && unavailableItems.get("loan").has("href") && !unavailableItems.get("loan").getString("href").isEmpty())) {
            actions += ";location";
        } else {
            // fix for crash when tryining to access a location entry that does not exists
            // the default actions depend on the existence of a uri entry
            if (!uriUrl.isEmpty()) {
                actions = "location";
            }
        }

        this.uriUrl = uriUrl;
        this.status = status;
        this.statusColor = statusColor;
        this.statusInfo = statusInfo;
        this.actions = actions;
    }
	
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
	
	public void setItemUriUrl(String itemUriUrl)
	{
		this.itemUriUrl = itemUriUrl;
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
