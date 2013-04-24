package de.eww.bibapp.tasks;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.LocationsEntry;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Loader for uri communication
 */
public class LocationsJsonLoader extends AbstractLoader<LocationsEntry>
{
	public LocationsJsonLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
	}
	
	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<LocationsEntry> loadInBackground()
	{
		List<LocationsEntry> response = new ArrayList<LocationsEntry>();
		
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.LOCATION_URL);
		
		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			InputStream inputStream = urlConnectionHelper.getStream();
			
			// starts the query
			inputStream = new BufferedInputStream(urlConnectionHelper.getInputStream());
			
			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("URI", httpResponse);
			
			JSONObject jsonResponse = new JSONObject(httpResponse);
			Iterator<?> keyIterator = jsonResponse.keys();
			
			while ( keyIterator.hasNext() )
			{
				String key = (String) keyIterator.next();
				JSONObject jsonContent = (JSONObject) jsonResponse.get(key);
				
				// check if this is a locations entry - we assume this, if there is "http://xmlns.com/foaf/0.1/name" value
				if ( jsonContent.has("http://xmlns.com/foaf/0.1/name") )
				{
					// prepare LocationsEntry data
					String entryName = "";
					String entryListName = "";
					String entryAddress = "";
					String entryOpeningHours = "";
					String entryEmail = "";
					String entryUrl = "";
					String entryPhone = "";
					String entryPosLong = "";
					String entryPosLat = "";
					String entryDescription = "";
					
					// get name
					JSONArray jsonNameArray = jsonContent.getJSONArray("http://xmlns.com/foaf/0.1/name");
					JSONObject jsonNameObject = jsonNameArray.getJSONObject(jsonNameArray.length() - 1);
					entryName = jsonNameObject.getString("value");
					
					// get list name
					if ( jsonContent.has("http://dbpedia.org/property/shortName") )
					{
						JSONArray jsonListNameArray = jsonContent.getJSONArray("http://dbpedia.org/property/shortName");
						JSONObject jsonListNameObject = jsonListNameArray.getJSONObject(jsonListNameArray.length() - 1);
						entryListName = jsonListNameObject.getString("value");
					}
					else
					{
						entryListName = entryName;
					}
					
					// get address
					if ( jsonContent.has("http://purl.org/ontology/gbv/address") )
					{
						JSONArray jsonAddressArray = jsonContent.getJSONArray("http://purl.org/ontology/gbv/address");
						JSONObject jsonAddressObject = jsonAddressArray.getJSONObject(jsonAddressArray.length() - 1);
						entryAddress = jsonAddressObject.getString("value");
					}
					
					// get opening hours
					if ( jsonContent.has("http://purl.org/ontology/gbv/openinghours") )
					{
						JSONArray jsonOpeningHoursArray = jsonContent.getJSONArray("http://purl.org/ontology/gbv/openinghours");
						JSONObject jsonOpeningHoursObject = jsonOpeningHoursArray.getJSONObject(jsonOpeningHoursArray.length() - 1);
						entryOpeningHours = jsonOpeningHoursObject.getString("value");
					}
					
					// get email
					if ( jsonContent.has("http://www.w3.org/2006/vcard/ns#email") )
					{
						JSONArray jsonEmailArray = jsonContent.getJSONArray("http://www.w3.org/2006/vcard/ns#email");
						JSONObject jsonEmailObject = jsonEmailArray.getJSONObject(jsonEmailArray.length() - 1);
						entryEmail = jsonEmailObject.getString("value");
					}
					
					// get url
					if ( jsonContent.has("http://www.w3.org/2006/vcard/ns#url") )
					{
						JSONArray jsonUrlArray = jsonContent.getJSONArray("http://www.w3.org/2006/vcard/ns#url");
						JSONObject jsonUrlObject = jsonUrlArray.getJSONObject(jsonUrlArray.length() - 1);
						entryUrl = jsonUrlObject.getString("value");
					}
					
					// get phone
					if ( jsonContent.has("http://xmlns.com/foaf/0.1/phone") )
					{
						JSONArray jsonPhoneArray = jsonContent.getJSONArray("http://xmlns.com/foaf/0.1/phone");
						JSONObject jsonPhoneObject = jsonPhoneArray.getJSONObject(jsonPhoneArray.length() - 1);
						entryPhone = jsonPhoneObject.getString("value");
					}
					
					// get location
					if ( jsonContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#location") )
					{
						JSONArray jsonLocationArray = jsonContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#location");
						JSONObject jsonLocationObject = jsonLocationArray.getJSONObject(jsonLocationArray.length() - 1);
						String locationKey = jsonLocationObject.getString("value");
						
						if ( jsonResponse.has(locationKey) )
						{
							JSONObject jsonLocationContent = jsonResponse.getJSONObject(locationKey);
							
							if ( jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#long") )
							{
								JSONArray jsonLongArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#long");
								JSONObject jsonLongObject = jsonLongArray.getJSONObject(jsonLongArray.length() - 1);
								entryPosLong = jsonLongObject.getString("value");
							}
							
							if ( jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#lat") )
							{
								JSONArray jsonLatArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
								JSONObject jsonLatObject = jsonLatArray.getJSONObject(jsonLatArray.length() - 1);
								entryPosLat = jsonLatObject.getString("value");
							}
						}
					}
					
					// get description
					if ( jsonContent.has("http://purl.org/dc/elements/1.1/description") )
					{
						JSONArray jsonDescriptionArray = jsonContent.getJSONArray("http://purl.org/dc/elements/1.1/description");
						JSONObject jsonDescriptionObject = jsonDescriptionArray.getJSONObject(jsonDescriptionArray.length() - 1);
						entryDescription = jsonDescriptionObject.getString("value");
					}
					
					// add entry
					response.add(new LocationsEntry(
						entryName,
						entryListName,
						entryAddress,
						entryOpeningHours,
						entryEmail,
						entryUrl,
						entryPhone,
						entryPosLong,
						entryPosLat,
						entryDescription
					));
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			
			this.raiseFailure();
		}
		finally
		{
			urlConnectionHelper.disconnect();
		}
		
		return response;
	}
}
