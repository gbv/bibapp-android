package de.eww.bibapp.tasks;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.data.LocationsEntry;
import de.eww.bibapp.fragments.detail.DetailFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Task for getting location information from uri
 */
public class LocationsEntryTask extends AsyncTask<String, Void, LocationsEntry>
{
	private DetailFragment fragment;
	
	public LocationsEntryTask(DetailFragment fragment)
	{
		this.fragment = fragment;
	}
	
	@Override
	protected LocationsEntry doInBackground(String... params)
	{
		String uriUrl = params[0];
		
		InputStream inputStream = null;
		LocationsEntry location = null;
		
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(uriUrl + "?format=json");
		
		try
		{
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			inputStream = new BufferedInputStream(urlConnectionHelper.getInputStream());
			
			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("URI", httpResponse);
			
			JSONObject jsonResponse = new JSONObject(httpResponse);
			
			if ( jsonResponse.has(uriUrl) )
			{
				JSONObject jsonContent = (JSONObject) jsonResponse.get(uriUrl);
				
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
					location = new LocationsEntry(
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
					);
				}
			}
			
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			
			this.raiseFailure();
		}
		finally
		{
			urlConnectionHelper.disconnect();
		}
		
		return location;
	}
	
	private void raiseFailure()
	{
		((AsyncCanceledInterface) this.fragment).onAsyncCanceled();
	}
	
	@Override
	protected void onPostExecute(LocationsEntry result)
	{
		this.fragment.onLocationLoadFinished(result);
	}
}
