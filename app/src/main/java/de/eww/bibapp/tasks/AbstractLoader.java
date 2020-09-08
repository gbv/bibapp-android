package de.eww.bibapp.tasks;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Abstract loader class for communication
 */
abstract public class AbstractLoader<T> extends AsyncTaskLoader<List<T>>
{
	protected List<T> entries;
	protected Fragment fragment;
	private boolean failure = false;
	
	public AbstractLoader(Context context, Fragment callingFragment)
	{
		super(context);

		this.fragment = callingFragment;
	}
	
	protected void raiseFailure()
	{
		this.failure = true;
	}
	
	/**
     * Handles a request to start the Loader.
     */
	@Override
	protected void onStartLoading()
	{
		if ( this.entries != null )
		{
			// If we currently have a result available, deliver it immediately.
			this.deliverResult(this.entries);
		}
		
		if ( this.takeContentChanged() || this.entries == null )
		{
			// If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
			this.forceLoad();
		}
	}
	
	/**
     * Handles a request to stop the Loader.
     */
	@Override
	protected void onStopLoading()
	{
		// Attempt to cancel the current load task if possible.
		this.cancelLoad();
	}
	
	/**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<T> data)
    {
        super.onCanceled(data);
    }
	
    /**
     * Handles a request to completely reset the Loader.
     */
	@Override
	protected void onReset()
	{
		super.onReset();
		
		// Ensure the loader is stopped
		this.onStopLoading();
		
		this.entries = null;
	}
	
	/**
     * Called when there is new data to deliver to the client.
     * Also used to handle any failures while processing loadInBackground,
     * because OperationCanceledException unfortunately requires API Level 16.
     */
    @Override public void deliverResult(List<T> data)
    {
    	if ( this.failure == false )
    	{
    		super.deliverResult(data);
    	}
    	else
    	{
    		((AsyncCanceledInterface) this.fragment).onAsyncCanceled();
    	}
    }
}
