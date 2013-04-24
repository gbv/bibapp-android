package de.eww.bibapp.fragments.dialogs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import de.eww.bibapp.R;

public class DetailActionsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener
{
	// Use this instance of the interface to deliver action events
	DetailActionsDialogLisener mListener;
    
    ArrayList<String> actionList;
    HashMap<Integer, String> actionMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
	public interface DetailActionsDialogLisener
	{
		public void onActionLocation(DialogFragment dialog);
		public void onActionRequest(DialogFragment dialog);
		public void onActionOrder(DialogFragment dialog);
		public void onActionOnline(DialogFragment dialog);
    }
	
	public void setActionList(ArrayList<String> actionList)
	{
		this.actionList = actionList;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    Fragment listenerFragment = DetailActionsDialogFragment.this.getParentFragment();
	    mListener = (DetailActionsDialogLisener) listenerFragment;
	    
	    ArrayList<String> displayList = new ArrayList<String>();
	    this.actionMap = new HashMap<Integer, String>();
	    Resources resources = this.getResources();
	    int index = 0;
	    if ( actionList.contains("location") )
	    {
	    	displayList.add(resources.getString(R.string.detailactionsdialog_location));
	    	this.actionMap.put(index++, "location");
	    }
	    if ( actionList.contains("request") )
	    {
	    	displayList.add(resources.getString(R.string.detailactionsdialog_request));
	    	this.actionMap.put(index++, "request");
	    }
	    if ( actionList.contains("order") )
	    {
	    	displayList.add(resources.getString(R.string.detailactionsdialog_order));
	    	this.actionMap.put(index++, "order");
	    }
	    if ( actionList.contains("online") )
	    {
	    	displayList.add(resources.getString(R.string.detailactionsdialog_online));
	    	this.actionMap.put(index++, "online");
	    }
	    
	    builder
	    // Add action buttons
           .setNegativeButton(R.string.detailactionsdialog_cancel, null)
           .setTitle(R.string.detailactionsdialog_heading)
           .setItems(displayList.toArray(new CharSequence[displayList.size()]), this);
	    
	    return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		String label = this.actionMap.get(which);
		
		if ( label.equals("location") )
		{
			this.mListener.onActionLocation(this);
		}
		if ( label.equals("request") )
		{
			this.mListener.onActionRequest(this);
		}
		if ( label.equals("order") )
		{
			this.mListener.onActionOrder(this);
		}
		if ( label.equals("online") )
		{
			this.mListener.onActionOnline(this);
		}
	}
}