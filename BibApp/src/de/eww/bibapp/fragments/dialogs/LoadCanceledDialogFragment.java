package de.eww.bibapp.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import de.eww.bibapp.R;

public class LoadCanceledDialogFragment extends DialogFragment
{
    View dialogView = null;
    AlertDialog alertDialog;
    
    private static boolean instanceOpen = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		if ( LoadCanceledDialogFragment.instanceOpen == true )
		{
			this.dismiss();
		}
		else
		{
			LoadCanceledDialogFragment.instanceOpen = true;
		}
	}
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		
		LoadCanceledDialogFragment.instanceOpen = false;
	}
	
	public View getDialogView()
	{
		return this.dialogView;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    this.dialogView = inflater.inflate(R.layout.dialog_load_canceled, null);
	    
	    builder.setView(this.dialogView)
	    // Add action buttons
           .setPositiveButton(R.string.errordialog_proceed, null);
	    
	    this.alertDialog = builder.create();
	    
	    return this.alertDialog;
	}
}