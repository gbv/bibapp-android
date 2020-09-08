package de.eww.bibapp.tasks.paia;

import android.app.Activity;

import org.json.JSONObject;

import java.net.URLEncoder;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.util.UrlHelper;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia login task
*/
public class PaiaLoginTask extends AbstractPaiaTask
{
	public PaiaLoginTask(Activity activity, AsyncCanceledInterface asyncCanceledImplementer)
	{
		super(activity, asyncCanceledImplementer);
	}

	@Override
	protected JSONObject doInBackground(String... params)
	{
		String username = params[0];
		String password = params[1];

		JSONObject result = new JSONObject();

		// get url
		try
		{
			username = URLEncoder.encode(username, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");

            String paiaUrl = UrlHelper.getPaiaUrl(activity) + "/auth/login?username=" + username + "&password=" + password + "&grant_type=password";

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

				if (paiaResponse.has("patron")) {
					result.put("patron", paiaResponse.get("patron"));
				}
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
