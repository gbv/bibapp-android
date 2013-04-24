package de.eww.bibapp.tasks.paia;

import org.json.JSONObject;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragments.AccountBorrowedFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * performs paia renew task
 */
public class PaiaRenewTask extends AbstractPaiaTask
{
	public PaiaRenewTask(AccountBorrowedFragment fragment)
	{
		super(fragment);
	}
	
	@Override
	protected JSONObject doInBackground(String... params)
	{
		String jsonString = params[0];
		
		// get url
		String paiaUrl = Constants.PAIA_URL + "/core/" + PaiaHelper.getUsername() + "/renew?access_token=" + PaiaHelper.getAccessToken();
		
		JSONObject paiaResponse = new JSONObject();
		
		try
		{
			this.setPostParameters(jsonString);
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
		((AccountBorrowedFragment) this.fragment).onRenew(result);
	}
}
