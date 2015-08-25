package de.eww.bibapp.tasks.paia;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.FeeItem;
import de.eww.bibapp.tasks.AbstractLoader;
import de.eww.bibapp.util.PrefUtils;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Gets a list of borrowed items from paia
*/
public class FeeJsonLoader extends AbstractLoader<FeeItem>
{

    Context mContext;

	public FeeJsonLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
        mContext = context;
	}

	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<FeeItem> loadInBackground()
	{
		List<FeeItem> response = new ArrayList<FeeItem>();

		// get url
		int localCatalogIndex = PrefUtils.getLocalCatalogIndex(mContext);
		String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/core/" + PaiaHelper.getInstance().getUsername() + "/fees?access_token=" + PaiaHelper.getInstance().getAccessToken();
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(paiaUrl, mContext);

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

                String about = fee.has("about") ? fee.getString("about") : "";

				response.add(new FeeItem(fee.getString("amount"), about, feeDate, sum));
			}
		}
		catch (Exception e)
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
