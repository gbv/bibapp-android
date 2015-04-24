package de.eww.bibapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.model.LocationItem;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Task for getting location information from uri
*/
public class LocationsJsonTask extends AsyncTask<String, Void, LocationItem> {

	private ModsFragment fragment;

	public LocationsJsonTask(ModsFragment fragment)
	{
		this.fragment = fragment;
	}

	@Override
	protected LocationItem doInBackground(String... params)
	{
		String uriUrl = params[0];

		InputStream inputStream = null;
        LocationItem location = null;

		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(uriUrl + "?format=json", fragment.getActivity());

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
					ArrayList<String> listOpeningHours = new ArrayList<String>();
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
						for ( int i=0; i < jsonOpeningHoursArray.length(); i++ )
						{
							JSONObject jsonOpeningHoursObject = jsonOpeningHoursArray.getJSONObject(i);
							listOpeningHours.add(jsonOpeningHoursObject.getString("value"));
						}
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

						if ( jsonLocationArray.length() > 0 )
						{
							JSONObject jsonLocationFirstObject = jsonLocationArray.getJSONObject(0);

							// check if the first item is of type bnode
							if ( jsonLocationFirstObject.getString("type").equals("bnode") )
							{
								// search the referenced nodes
								String referenceNodeName = jsonLocationFirstObject.getString("value");
								if ( jsonResponse.has(referenceNodeName) )
								{
									JSONObject jsonLocationContent = jsonResponse.getJSONObject(referenceNodeName);

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
					if ( jsonContent.has("http://purl.org/dc/elements/1.1/description") )
					{
						JSONArray jsonDescriptionArray = jsonContent.getJSONArray("http://purl.org/dc/elements/1.1/description");
						JSONObject jsonDescriptionObject = jsonDescriptionArray.getJSONObject(jsonDescriptionArray.length() - 1);
						entryDescription = jsonDescriptionObject.getString("value");
					}

					// add entry
					location = new LocationItem(
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
	protected void onPostExecute(LocationItem result)
	{
		this.fragment.onLocationLoadFinished(result);
	}
}
