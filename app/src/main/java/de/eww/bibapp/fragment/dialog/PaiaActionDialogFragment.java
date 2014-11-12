package de.eww.bibapp.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import de.eww.bibapp.R;

public class PaiaActionDialogFragment extends DialogFragment
{
	// Use this instance of the interface to deliver action events
	PaiaActionDialogListener mListener;
    
    View dialogView = null;
    AlertDialog alertDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
	public interface PaiaActionDialogListener
	{
        public void onActionDialogPositiveClick(DialogFragment dialog);
    }
	
	public View getDialogView()
	{
		return this.dialogView;
	}
	
	public void paiaActionDone(String resultMessage)
	{
		// enable positiv button
		this.alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
		
		// remove loading indicator
		View progressContainer = this.dialogView.findViewById(R.id.progressContainer);
		progressContainer.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_out));
		progressContainer.setVisibility(View.GONE);
		
		// show result textview and display text
		View resultTextView = this.dialogView.findViewById(R.id.paiadialog_result);
		resultTextView.startAnimation(AnimationUtils.loadAnimation(this.getActivity(), android.R.anim.fade_in));
		resultTextView.setVisibility(View.VISIBLE);
		((TextView) resultTextView).setText(resultMessage);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    
	    Fragment listenerFragment = PaiaActionDialogFragment.this.getParentFragment();
	    mListener = (PaiaActionDialogListener) listenerFragment;

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    this.dialogView = inflater.inflate(R.layout.dialog_paia_action, null);
	    
	    builder.setView(this.dialogView)
	    // Add action buttons
           .setPositiveButton(R.string.paiadialog_proceed, null);
	    
	    this.alertDialog = builder.create();
	    
	    // override onClick for positiv button to prevent auto dismiss and send event to host
	    this.alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
	    {
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = PaiaActionDialogFragment.this.alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				b.setEnabled(false);
				b.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v) {
						// Send the positive button event back to the host fragment
						PaiaActionDialogFragment.this.mListener.onActionDialogPositiveClick(PaiaActionDialogFragment.this);
					}
				});
			}
		});
	    
	    return this.alertDialog;
	}
}