package de.eww.bibapp.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import de.eww.bibapp.R;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.util.PrefUtils;

public class LoginDialogFragment extends DialogFragment
{
	// Use this instance of the interface to deliver action events
    LoginDialogListener mListener;
    
    View dialogView = null;
    
    public void setListener(LoginDialogListener listener)
    {
    	this.mListener = listener;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
	public interface LoginDialogListener
	{
        public void onLoginDialogPositiveClick(DialogFragment dialog, boolean storeData);
        public void onLoginDialogNegativeClick(DialogFragment dialog);
    }
	
	public View getDialogView()
	{
		return this.dialogView;
	}
	
	public void setWrongLogin()
	{
		TextView wrongLoginView = (TextView) this.dialogView.findViewById(R.id.logindialog_wrong);
		wrongLoginView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    this.dialogView = inflater.inflate(R.layout.dialog_signin, null);
	    
	    builder.setView(this.dialogView)
	    // Add action buttons
           .setPositiveButton(R.string.logindialog_login, null)
           .setNegativeButton(R.string.logindialog_cancel, new DialogInterface.OnClickListener()
           {
               public void onClick(DialogInterface dialog, int id)
               {
            	   // Send the negative button event back to the host fragment
            	   LoginDialogFragment.this.mListener.onLoginDialogNegativeClick(LoginDialogFragment.this);
               }
           });

		boolean storeLogin = PrefUtils.isLoginStored(getActivity());
		if (storeLogin == false) {
			CheckBox checkBox = (CheckBox) this.dialogView.findViewById(R.id.logindialog_save);
			checkBox.setVisibility(View.VISIBLE);
		}
	    
	    final AlertDialog alertDialog = builder.create();
	    
	    // override onClick for positiv button to prevent auto dismiss and send event to host
	    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
	    {
			@Override
			public void onShow(DialogInterface dialog)
			{
				Button b = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						boolean storeData = false;
						
						CheckBox checkBox = (CheckBox) LoginDialogFragment.this.dialogView.findViewById(R.id.logindialog_save);
						if ( checkBox.isChecked() )
						{
							storeData = true;
						}
						
						// Send the positive button event back to the host fragment
						LoginDialogFragment.this.mListener.onLoginDialogPositiveClick(LoginDialogFragment.this, storeData);
					}
				});
			}
		});
	    
	    return alertDialog;
	}
}