//package de.eww.bibapp;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import de.eww.bibapp.fragments.dialogs.LoginDialogFragment;
//import de.eww.bibapp.tasks.paia.PaiaLoginTask;
//
//public class PaiaHelper implements LoginDialogFragment.LoginDialogListener
//{
//    private static PaiaHelper instance = new PaiaHelper();
//
//    public enum SCOPES {
//        READ_PATRON,
//        READ_FEES,
//        READ_ITEMS,
//        WRITE_ITEMS
//    }
//
//	private  String accessToken = null;
//	private  String username = null;
//	private  Date accessTokenDate = null;
//    private  List<SCOPES> scopes;
//
//    private Fragment fragment;
//    private List<PaiaListener> listener;
//
//	public interface PaiaListener
//	{
//		public void onPaiaConnected();
//	}
//
//	private PaiaHelper()
//	{
//        this.listener = new ArrayList<PaiaListener>();
//        this.reset();
//	}
//
//    public static PaiaHelper getInstance() {
//        return PaiaHelper.instance;
//    }
//
//	public String getAccessToken()
//	{
//		return this.accessToken;
//	}
//
//	public void updateAccessTokenDate(int expiresIn)
//	{
//        Date now = new Date();
//		this.accessTokenDate = new Date(now.getTime() + expiresIn * 1000);
//	}
//
//	public String getUsername()
//	{
//		return this.username;
//	}
//
//    public boolean hasScope(SCOPES scope) {
//        return this.scopes.contains(scope);
//    }
//
//	public void reset()
//	{
//		this.accessToken = null;
//        this.username = null;
//        this.accessTokenDate = null;
//        this.scopes = new ArrayList<SCOPES>();
//        this.listener.clear();
//	}
//
//	public synchronized void ensureConnection(Fragment fragment)
//	{
//        // register the listener for later callback
//        if (fragment instanceof PaiaListener) {
//            this.listener.add((PaiaListener) fragment);
//        }
//
//        // if another thread is already trying to login the user,
//        // don't try it again
//        if (this.listener.size() == 1) {
//            this.fragment = fragment;
//
//            // if we do not already have an access token or the token is expired
//            Date now = new Date();
//            if (this.accessToken == null || ( this.accessTokenDate != null && now.after(this.accessTokenDate))) {
//                Log.v("PAIA", "not logged in or token expired");
//
//                // if login data is not stored or credentials are not set, ask user for them
//                boolean showLoginDialog = false;
//
//                SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
//                boolean storeLogin = settings.getBoolean("store_login", false);
//                showLoginDialog = !storeLogin;
//
//                if ( !showLoginDialog )
//                {
//                    showLoginDialog =	(settings.getString("store_login_username", null) == null) ||
//                                        (settings.getString("store_login_password", null) == null);
//                }
//
//                //this.reset();
//
//                if ( showLoginDialog )
//                {
//                    LoginDialogFragment dialogFragment = new LoginDialogFragment();
//                    dialogFragment.setListener(this);
//                    dialogFragment.show(this.fragment.getChildFragmentManager(), "login");
//                }
//                else
//                {
//                    this.username = settings.getString("store_login_username", null);
//
//                    // login
//                    AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.fragment).execute(this.username, settings.getString("store_login_password", null));
//
//                    try
//                    {
//                        JSONObject loginResponse = loginTask.get();
//                        this.accessToken = loginResponse.getString("access_token");
//                        this.setScopes(loginResponse.getString("scopes"));
//                        this.updateAccessTokenDate(loginResponse.getInt("expires_in"));
//
//                    }
//                    catch (Exception e)
//                    {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    this.connected();
//                }
//            } else {
//                this.connected();
//            }
//        }
//	}
//
//    private void connected() {
//        // call the registered listener and reset the list
//        for (PaiaListener l : this.listener) {
//            l.onPaiaConnected();
//        }
//
//        this.listener.clear();
//    }
//
//	@Override
//	public void onLoginDialogPositiveClick(DialogFragment dialog, boolean storeData)
//	{
//		View dialogView = ((LoginDialogFragment) dialog).getDialogView();
//
//		EditText usernameText = (EditText) dialogView.findViewById(R.id.logindialog_username);
//		EditText passwordText = (EditText) dialogView.findViewById(R.id.logindialog_password);
//
//		String username = usernameText.getText().toString();
//		String password = passwordText.getText().toString();
//
//		// verify input
//		if ( !username.trim().isEmpty() && !password.trim().isEmpty() )
//		{
//            this.username = username;
//
//			// perform login
//			AsyncTask<String, Void, JSONObject> loginTask = new PaiaLoginTask(this.fragment).execute(username, password);
//
//			try
//			{
//                JSONObject loginResponse = loginTask.get();
//				String accessToken = loginResponse.getString("access_token");
//
//				if ( accessToken.isEmpty() )
//				{
//					// login was wrong - update dialog
//					((LoginDialogFragment) dialog).setWrongLogin();
//				}
//				else
//				{
//                    this.accessToken = accessToken;
//                    this.setScopes(loginResponse.getString("scopes"));
//
//					// force soft keyboard to hide
//					InputMethodManager imm = (InputMethodManager) this.fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);
//
//					// store login credentials - if set in preferecnes
//					// or the checkbox was set in the login dialog
//					SharedPreferences settings = this.fragment.getActivity().getPreferences(0);
//				    boolean storeLoginCredentials = settings.getBoolean("store_login", false) || storeData;
//
//				    if ( storeLoginCredentials )
//				    {
//				    	SharedPreferences.Editor editor = settings.edit();
//				    	editor.putString("store_login_username", username);
//				    	editor.putString("store_login_password", password);
//
//				    	if ( storeData == true )
//				    	{
//				    		editor.putBoolean("store_login", true);
//				    	}
//
//				    	editor.commit();
//				    }
//
//                    this.updateAccessTokenDate(loginResponse.getInt("expires_in"));
//
//                    this.connected();
//
//					// close dialog
//					dialog.dismiss();
//				}
//			}
//			catch (Exception e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public void onLoginDialogNegativeClick(DialogFragment dialog)
//	{
//		View dialogView = ((LoginDialogFragment) dialog).getDialogView();
//
//		EditText usernameText = (EditText) dialogView.findViewById(R.id.logindialog_username);
//
//		// force soft keyboard to hide
//		InputMethodManager imm = (InputMethodManager) this.fragment.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(usernameText.getWindowToken(), 0);
//
//		dialog.getDialog().cancel();
//        this.reset();
//
//		if ( !this.fragment.getClass().getName().equals("de.eww.bibapp.fragments.DetailFragment") )
//		{
//			// change main navigation tab
//			MainActivity mainActivity = (MainActivity) this.fragment.getActivity();
//
//			CustomFragmentTabHost mainTabHost = (CustomFragmentTabHost) mainActivity.findViewById(R.id.main_tabhost);
//			mainTabHost.setCurrentTab(0);
//		}
//	}
//
//    private void setScopes(String scopesString) {
//        this.scopes.clear();
//        String[] scopes = scopesString.split(" ");
//
//        if (scopes.length > 0) {
//            for (String scope: scopes) {
//                if (scope.equals("read_patron")) {
//                    this.scopes.add(SCOPES.READ_PATRON);
//                } else if (scope.equals("read_fees")) {
//                    this.scopes.add(SCOPES.READ_FEES);
//                } else if (scope.equals("read_items")) {
//                    this.scopes.add(SCOPES.READ_ITEMS);
//                } else if (scope.equals("write_items")) {
//                    this.scopes.add(SCOPES.WRITE_ITEMS);
//                }
//            }
//        }
//    }
//}
