package de.eww.bibapp.tasks.paia;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.account.AccountBookedFragment;
import de.eww.bibapp.util.PrefUtils;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia cancel task
*/
public class PaiaCancelTask extends AbstractPaiaTask
{
    private Fragment fragment;

	public PaiaCancelTask(Fragment fragment, Activity activity, AsyncCanceledInterface asyncCanceledImplementer)
	{
		super(activity, asyncCanceledImplementer);

        this.fragment = fragment;
	}

	@Override
	protected JSONObject doInBackground(String... params)
	{
		String jsonString = params[0];

		// get url
		int localCatalogIndex = PrefUtils.getLocalCatalogIndex(activity);
		String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/core/" + PaiaHelper.getInstance().getUsername() + "/cancel?access_token=" + PaiaHelper.getInstance().getAccessToken();

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
		((AccountBookedFragment) this.fragment).onRenew(result);
	}
}
