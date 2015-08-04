package de.eww.bibapp.tasks.paia;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.URLConnectionHelper;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Abstract task class for paia actions
*/
abstract public class AbstractPaiaTask extends AsyncTask<String, Void, JSONObject>
{
	private String postParameters = "";
    private AsyncCanceledInterface asyncCanceledImplementer;
	protected Activity activity;

	public AbstractPaiaTask(Activity activity, AsyncCanceledInterface asyncCanceledImplementer)
	{
		this.activity = activity;
        this.asyncCanceledImplementer = asyncCanceledImplementer;
	}

	protected void raiseFailure()
	{
        asyncCanceledImplementer.onAsyncCanceled();
	}

	public void setPostParameters(String postParameters)
	{
		this.postParameters = postParameters;
	}

	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and return the result of the action
     */
	public JSONObject performRequest(String paiaUrl) throws Exception
	{
		JSONObject response = new JSONObject();
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(paiaUrl, activity);

		try
		{
			// open connection
			urlConnectionHelper.configure();

			if ( this.postParameters.isEmpty() )
			{
				urlConnectionHelper.connect(null);
			}
			else
			{
				urlConnectionHelper.connect(this.postParameters);
			}

			InputStream inputStream = urlConnectionHelper.getStream();

			String httpResponse = urlConnectionHelper.readStream(inputStream);
			Log.v("PAIA", httpResponse);

			if ( httpResponse.substring(0, 1).equals("[") )
			{
				response = new JSONObject();
				response.put("array", new JSONArray(httpResponse));
			}
			else
			{
				response = new JSONObject(httpResponse);
			}
		}
		finally
		{
			urlConnectionHelper.disconnect();
		}

		return response;
	}
}
