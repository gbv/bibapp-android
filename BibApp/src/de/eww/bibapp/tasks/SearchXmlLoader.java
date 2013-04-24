package de.eww.bibapp.tasks;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.AsyncTaskLoader;
import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.SearchXmlParser;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Loader for search results
 */
public final class SearchXmlLoader extends AsyncTaskLoader<HashMap<String, Object>>
{
	private HashMap<String, Object> entries;
	private String searchString = null;
	private Integer count = null;;
	private boolean isLocalSearch = true;
	private int offset = 1;
	private Fragment fragment;
	private boolean failure = false;
	
	private void raiseFailure()
	{
		this.failure = true;
	}

	public SearchXmlLoader(Context context, Fragment callingFragment)
	{
		super(context);
		
		this.fragment = callingFragment;
	}
	
	public SearchXmlLoader(Context context, Fragment callingFragment, String searchString, int offset, int count)
	{
		super(context);
		
		this.fragment = callingFragment;
		this.searchString = searchString;
		this.offset = offset;
		this.count = count;
	}
	
	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}
	
	public void resetOffset()
	{
		this.offset = 1;
	}
	
	public int getOffset()
	{
		return this.offset;
	}
	
	public void setIsLocalSearch(boolean isLocalSearch)
	{
		this.isLocalSearch = isLocalSearch;
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
		
		if ( this.takeContentChanged()/* || this.entries == null*/ )
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
    public void onCanceled(HashMap<String, Object> data)
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
    @Override public void deliverResult(HashMap<String, Object> data)
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

	@Override
	public HashMap<String, Object> loadInBackground()
	{
		InputStream input = null;
		
		// Instantiate the parser
		SearchXmlParser searchXmlParser = new SearchXmlParser();
		HashMap<String, Object> response = new HashMap<String, Object>();
		
		String searchString = this.searchString;
		searchString = searchString.replaceAll("ü", "ue");
		searchString = searchString.replaceAll("ö", "oe");
		searchString = searchString.replaceAll("ä", "ae");
		searchString = searchString.replaceAll("Ü", "ue");
		searchString = searchString.replaceAll("Ö", "oe");
		searchString = searchString.replaceAll("Ä", "ae");
		try
		{
			searchString = URLEncoder.encode(searchString, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.getSearchUrl(searchString, this.offset, Constants.SEARCH_HITS_PER_REQUEST, this.isLocalSearch));
		
		try
		{
			this.offset += Constants.SEARCH_HITS_PER_REQUEST;
			
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			input = urlConnectionHelper.getInputStream();
			response = searchXmlParser.parse(input, this.isLocalSearch);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			this.raiseFailure();
		}
		finally
		{
			urlConnectionHelper.disconnect();
		}
		
		return response;
	}
}