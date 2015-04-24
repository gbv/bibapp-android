package de.eww.bibapp.tasks.paia;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.account.AccountFragment;

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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

		String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/core/" + PaiaHelper.getInstance().getUsername() + "?access_token=" + PaiaHelper.getInstance().getAccessToken();

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
