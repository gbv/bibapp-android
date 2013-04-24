package de.eww.bibapp.tasks.paia;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.FeeEntry;
import de.eww.bibapp.tasks.AbstractLoader;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Gets a list of borrowed items from paia
 */
public class FeeJsonLoader extends AbstractLoader<FeeEntry>
{
	public FeeJsonLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
	}
	
	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<FeeEntry> loadInBackground()
	{
		List<FeeEntry> response = new ArrayList<FeeEntry>();
		
		// get url
		String paiaUrl = Constants.PAIA_URL + "/core/" + PaiaHelper.getUsername() + "/fees?access_token=" + PaiaHelper.getAccessToken();
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
			try
			{
				paiaResponse = new JSONObject(httpResponse);
			}
			catch ( JSONException e )
			{
				return response;
			}
			
			String sum = paiaResponse.getString("amount");
			
			JSONArray feeArray = paiaResponse.getJSONArray("fee");
			int feeArrayLength = feeArray.length();
			for ( int i=0; i < feeArrayLength; i++ )
			{
				JSONObject fee = (JSONObject) feeArray.get(i);
				
				String feeDateString = fee.getString("date");
				SimpleDateFormat simpleDateFormat;
				
				if ( feeDateString.substring(2, 3).equals("-") && feeDateString.substring(5, 6).equals("-") )
				{
					simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
				}
				else
				{
					simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
				}
				
				Date feeDate = simpleDateFormat.parse(fee.getString("date"));
				
				response.add(new FeeEntry(fee.getString("amount"), fee.getString("about"), feeDate, sum));
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
