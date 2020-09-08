package de.eww.bibapp.tasks.paia;

import android.content.Context;
import androidx.fragment.app.Fragment;
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
import de.eww.bibapp.model.PaiaItem;
import de.eww.bibapp.tasks.AbstractLoader;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.util.UrlHelper;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Gets a list of borrowed items from paia
*/
public class BorrowedJsonLoader extends AbstractLoader<PaiaItem>
{

    Context mContext;

	public BorrowedJsonLoader(Context context, Fragment callingFragment)
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
	public List<PaiaItem> loadInBackground()
	{
		List<PaiaItem> response = new ArrayList<PaiaItem>();

		// get url
		String paiaUrl = UrlHelper.getPaiaUrl(mContext) + "/core/" + PaiaHelper.getInstance().getUsername() + "/items?access_token=" + PaiaHelper.getInstance().getAccessToken();
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(paiaUrl, mContext);

		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);

			InputStream inputStream = urlConnectionHelper.getStream();

			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("PAIA", httpResponse);

            JSONObject paiaResponse = new JSONObject(httpResponse);

            if (paiaResponse.has("error")) {
                throw new Exception();
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
                        String[] acceptedDateFormats = {
                            "yyyy-MM-dd",
                            "yyyy-MM-dd'T'HH:mm:sszzz"
                        };

                        Date date = null;
                        if (borrowedEntry.has("duedate")) {
                            String borrowedDateString = borrowedEntry.getString("duedate");

                            if (!borrowedDateString.isEmpty()) {
                                for (String format: acceptedDateFormats) {
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.GERMANY);
                                        date = sdf.parse(borrowedDateString);
                                        break;
                                    } catch (ParseException e) {
                                    }
                                }
                            }
                        }

                        Date startDate = null;
                        if (borrowedEntry.has("starttime")) {
                            String startTime = borrowedEntry.getString("starttime");

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
                        if (borrowedEntry.has("endtime")) {
                            String endTime = borrowedEntry.getString("endtime");

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

                        PaiaItem document = new PaiaItem();
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
                        document.setStartDate(startDate);
                        document.setEndDate(endDate);
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
