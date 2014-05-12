package de.eww.bibapp;

import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import de.eww.bibapp.fragments.dialogs.LoginDialogFragment;
import de.eww.bibapp.tasks.paia.PaiaLoginTask;

public class PaiaHelper implements LoginDialogFragment.LoginDialogListener
{
	private static String accessToken = null;
	private static String username = null;
	private static Date accessTokenDate = null;
	
	private Fragment fragment;
	
	public interface PaiaListener
	{
		public void onPaiaConnected();
	}
	
	public PaiaHelper(Fragment fragment)
	{
		this.fragment = fragment;
	}
	
	public static String getAccessToken()
	{
		return PaiaHelper.accessToken;
	}
	
	public static void updateAccessTokenDate()
	{
		PaiaHelper.accessTokenDate = new Date();
	}
	
	public static String getUsername()
	{
		return PaiaHelper.username;
	}
	
	public static void reset()
	{
		PaiaHelper.accessToken = null;
		PaiaHelper.username = null;
		PaiaHelper.accessTokenDate = null;
	}
	
	public void ensureConnection()
	{
		Date compareDate = null;
		if ( PaiaHelper.accessTokenDate != null )
		{
			compareDate = new Date(PaiaHelper.accessTokenDate.getTime() + (15 * 60 * 1000));
		}
		
		// if we do not already have an access token or the token is expired
		Date now = new Date();
	    if ( PaiaHelper.accessToken == null ||
	    		( compareDate != null && now.after(compareDate)) )
	    {
	    	Log.v("PAIA", "not logged in or token expired");
	    	
	    	// if login data is not stored or credentials are not set, ask user for them
		    boolean showLoginDialog = false;
		    
		    SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
		    showLoginDialog = !settings.getBoolean("store_login", false);
		    
		    if ( !showLoginDialog )
		    {
		    	showLoginDialog =	(settings.getString("store_login_username", null) == null) ||
		    						(settings.getString("store_login_password", null) == null);
		    }
		    
		    if ( showLoginDialog )
		    {
		    	LoginDialogFragment dialogFragment = new LoginDialogFragment();
		    	dialogFragment.setListener(this);
				dialogFragment.show(this.fragment.getChildFragmentManager(), "login");
		    }
		    else
		    {
		    	PaiaHelper.username = settings.getString("store_login_username", null);
		    	
		    	// login
		    	AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.fragment).execute(PaiaHelper.username, settings.getString("store_login_password", null));
		    	
				try
				{
					JSONObject accessToken = loginTask.get();
					PaiaHelper.accessToken = accessToken.getString("access_token");
					PaiaHelper.updateAccessTokenDate();
					
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				((PaiaListener) this.fragment).onPaiaConnected();
		    }
	    }
	    else
	    {
	    	((PaiaListener) this.fragment).onPaiaConnected();
	    }
	}
	
	@Override
	public void onLoginDialogPositiveClick(DialogFragment dialog, boolean storeData)
	{
		View dialogView = ((LoginDialogFragment) dialog).getDialogView();
		
		EditText usernameText = (EditText) dialogView.findViewById(R.id.logindialog_username);
		EditText passwordText = (EditText) dialogView.findViewById(R.id.logindialog_password);
		
		String username = usernameText.getText().toString();
		String password = passwordText.getText().toString();
		
		// verify input
		if ( !username.trim().isEmpty() && !password.trim().isEmpty() )
		{
			PaiaHelper.username = username;
			
			// perform login
			AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.fragment).execute(username, password);
			
			try
			{
				JSONObject accessTokenObject = loginTask.get();
				String accessToken = accessTokenObject.getString("access_token");
				
				if ( accessToken.isEmpty() )
				{
					// login was wrong - update dialog
					((LoginDialogFragment) dialog).setWrongLogin();
				}
				else
				{
					PaiaHelper.accessToken = accessToken;
					
					// force soft keyboard to hide
					InputMethodManager imm = (InputMethodManager) this.fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);
					
					// store login credentials - if set in preferecnes
					// or the checkbox was set in the login dialog
					SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
				    boolean storeLoginCredentials = settings.getBoolean("store_login", false) || storeData;
				    
				    if ( storeLoginCredentials )
				    {
				    	SharedPreferences.Editor editor = settings.edit();
				    	editor.putString("store_login_username", username);
				    	editor.putString("store_login_password", password);
				    	
				    	if ( storeData == true )
				    	{
				    		editor.putBoolean("store_login", true);
				    	}
				    	
				    	editor.commit();
				    }
				    
				    PaiaHelper.updateAccessTokenDate();
				    
				    ((PaiaListener) this.fragment).onPaiaConnected();
					
					// close dialog
					dialog.dismiss();
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLoginDialogNegativeClick(DialogFragment dialog)
	{
		View dialogView = ((LoginDialogFragment) dialog).getDialogView();
		
		EditText usernameText = (EditText) dialogView.findViewById(R.id.logindialog_username);
		
		// force soft keyboard to hide
		InputMethodManager imm = (InputMethodManager) this.fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);
		
		dialog.getDialog().cancel();
		
		if ( !this.fragment.getClass().getName().equals("de.eww.bibapp.fragments.DetailFragment") )
		{
			// change main navigation tab
			MainActivity mainActivity = (MainActivity) this.fragment.getActivity();
			
			CustomFragmentTabHost mainTabHost = (CustomFragmentTabHost) mainActivity.findViewById(R.id.main_tabhost);
			mainTabHost.setCurrentTab(0);
		}
	}
}
