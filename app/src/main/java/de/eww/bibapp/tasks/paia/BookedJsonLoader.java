package de.eww.bibapp.tasks.paia;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
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
 * Gets a list of booked items from paia
 */
public class BookedJsonLoader extends AbstractLoader<PaiaDocument>
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
				JSONArray bookedArray = paiaResponse.getJSONArray("doc");
				int bookedArrayLength = bookedArray.length();
				for ( int i=0; i < bookedArrayLength; i++ )
				{
					JSONObject bookedEntry = (JSONObject) bookedArray.get(i);
					
					// booked items have status unequal to 2, 3 or 4
					int status = bookedEntry.getInt("status");
					if ( status != 2 && status != 3 && status != 4 )
					{
                        String[] acceptedDateFormats = {
                            "yyyy-MM-dd'T'HH:mm:sszzz",
                            "yyyy-MM-dd"

                        };

                        Date date = null;
                        if (bookedEntry.has("duedate")) {
                            String bookedDateString = bookedEntry.getString("duedate");

                            if (!bookedDateString.isEmpty()) {
                                for (String format: acceptedDateFormats) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);
                                        date = sdf.parse(bookedDateString);
                                        break;
                                    } catch (ParseException e) {
                                    }
                                }
                            }
                        }

                        Date startDate = null;
                        if (bookedEntry.has("starttime")) {
                            String startTime = bookedEntry.getString("starttime");

                            if (!startTime.isEmpty()) {
                                for (String format: acceptedDateFormats) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);
                                        startDate = sdf.parse(startTime);
                                        break;
                                    } catch (ParseException e) {
                                    }
                                }
                            }
                        }

                        Date endDate = null;
                        if (bookedEntry.has("endtime")) {
                            String endTime = bookedEntry.getString("endtime");

                            if (!endTime.isEmpty()) {
                                for (String format: acceptedDateFormats) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);
                                        endDate = sdf.parse(endTime);
                                        break;
                                    } catch (ParseException e) {
                                    }
                                }
                            }
                        }

                        PaiaDocument document = new PaiaDocument();
                        document.setStatus(status);

                        document.setItem(bookedEntry.has("item") ? bookedEntry.getString("item") : "");
                        document.setEdition(bookedEntry.has("edition") ? bookedEntry.getString("edition") : "");
                        document.setRequested(bookedEntry.has("requested") ? bookedEntry.getString("requested") : "");
                        document.setAbout(bookedEntry.has("about") ? bookedEntry.getString("about") : "");
                        document.setLabel(bookedEntry.has("label") ? bookedEntry.getString("label") : "");
                        document.setQueue(bookedEntry.has("queue") ? bookedEntry.getInt("queue") : 0);
                        document.setRenewals(bookedEntry.has("renewals") ? bookedEntry.getInt("renewals") : 0);
                        document.setReminder(bookedEntry.has("reminder") ? bookedEntry.getInt("reminder") : 0);
                        document.setDueDate(date);
                        document.setStartDate(startDate);
                        document.setEndDate(endDate);
                        document.setCanCancel(bookedEntry.has("cancancel") ? bookedEntry.getBoolean("cancancel") : true);
                        document.setCanRenew(bookedEntry.has("canrenew") ? bookedEntry.getBoolean("canrenew") : true);
                        document.setError(bookedEntry.has("error") ? bookedEntry.getString("error") : "");
                        document.setStorage(bookedEntry.has("storage") ? bookedEntry.getString("storage") : "");
                        document.setStorageId(bookedEntry.has("storageid") ? bookedEntry.getString("storageid") : "");

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
