package de.eww.bibapp.tasks.paia;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import java.net.URLEncoder;

import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.constants.Constants;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia login task
*/
public class PaiaLoginTask extends AbstractPaiaTask
{
	public PaiaLoginTask(Fragment callingFragment)
	{
		super(callingFragment);
	}

	@Override
	protected JSONObject doInBackground(String... params)
	{
		String username = params[0];
		String password = params[1];

		JSONObject result = new JSONObject();

		// get url
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
        String localCatalogPreference = sharedPreferences.getString(SettingsActivity.KEY_PREF_LOCAL_CATALOG, "");
        int localCatalogIndex = 0;
        if (!localCatalogPreference.isEmpty()) {
            localCatalogIndex = Integer.valueOf(localCatalogPreference);
        }

		try
		{
			username = URLEncoder.encode(username, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");

            String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/auth/login?username=" + username + "&password=" + password + "&grant_type=password";

			JSONObject paiaResponse = this.performRequest(paiaUrl);

			if ( paiaResponse.has("error") ){
                if (paiaResponse.get("error").equals("access_denied")) {
                    // wrong login - return json object with empty access token
                    result.put("access_token", "");
                }
			} else {
				// login correct - store data
				result.put("access_token", paiaResponse.getString("access_token"));
                result.put("scopes", paiaResponse.getString("scope"));
                result.put("expires_in", paiaResponse.getString("expires_in"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			this.raiseFailure();
		}

		return result;
	}
}
