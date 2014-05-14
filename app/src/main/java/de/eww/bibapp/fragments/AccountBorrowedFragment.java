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
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;
import de.eww.bibapp.adapters.BorrowedAdapter;
import de.eww.bibapp.data.PaiaDocument;
import de.eww.bibapp.fragments.dialogs.InsufficentRightsDialogFragment;
import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
import de.eww.bibapp.fragments.dialogs.PaiaActionDialogFragment;
import de.eww.bibapp.tasks.paia.BorrowedJsonLoader;
import de.eww.bibapp.tasks.paia.PaiaRenewTask;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * ListFragment class, implementing the borrowed account tab
 */
public class AccountBorrowedFragment extends AbstractListFragment implements
	LoaderManager.LoaderCallbacks<List<PaiaDocument>>,
	PaiaActionDialogFragment.PaiaActionDialogLisener,
	AsyncCanceledInterface
{
	// This is the Adapter being used to display the list's data.
    BorrowedAdapter mAdapter;
    
    ArrayList<PaiaDocument> checkedItems;
    PaiaActionDialogFragment paiaDialog;
    MenuItem menuItem;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
        
        this.checkedItems = new ArrayList<PaiaDocument>();
        
        // We have a menu item to show in action bar.
        this.setHasOptionsMenu(true);
        
        // Create an empty adapter we will use to display the loaded data.
        this.mAdapter = new BorrowedAdapter(getActivity(), R.layout.fragment_borrowed_item_view, PaiaHelper.hasScope(PaiaHelper.SCOPES.WRITE_ITEMS));
        this.setListAdapter(mAdapter);
        
        // Show progress indicator.
        this.isListShown = true;
        this.setListShown(false);

        if (PaiaHelper.hasScope(PaiaHelper.SCOPES.READ_ITEMS)) {
            // Force recreation of loader
            this.getLoaderManager().destroyLoader(0);
            this.getLoaderManager().initLoader(0, null, this);
        } else {
            this.setListShown(true);

            InsufficentRightsDialogFragment dialog = new InsufficentRightsDialogFragment();
            dialog.show(this.getChildFragmentManager(), "load_rights");
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_account_borrowed_view, container, false);
		
		return v;
	}
	
	@Override
	public Loader<List<PaiaDocument>> onCreateLoader(int id, Bundle args)
	{
		Loader<List<PaiaDocument>> loader = new BorrowedJsonLoader(getActivity(), this);
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<PaiaDocument>> loader, List<PaiaDocument> data)
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
	public void onLoaderReset(Loader<List<PaiaDocument>> arg0)
	{
		// Clear the data in the adapter.
		this.mAdapter.clear();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// check/uncheck the checkbox
		CheckBox checkboxView = (CheckBox) v.findViewById(R.id.borrowed_item_checkbox);
		if ( checkboxView.isEnabled() )
		{
			checkboxView.toggle();

            PaiaDocument item = (PaiaDocument) this.getListAdapter().getItem(position);
			
			// update checked items
			if ( checkboxView.isShown() && checkboxView.isChecked() )
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
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		this.getActivity().getMenuInflater().inflate(R.menu.account_borrowed, menu);
		
		// disable menu item
		this.menuItem = menu.findItem(R.id.menu_account_borrowed_extend);
		this.menuItem.setEnabled(false);
		
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch ( item.getItemId() )
	    {
	        case R.id.menu_account_borrowed_extend:
	        	// start async task to send paia request
                JSONObject jsonRequest = new JSONObject();
	        	
				try
				{
                    JSONArray jsonArray = new JSONArray();
					Iterator<PaiaDocument> it = this.checkedItems.iterator();
					
					while ( it.hasNext() )
					{
                        PaiaDocument checkedItem = it.next();

						JSONObject checkedItemObject = new JSONObject();
						checkedItemObject.put("item", checkedItem.getItem());
                        if (!checkedItem.getEdition().equals("")) {
                            checkedItemObject.put("edition", checkedItem.getEdition());
                        }

						jsonArray.put(checkedItemObject);
					}

                    jsonRequest.put("doc", jsonArray);
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	AsyncTask<String, Void, JSONObject> renewTask = new PaiaRenewTask(this);
	        	renewTask.execute(jsonRequest.toString());
	        	
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
            if (response.has("doc")) {
                JSONArray docArray = response.getJSONArray("doc");

                int docArrayLength = docArray.length();
                int numFailedItems = 0;

                for (int i=0; i < docArrayLength; i++) {
                    JSONObject docEntry = docArray.getJSONObject(i);

                    if (docEntry.has("error")) {
                        numFailedItems++;
                    }
                }

                if (numFailedItems == this.checkedItems.size()) {
                    responseText = (String) resources.getText(R.string.paiadialog_renew_failure);
                } else if (numFailedItems > 0) {
                    responseText = (String) resources.getText(R.string.paiadialog_renew_partial);
                } else {
                    responseText = (String) resources.getText(R.string.paiadialog_renew_success);
                }
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