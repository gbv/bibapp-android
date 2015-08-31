package de.eww.bibapp.tasks.paia;

import android.app.Activity;

import org.json.JSONObject;

import java.net.URLEncoder;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.util.PrefUtils;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia login task
*/
public class PaiaLogoutTask extends AbstractPaiaTask
{
	public PaiaLogoutTask(Activity activity, AsyncCanceledInterface asyncCanceledImplementer)
	{
		super(activity, asyncCanceledImplementer);
	}

	@Override
	protected JSONObject doInBackground(String... params)
	{
		String patron = params[0];

		JSONObject result = new JSONObject();

		// get url
		int localCatalogIndex = PrefUtils.getLocalCatalogIndex(activity);

		try
		{
			patron = URLEncoder.encode(patron, "UTF-8");

            String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/auth/logout?access_token=" + PaiaHelper.getInstance().getAccessToken() + "&patron=" + patron;

			result = this.performRequest(paiaUrl);
		}
		catch (Exception e)
		{
			e.printStackTrace();

			this.raiseFailure();
		}

		return result;
	}
}
