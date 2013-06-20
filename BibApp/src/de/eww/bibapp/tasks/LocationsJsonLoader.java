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
			
			// search the main entry, this should be the one with the key "http://www.w3.org/ns/org#hasSite"
			JSONObject mainEntry = null;
			Iterator<?> keyIterator = jsonResponse.keys();
			while ( keyIterator.hasNext() )
			{
				String key = (String) keyIterator.next();
				JSONObject jsonContent = (JSONObject) jsonResponse.get(key);
				
				if ( jsonContent.has("http://www.w3.org/ns/org#hasSite") )
				{
					mainEntry = jsonContent;
				}
			}
			
			// is there a main entry?
			if ( mainEntry != null )
			{
				// add the main entry to the reponse list
				response.add(this.createLocationFromJSON(mainEntry, jsonResponse));
				
				// iterate the elements of the "http://www.w3.org/ns/org#hasSite" key, holding all child locations
				JSONArray jsonChildArray = mainEntry.getJSONArray("http://www.w3.org/ns/org#hasSite");
				
				for ( int i=0; i < jsonChildArray.length(); i++ )
				{
					JSONObject jsonChildContent = (JSONObject) jsonChildArray.get(i);
					
					// get the uri of the child
					String childUri = jsonChildContent.getString("value");
					
					// make a new uri request
					URLConnectionHelper childUrlConnectionHelper = new URLConnectionHelper(childUri + "?format=json");
					
					try
					{
						// open connection
						childUrlConnectionHelper.configure();
						childUrlConnectionHelper.connect(null);
						
						InputStream childInputStream = childUrlConnectionHelper.getStream();
						
						// starts the query
						childInputStream = new BufferedInputStream(childUrlConnectionHelper.getInputStream());
						
						String childHttpResponse = childUrlConnectionHelper.readStream(childInputStream);
						Log.v("URI", childHttpResponse);
						
						JSONObject childJsonResponse = new JSONObject(childHttpResponse);
						
						if ( childJsonResponse.has(childUri) )
						{
							JSONObject childJsonContent = (JSONObject) childJsonResponse.get(childUri);
							
							// add the child entry to the reponse list
							response.add(this.createLocationFromJSON(childJsonContent, childJsonResponse));
						}
					}
					catch ( Exception e )
					{
						throw e;
					}
					finally
					{
						childUrlConnectionHelper.disconnect();
					}
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
	
	private LocationsEntry createLocationFromJSON(JSONObject jsonObject, JSONObject completeResponse) throws Exception
	{
		// prepare LocationsEntry data
		String entryName = "";
		String entryListName = "";
		String entryAddress = "";
		ArrayList<String> listOpeningHours = new ArrayList<String>();
		String entryEmail = "";
		String entryUrl = "";
		String entryPhone = "";
		String entryPosLong = "";
		String entryPosLat = "";
		String entryDescription = "";
		
		// check if this is a locations entry - we assume this, if there is "http://xmlns.com/foaf/0.1/name" value
		if ( jsonObject.has("http://xmlns.com/foaf/0.1/name") )
		{
			// get name
			JSONArray jsonNameArray = jsonObject.getJSONArray("http://xmlns.com/foaf/0.1/name");
			JSONObject jsonNameObject = jsonNameArray.getJSONObject(jsonNameArray.length() - 1);
			entryName = jsonNameObject.getString("value");
			
			// get list name
			if ( jsonObject.has("http://dbpedia.org/property/shortName") )
			{
				JSONArray jsonListNameArray = jsonObject.getJSONArray("http://dbpedia.org/property/shortName");
				JSONObject jsonListNameObject = jsonListNameArray.getJSONObject(jsonListNameArray.length() - 1);
				entryListName = jsonListNameObject.getString("value");
			}
			else
			{
				entryListName = entryName;
			}
			
			// get address
			if ( jsonObject.has("http://purl.org/ontology/gbv/address") )
			{
				JSONArray jsonAddressArray = jsonObject.getJSONArray("http://purl.org/ontology/gbv/address");
				JSONObject jsonAddressObject = jsonAddressArray.getJSONObject(jsonAddressArray.length() - 1);
				entryAddress = jsonAddressObject.getString("value");
			}
			
			// get opening hours
			if ( jsonObject.has("http://purl.org/ontology/gbv/openinghours") )
			{
				JSONArray jsonOpeningHoursArray = jsonObject.getJSONArray("http://purl.org/ontology/gbv/openinghours");
				
				for ( int i=0; i < jsonOpeningHoursArray.length(); i++ )
				{
					JSONObject jsonOpeningHoursObject = jsonOpeningHoursArray.getJSONObject(i);
					listOpeningHours.add(jsonOpeningHoursObject.getString("value"));
				}
			}
			
			// get email
			if ( jsonObject.has("http://www.w3.org/2006/vcard/ns#email") )
			{
				JSONArray jsonEmailArray = jsonObject.getJSONArray("http://www.w3.org/2006/vcard/ns#email");
				JSONObject jsonEmailObject = jsonEmailArray.getJSONObject(jsonEmailArray.length() - 1);
				entryEmail = jsonEmailObject.getString("value");
			}
			
			// get url
			if ( jsonObject.has("http://www.w3.org/2006/vcard/ns#url") )
			{
				JSONArray jsonUrlArray = jsonObject.getJSONArray("http://www.w3.org/2006/vcard/ns#url");
				JSONObject jsonUrlObject = jsonUrlArray.getJSONObject(jsonUrlArray.length() - 1);
				entryUrl = jsonUrlObject.getString("value");
			}
			
			// get phone
			if ( jsonObject.has("http://xmlns.com/foaf/0.1/phone") )
			{
				JSONArray jsonPhoneArray = jsonObject.getJSONArray("http://xmlns.com/foaf/0.1/phone");
				JSONObject jsonPhoneObject = jsonPhoneArray.getJSONObject(jsonPhoneArray.length() - 1);
				entryPhone = jsonPhoneObject.getString("value");
			}
			
			// get location
			if ( jsonObject.has("http://www.w3.org/2003/01/geo/wgs84_pos#location") )
			{
				JSONArray jsonLocationArray = jsonObject.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#location");
				
				if ( jsonLocationArray.length() > 0 )
				{
					JSONObject jsonLocationFirstObject = jsonLocationArray.getJSONObject(0);
					
					// check if the first item is of type bnode
					if ( jsonLocationFirstObject.getString("type").equals("bnode") )
					{
						// search the referenced nodes
						String referenceNodeName = jsonLocationFirstObject.getString("value");
						if ( completeResponse.has(referenceNodeName) )
						{
							JSONObject jsonLocationContent = completeResponse.getJSONObject(referenceNodeName);
							
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
					else
					{
						
					}
				}
			}
			
			// get description
			if ( jsonObject.has("http://purl.org/dc/elements/1.1/description") )
			{
				JSONArray jsonDescriptionArray = jsonObject.getJSONArray("http://purl.org/dc/elements/1.1/description");
				JSONObject jsonDescriptionObject = jsonDescriptionArray.getJSONObject(jsonDescriptionArray.length() - 1);
				entryDescription = jsonDescriptionObject.getString("value");
			}
		}
		
		// add entry
		return new LocationsEntry(
			entryName,
			entryListName,
			entryAddress,
			listOpeningHours,
			entryEmail,
			entryUrl,
			entryPhone,
			entryPosLong,
			entryPosLat,
			entryDescription
		);
	}
}
