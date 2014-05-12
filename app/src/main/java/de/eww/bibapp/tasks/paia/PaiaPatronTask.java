package de.eww.bibapp.tasks.paia;

import org.json.JSONObject;

import android.content.SharedPreferences;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragments.AccountFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * performs paia patron task
 */
public class PaiaPatronTask extends AbstractPaiaTask
{
	public PaiaPatronTask(AccountFragment fragment)
	{
		super(fragment);
	}
	
	@Override
	protected JSONObject doInBackground(String... params)
	{
		// get url
		SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
		
		String paiaUrl = Constants.getPaiaUrl(spinnerValue) + "/core/" + PaiaHelper.getUsername() + "/?access_token=" + PaiaHelper.getAccessToken();
		
		JSONObject paiaResponse = new JSONObject();
		
		try
		{
			paiaResponse = this.performRequest(paiaUrl);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			this.raiseFailure();
		}
		
		return paiaResponse;
	}
	
	@Override
	protected void onPostExecute(JSONObject result)
	{
		((AccountFragment) this.fragment).onPatronLoaded(result);
	}
}
