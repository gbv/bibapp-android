package de.eww.bibapp.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import de.eww.bibapp.R;

public class InsufficentRightsDialogFragment extends DialogFragment
{
    View dialogView = null;
    AlertDialog alertDialog;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    this.dialogView = inflater.inflate(R.layout.dialog_insufficent_rights, null);
	    
	    builder.setView(this.dialogView)
	    // Add action buttons
           .setPositiveButton(R.string.errordialog_proceed, null);
	    
	    this.alertDialog = builder.create();
	    
	    return this.alertDialog;
	}
}