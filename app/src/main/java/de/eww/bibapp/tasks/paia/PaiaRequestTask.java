package de.eww.bibapp.tasks.paia;

import android.app.Activity;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.util.PrefUtils;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia request task
*/
public class PaiaRequestTask extends AbstractPaiaTask {

	private Fragment fragment;

	public PaiaRequestTask(ModsFragment fragment, Activity activity, AsyncCanceledInterface asyncCanceledImplementer) {
		super(activity, asyncCanceledImplementer);

		this.fragment = fragment;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		String jsonString = params[0];

		// get url
		int localCatalogIndex = PrefUtils.getLocalCatalogIndex(activity);
		String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/core/" + PaiaHelper.getInstance().getUsername() + "/request?access_token=" + PaiaHelper.getInstance().getAccessToken();

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
	protected void onPostExecute(JSONObject result) {
		((ModsFragment) this.fragment).onPaiaRequestActionDone(result);
	}
}