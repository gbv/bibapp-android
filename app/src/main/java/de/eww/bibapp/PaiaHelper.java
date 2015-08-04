package de.eww.bibapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.fragment.dialog.LoginDialogFragment;
import de.eww.bibapp.tasks.paia.PaiaLoginTask;

public class PaiaHelper implements LoginDialogFragment.LoginDialogListener
{
    private static PaiaHelper instance = new PaiaHelper();

    public enum SCOPES {
        READ_PATRON,
        READ_FEES,
        READ_ITEMS,
        WRITE_ITEMS
    }

	private  String accessToken = null;
	private  String username = null;
	private  Date accessTokenDate = null;
    private  List<SCOPES> scopes;

    private FragmentActivity activity;
    private List<PaiaListener> listener;

	public interface PaiaListener
	{
		void onPaiaConnected();
	}

	private PaiaHelper()
	{
        this.listener = new ArrayList<PaiaListener>();
        this.reset();
	}

    public static PaiaHelper getInstance() {
        return PaiaHelper.instance;
    }

	public String getAccessToken()
	{
		return this.accessToken;
	}

	public void updateAccessTokenDate(int expiresIn)
	{
        Date now = new Date();
		this.accessTokenDate = new Date(now.getTime() + expiresIn * 1000);
	}

	public String getUsername()
	{
		return this.username;
	}

    public boolean hasScope(SCOPES scope) {
        return this.scopes.contains(scope);
    }

	public void reset()
	{
		this.accessToken = null;
        this.username = null;
        this.accessTokenDate = null;
        this.scopes = new ArrayList<SCOPES>();
        this.listener.clear();
	}

	public synchronized void ensureConnection(PaiaListener listener, FragmentActivity activity, AsyncCanceledInterface asyncCanceledImplementer)
	{
        // register the listener for later callback
        this.listener.add(listener);

        // if another thread is already trying to login the user,
        // don't try it again
        if (this.listener.size() == 1) {
            this.activity = activity;

            // if we do not already have an access token or the token is expired
            Date now = new Date();
            if (this.accessToken == null || ( this.accessTokenDate != null && now.after(this.accessTokenDate))) {
                Log.v("PAIA", "not logged in or token expired");

                // if login data is not stored or credentials are not set, ask user for them
                boolean showLoginDialog = false;

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                boolean storeLogin = sharedPref.getBoolean(SettingsActivity.KEY_PREF_STORE_LOGIN, false);

                // storeLogin could be true without any stored login data, if the user disables and reenables
                // the corresponding option in the settings activity
                String usernameForCheck = sharedPref.getString("store_login_username", null);

                showLoginDialog = !storeLogin || usernameForCheck == null;

                if ( showLoginDialog )
                {
                    LoginDialogFragment dialogFragment = new LoginDialogFragment();
                    dialogFragment.setCancelable(false);
                    dialogFragment.setListener(this);
                    dialogFragment.show(this.activity.getSupportFragmentManager(), "login");
                } else {
                    this.username = sharedPref.getString("store_login_username", null);

                    // login
                    AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.activity, (AsyncCanceledInterface) this.activity).execute(this.username, sharedPref.getString("store_login_password", null));

                    try
                    {
                        JSONObject loginResponse = loginTask.get();
                        this.accessToken = loginResponse.getString("access_token");
                        this.setScopes(loginResponse.getString("scopes"));
                        this.updateAccessTokenDate(loginResponse.getInt("expires_in"));

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    this.connected();
                }
            } else {
                this.connected();
            }
        }
	}

    private void connected() {
        // Important: Copy the list of registered listener and clear the original one
        // to prevent cyclic problems
        List<PaiaListener> connectionListener = new ArrayList<PaiaListener>();
        connectionListener.addAll(listener);

        listener.clear();

        // call the registered listener and reset the list
        for (PaiaListener l : connectionListener) {
            l.onPaiaConnected();
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
            this.username = username;

			// perform login
			AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.activity, (AsyncCanceledInterface) this.activity).execute(username, password);

			try
			{
                JSONObject loginResponse = loginTask.get();

                if (!loginResponse.has("access_token") || loginResponse.getString("access_token").isEmpty()) {
                    // login was wrong - update dialog
                    ((LoginDialogFragment) dialog).setWrongLogin();
                } else {
                    accessToken = loginResponse.getString("access_token");
                    this.setScopes(loginResponse.getString("scopes"));

					// force soft keyboard to hide
					InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);

					// store login credentials - if set in preferences
					// or the checkbox was set in the login dialog
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.activity);
                    boolean storeLoginCredentials = sharedPref.getBoolean(SettingsActivity.KEY_PREF_STORE_LOGIN, false) || storeData;

				    if ( storeLoginCredentials ) {
				    	SharedPreferences.Editor editor = sharedPref.edit();
				    	editor.putString("store_login_username", username);
				    	editor.putString("store_login_password", password);

				    	if ( storeData == true ) {
				    		editor.putBoolean(SettingsActivity.KEY_PREF_STORE_LOGIN, true);
				    	}

				    	editor.commit();
				    }

                    this.updateAccessTokenDate(loginResponse.getInt("expires_in"));

                    this.connected();

					// close dialog
					dialog.dismiss();
				}
			} catch (Exception e) {
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
		InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);

		dialog.getDialog().cancel();
        this.reset();

        ((BaseActivity) this.activity).selectItem(0);
	}

    private void setScopes(String scopesString) {
        this.scopes.clear();
        String[] scopes = scopesString.split(" ");

        if (scopes.length > 0) {
            for (String scope: scopes) {
                if (scope.equals("read_patron")) {
                    this.scopes.add(SCOPES.READ_PATRON);
                } else if (scope.equals("read_fees")) {
                    this.scopes.add(SCOPES.READ_FEES);
                } else if (scope.equals("read_items")) {
                    this.scopes.add(SCOPES.READ_ITEMS);
                } else if (scope.equals("write_items")) {
                    this.scopes.add(SCOPES.WRITE_ITEMS);
                }
            }
        }
    }
}
