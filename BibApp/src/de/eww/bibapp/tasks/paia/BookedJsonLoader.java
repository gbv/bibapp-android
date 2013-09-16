package de.eww.bibapp.tasks.paia;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.BookedEntry;
import de.eww.bibapp.tasks.AbstractLoader;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Gets a list of booked items from paia
 */
public class BookedJsonLoader extends AbstractLoader<BookedEntry>
{
	public BookedJsonLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
	}
	
	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<BookedEntry> loadInBackground()
	{
		List<BookedEntry> response = new ArrayList<BookedEntry>();
		
		// get url
		SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
		
		String paiaUrl = Constants.getPaiaUrl(spinnerValue) + "/core/" + PaiaHelper.getUsername() + "/items?access_token=" + PaiaHelper.getAccessToken();
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(paiaUrl);
		
		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			InputStream inputStream = urlConnectionHelper.getStream();
			
			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("PAIA", httpResponse);
			
			JSONObject paiaResponse;
			if ( httpResponse.substring(0, 1).equals("[") )
			{
				paiaResponse = new JSONObject();
				paiaResponse.put("array", new JSONArray(httpResponse));
			}
			else
			{
				paiaResponse = new JSONObject(httpResponse);
			}
			
			if ( paiaResponse.has("doc") )
			{
				JSONArray bookedArray = paiaResponse.getJSONArray("doc");
				int bookedArrayLength = bookedArray.length();
				for ( int i=0; i < bookedArrayLength; i++ )
				{
					JSONObject bookedEntry = (JSONObject) bookedArray.get(i);
					
					// booked items have status unequal to 2, 3 or 4
					int status = bookedEntry.getInt("status");
					if ( status != 2 && status != 3 && status != 4 )
					{
						String bookedDateString = bookedEntry.getString("duedate");
						SimpleDateFormat simpleDateFormat;
						
						if ( bookedDateString.substring(2, 3).equals("-") && bookedDateString.substring(5, 6).equals("-") )
						{
							simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
						}
						else
						{
							simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
						}
						
						Date date = simpleDateFormat.parse(bookedEntry.getString("duedate"));
						
						
						response.add(new BookedEntry
						(
							bookedEntry.getString("about"),
							bookedEntry.getString("label"),
							date,
							bookedEntry.getString("item"),
							bookedEntry.getString("edition"),
							bookedEntry.getString("barcode")
						)
						);
					}
				}
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
