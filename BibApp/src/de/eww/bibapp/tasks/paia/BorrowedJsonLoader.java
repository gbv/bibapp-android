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
import android.support.v4.app.Fragment;
import android.util.Log;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.BorrowedEntry;
import de.eww.bibapp.tasks.AbstractLoader;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Gets a list of borrowed items from paia
 */
public class BorrowedJsonLoader extends AbstractLoader<BorrowedEntry>
{
	public BorrowedJsonLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
	}
	
	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<BorrowedEntry> loadInBackground()
	{
		List<BorrowedEntry> response = new ArrayList<BorrowedEntry>();
		
		// get url
		String paiaUrl = Constants.PAIA_URL + "/core/" + PaiaHelper.getUsername() + "/items?access_token=" + PaiaHelper.getAccessToken();
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
				JSONArray borrowedArray = paiaResponse.getJSONArray("doc");
				int borrowedArrayLength = borrowedArray.length();
				for ( int i=0; i < borrowedArrayLength; i++ )
				{
					JSONObject borrowedEntry = (JSONObject) borrowedArray.get(i);
					
					// borrowed items have status 2, 3 or 4
					int status = borrowedEntry.getInt("status");
					if ( status == 2 || status == 3 || status == 4 )
					{
						String borrowedDateString = borrowedEntry.getString("duedate");
						SimpleDateFormat simpleDateFormat;
						
						Date date = null;
						
						if ( !borrowedDateString.isEmpty() )
						{
							if ( borrowedDateString.substring(2, 3).equals("-") && borrowedDateString.substring(5, 6).equals("-") )
							{
								simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
							}
							else
							{
								simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
							}
							
							date = simpleDateFormat.parse(borrowedEntry.getString("duedate"));
						}
						
						response.add(new BorrowedEntry
						(
							borrowedEntry.getString("about"),
							borrowedEntry.getString("label"),
							date,
							borrowedEntry.getInt("queue"),
							borrowedEntry.getInt("renewals"),
							borrowedEntry.getString("storage"),
							borrowedEntry.getString("item"),
							borrowedEntry.getString("edition"),
							borrowedEntry.getString("barcode"),
							borrowedEntry.getBoolean("canrenew")
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
