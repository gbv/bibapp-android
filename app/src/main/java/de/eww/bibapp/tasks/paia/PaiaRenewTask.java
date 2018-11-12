package de.eww.bibapp.tasks.paia;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.account.AccountBorrowedFragment;
import de.eww.bibapp.util.PrefUtils;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* performs paia renew task
*/
public class PaiaRenewTask extends AbstractPaiaTask
{
	private Fragment fragment;

	public PaiaRenewTask(AccountBorrowedFragment fragment, Activity activity, AsyncCanceledInterface asyncCanceledImplementer)
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
		String paiaUrl = Constants.getPaiaUrl(localCatalogIndex) + "/core/" + PaiaHelper.getInstance().getUsername() + "/renew?access_token=" + PaiaHelper.getInstance().getAccessToken();

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
