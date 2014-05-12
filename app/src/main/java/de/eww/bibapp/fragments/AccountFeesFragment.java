package de.eww.bibapp.fragments;

import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapters.FeeAdapter;
import de.eww.bibapp.data.FeeEntry;
import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
import de.eww.bibapp.tasks.paia.FeeJsonLoader;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * ListFragment class, implementing the fees account tab
 */
public class AccountFeesFragment extends AbstractListFragment implements
	LoaderManager.LoaderCallbacks<List<FeeEntry>>,
	AsyncCanceledInterface
{
	// This is the Adapter being used to display the list's data.
    FeeAdapter mAdapter;

	@Override
    public void onActivityCreated(Bundle savedInstanceState)
	{
        super.onActivityCreated(savedInstanceState);
        
        // We have a menu item to show in action bar.
        this.setHasOptionsMenu(true);
        
        // Create an empty adapter we will use to display the loaded data.
        this.mAdapter = new FeeAdapter(getActivity(), R.layout.fragment_borrowed_item_view);
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
		View v = inflater.inflate(R.layout.fragment_account_fees_view, container, false);
		
		return v;
	}
	
	@Override
	public Loader<List<FeeEntry>> onCreateLoader(int arg0, Bundle arg1)
	{
		Loader<List<FeeEntry>> loader = new FeeJsonLoader(getActivity(), this);
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<FeeEntry>> loader, List<FeeEntry> data)
	{
		// Set new data
		this.mAdapter.clear();
		this.mAdapter.addAll(data);
		this.mAdapter.notifyDataSetChanged();
		
		if ( !data.isEmpty() )
		{
			TextView sumView = (TextView) this.getView().findViewById(R.id.fee_sum);
			Resources resources = this.getActivity().getResources();
			sumView.setText(resources.getString(R.string.account_fees_amount) + " " + data.get(0).sum);
		}
		
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
	public void onLoaderReset(Loader<List<FeeEntry>> arg0)
	{
		// Clear the data in the adapter.
		this.mAdapter.clear();
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