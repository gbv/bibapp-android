package de.eww.bibapp.fragments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapters.BookedAdapter;
import de.eww.bibapp.data.BookedEntry;
import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
import de.eww.bibapp.fragments.dialogs.PaiaActionDialogFragment;
import de.eww.bibapp.tasks.paia.BookedJsonLoader;
import de.eww.bibapp.tasks.paia.PaiaCancelTask;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * ListFragment class, implementing the booked account tab
 */
public class AccountBookedFragment extends AbstractListFragment implements
	LoaderManager.LoaderCallbacks<List<BookedEntry>>,
	PaiaActionDialogFragment.PaiaActionDialogLisener,
	AsyncCanceledInterface
{
	// This is the Adapter being used to display the list's data.
    BookedAdapter mAdapter;
    
    ArrayList<BookedEntry> checkedItems;
    PaiaActionDialogFragment paiaDialog;
    MenuItem menuItem;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
        
        this.checkedItems = new ArrayList<BookedEntry>();
        
        // We have a menu item to show in action bar.
        this.setHasOptionsMenu(true);
        
        // Create an empty adapter we will use to display the loaded data.
        this.mAdapter = new BookedAdapter(getActivity(), R.layout.fragment_borrowed_item_view);
        this.setListAdapter(mAdapter);
        
        // Show progress indicator.
        this.isListShown = true;
        this.setListShown(false);
        
        // Force recreation of loader
        this.getLoaderManager().destroyLoader(0);
        this.getLoaderManager().initLoader(0, null, this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_account_booked_view, container, false);
		
		return v;
	}
	
	@Override
	public Loader<List<BookedEntry>> onCreateLoader(int arg0, Bundle arg1)
	{
		Loader<List<BookedEntry>> loader = new BookedJsonLoader(getActivity(), this);
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<BookedEntry>> loader, List<BookedEntry> data)
	{
		// Set new data
		this.mAdapter.clear();
		this.mAdapter.addAll(data);
		this.mAdapter.notifyDataSetChanged();
		
		// Show the list
		if ( this.isResumed() )
		{
			this.setListShown(true);
		}
		else
		{
			this.setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<BookedEntry>> arg0)
	{
		// Clear the data in the adapter.
		this.mAdapter.clear();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// check/uncheck the checkbox
		CheckBox checkboxView = (CheckBox) v.findViewById(R.id.booked_item_checkbox);
		checkboxView.toggle();
		
		BookedEntry item = (BookedEntry) this.getListAdapter().getItem(position);
		
		// update checked items
		if ( checkboxView.isChecked() )
		{
			this.checkedItems.add(item);
		}
		else
		{
			this.checkedItems.remove(item);
		}
		
		// enable / disable menu item
		this.menuItem.setEnabled(!this.checkedItems.isEmpty());
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		this.getActivity().getMenuInflater().inflate(R.menu.account_booked, menu);
		
		// disable menu item
		this.menuItem = menu.findItem(R.id.menu_account_booked_cancel);
		this.menuItem.setEnabled(false);
		
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
	    {
	        case R.id.menu_account_booked_cancel:
	        	// start async task to send paia request
	        	JSONArray jsonArray = new JSONArray();
	        	
				try
				{
					Iterator<BookedEntry> it = this.checkedItems.iterator();
					
					while ( it.hasNext() )
					{
						BookedEntry checkedItem = it.next();
						
						JSONObject checkedItemObject = new JSONObject();
						checkedItemObject.put("item", checkedItem.item);
						checkedItemObject.put("edition", checkedItem.edition);
						checkedItemObject.put("barcode",  checkedItem.barcode);
						
						jsonArray.put(checkedItemObject);
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	AsyncTask<String, Void, JSONObject> cancelTask = new PaiaCancelTask(this);
	        	cancelTask.execute(jsonArray.toString());
	        	
	        	// show the action dialog
	        	this.paiaDialog = new PaiaActionDialogFragment();
				this.paiaDialog.show(this.getChildFragmentManager(), "paia_action");
	    		
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onRenew(JSONObject response)
	{
		// determ text to display
		String responseText = "";
		
		Resources resources = this.getActivity().getResources();
		
		try {
			if ( response.getJSONArray("array").length() == 0 )
			{
				responseText = (String) resources.getText(R.string.paiadialog_cancel_failure);
			}
			else if ( response.getJSONArray("array").length() == this.checkedItems.size() )
			{
				responseText = (String) resources.getText(R.string.paiadialog_cancel_success);
			}
			else
			{
				responseText = (String) resources.getText(R.string.paiadialog_cancel_partial);
			}
		}
		catch (NotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.paiaDialog.paiaActionDone(responseText);
		
		// reload list
		this.setListShown(false);
		this.getLoaderManager().getLoader(0).forceLoad();
		
		// reset checked items
		this.checkedItems.clear();
		
		// reset menu item
		this.menuItem.setEnabled(false);
	}

	@Override
	public void onActionDialogPositiveClick(DialogFragment dialog)
	{
		// close dialog
		this.paiaDialog.dismiss();
	}

	@Override
	public void onAsyncCanceled()
	{
		this.setListShown(true);
		
		if ( this.getView() != null )
		{
			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
		}
	}
}