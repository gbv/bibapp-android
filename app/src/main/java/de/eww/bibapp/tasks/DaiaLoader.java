package de.eww.bibapp.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.eww.bibapp.R;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DaiaItem;
import de.eww.bibapp.model.LocationItem;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.parser.DaiaXmlParser;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Loader for daia communication
*/
public class DaiaLoader extends AbstractLoader<DaiaItem>
{
	private String ppn;
	private boolean fromLocalSearch;
	private ModsItem item;
    private Context mContext;

	public DaiaLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
        mContext = context;
	}

	public void setPpn(String ppn)
	{
		this.ppn = ppn;
	}

	public void setItem(ModsItem item)
	{
		this.item = item;
	}

	public void setFromLocalSearch(boolean fromLocalSearch)
	{
		this.fromLocalSearch = fromLocalSearch;
	}

	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public ArrayList<DaiaItem> loadInBackground()
	{
		// Instantiate the parser
		DaiaXmlParser daiaXmlParser = new DaiaXmlParser(this.item);
		ArrayList<DaiaItem> response = new ArrayList<DaiaItem>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.getDaiaUrl(this.ppn, this.fromLocalSearch, localCatalogIndex), mContext);

		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);

			InputStream inputStream = urlConnectionHelper.getStream();

			ArrayList<DaiaItem> daiaResponse = daiaXmlParser.parse(inputStream);

			// each item contains a uri url from which we can request additional department information
			Iterator<DaiaItem> it = daiaResponse.iterator();

			while ( it.hasNext() )
			{
                DaiaItem entry = it.next();

				if ( !entry.uriUrl.isEmpty() )
				{
					URLConnectionHelper locationUrlConnectionHelper = new URLConnectionHelper(entry.uriUrl + "?format=json", mContext);

					try
					{
						locationUrlConnectionHelper.configure();
						locationUrlConnectionHelper.connect(null);

						InputStream locationInput = new BufferedInputStream(locationUrlConnectionHelper.getInputStream());

						String locationHttpResponse = locationUrlConnectionHelper.readStream(locationInput);
						Log.v("URI", locationHttpResponse);

						JSONObject jsonResponse = new JSONObject(locationHttpResponse);

						if ( jsonResponse.has(entry.uriUrl) )
						{
							JSONObject locationObject = jsonResponse.getJSONObject(entry.uriUrl);

							if ( locationObject.has("http://dbpedia.org/property/shortName") )
							{
								// get list name
								JSONArray jsonListNameArray = locationObject.getJSONArray("http://dbpedia.org/property/shortName");
								JSONObject jsonListNameObject = jsonListNameArray.getJSONObject(jsonListNameArray.length() - 1);
								entry.setDepartment(jsonListNameObject.getString("value"));
							}
							else
							{
								// get name
								JSONArray jsonNameArray = locationObject.getJSONArray("http://xmlns.com/foaf/0.1/name");
								JSONObject jsonNameObject = jsonNameArray.getJSONObject(jsonNameArray.length() - 1);
								entry.setDepartment(jsonNameObject.getString("value"));
							}

							// get location
							if ( locationObject.has("http://www.w3.org/2003/01/geo/wgs84_pos#location") )
							{
								JSONArray jsonLocationArray = locationObject.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#location");
								JSONObject jsonLocationObject = jsonLocationArray.getJSONObject(jsonLocationArray.length() - 1);
								String locationKey = jsonLocationObject.getString("value");

								if ( jsonResponse.has(locationKey) )
								{
									JSONObject jsonLocationContent = jsonResponse.getJSONObject(locationKey);

									String entryPosLong = "";
									String entryPosLat = "";

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

									if ( !entryPosLong.isEmpty() && !entryPosLat.isEmpty() )
									{
										entry.setLocation(new LocationItem("", "", "", new ArrayList<String>(), "", "", "", entryPosLong, entryPosLat, ""));
									}
								}
							}
						}
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();

						// remove location action for this item
						String actions = entry.actions;
						Pattern pattern = Pattern.compile(";*location");
						Matcher matcher = pattern.matcher(actions);
						if ( matcher.find() )
						{
							actions = matcher.replaceAll("");
						}
						entry.actions = actions;

						//this.raiseFailure();
					}
					finally
					{
						locationUrlConnectionHelper.disconnect();
					}
				}
				else
				{
					if ( this.fragment.isAdded() )
					{
						Resources resources = this.fragment.getResources();
						entry.setDepartment(resources.getString(R.string.detail_daia_no_department));
					}
				}

				// add entry to return list
				response.add(entry);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
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