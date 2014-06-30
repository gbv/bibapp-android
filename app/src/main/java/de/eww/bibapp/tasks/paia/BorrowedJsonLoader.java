package de.eww.bibapp.tasks.paia;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.PaiaDocument;
import de.eww.bibapp.tasks.AbstractLoader;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Gets a list of borrowed items from paia
 */
public class BorrowedJsonLoader extends AbstractLoader<PaiaDocument>
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
	public List<PaiaDocument> loadInBackground()
	{
		List<PaiaDocument> response = new ArrayList<PaiaDocument>();
		
		// get url
		SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
		
		String paiaUrl = Constants.getPaiaUrl(spinnerValue) + "/core/" + PaiaHelper.getInstance().getUsername() + "/items?access_token=" + PaiaHelper.getInstance().getAccessToken();
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(paiaUrl);
		
		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			InputStream inputStream = urlConnectionHelper.getStream();
			
			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("PAIA", httpResponse);

            JSONObject paiaResponse = new JSONObject(httpResponse);
			
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
                        Date date = null;
                        if (borrowedEntry.has("duedate")) {
                            String borrowedDateString = borrowedEntry.getString("duedate");
                            SimpleDateFormat simpleDateFormat;


                            if (!borrowedDateString.isEmpty()) {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
                                date = simpleDateFormat.parse(borrowedEntry.getString("duedate"));
                            }
                        }

                        PaiaDocument document = new PaiaDocument();
                        document.setStatus(status);

                        document.setItem(borrowedEntry.has("item") ? borrowedEntry.getString("item") : "");
                        document.setEdition(borrowedEntry.has("edition") ? borrowedEntry.getString("edition") : "");
                        document.setRequested(borrowedEntry.has("requested") ? borrowedEntry.getString("requested") : "");
                        document.setAbout(borrowedEntry.has("about") ? borrowedEntry.getString("about") : "");
                        document.setLabel(borrowedEntry.has("label") ? borrowedEntry.getString("label") : "");
                        document.setQueue(borrowedEntry.has("queue") ? borrowedEntry.getInt("queue") : 0);
                        document.setRenewals(borrowedEntry.has("renewals") ? borrowedEntry.getInt("renewals") : 0);
                        document.setReminder(borrowedEntry.has("reminder") ? borrowedEntry.getInt("reminder") : 0);
                        document.setDueDate(date);
                        document.setCanCancel(borrowedEntry.has("cancancel") ? borrowedEntry.getBoolean("cancancel") : true);
                        document.setCanRenew(borrowedEntry.has("canrenew") ? borrowedEntry.getBoolean("canrenew") : true);
                        document.setError(borrowedEntry.has("error") ? borrowedEntry.getString("error") : "");
                        document.setStorage(borrowedEntry.has("storage") ? borrowedEntry.getString("storage") : "");
                        document.setStorageId(borrowedEntry.has("storageid") ? borrowedEntry.getString("storageid") : "");

                        response.add(document);
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
