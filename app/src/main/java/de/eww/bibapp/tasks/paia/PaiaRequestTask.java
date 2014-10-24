//package de.eww.bibapp.tasks.paia;
//
//import android.content.SharedPreferences;
//
//import org.json.JSONObject;
//
//import de.eww.bibapp.PaiaHelper;
//import de.eww.bibapp.constants.Constants;
//import de.eww.bibapp.fragments.detail.DetailFragment;
//
///**
// * @author Christoph Schönfeld - effective WEBWORK GmbH
// *
// * This file is part of the Android BibApp Project
// * =========================================================
// * performs paia request task
// */
//public class PaiaRequestTask extends AbstractPaiaTask
//{
//	public PaiaRequestTask(DetailFragment fragment)
//	{
//		super(fragment);
//	}
//
//	@Override
//	protected JSONObject doInBackground(String... params)
//	{
//		String jsonString = params[0];
//
//		// get url
//		SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
//		int spinnerValue = settings.getInt("local_catalog", Constants.LOCAL_CATALOG_DEFAULT);
//
//		String paiaUrl = Constants.getPaiaUrl(spinnerValue) + "/core/" + PaiaHelper.getInstance().getUsername() + "/request?access_token=" + PaiaHelper.getInstance().getAccessToken();
//
//		JSONObject paiaResponse = new JSONObject();
//
//		try
//		{
//			this.setPostParameters(jsonString);
//			paiaResponse = this.performRequest(paiaUrl);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//
//			this.raiseFailure();
//		}
//
//		return paiaResponse;
//	}
//
//	@Override
//	protected void onPostExecute(JSONObject result)
//	{
//		((DetailFragment) this.fragment).onRequest(result);
//	}
//}
