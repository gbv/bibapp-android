package de.eww.bibapp.tasks;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import de.eww.bibapp.URLConnectionHelper;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.NewsEntry;
import de.eww.bibapp.data.NewsXmlParser;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Loader for rss feed
 */
public final class NewsRSSLoader extends AbstractLoader<NewsEntry>
{
	public NewsRSSLoader(Context context, Fragment callingFragment)
	{
		super(context, callingFragment);
	}
	
	/**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
	@Override
	public List<NewsEntry> loadInBackground()
	{
		// Instantiate the parser
		NewsXmlParser newsXmlParser = new NewsXmlParser();
		List<NewsEntry> response = null;
		
		URLConnectionHelper urlConnectionHelper = new URLConnectionHelper(Constants.NEWS_URL);
		
		try
		{
			// open connection
			urlConnectionHelper.configure();
			urlConnectionHelper.connect(null);
			
			InputStream inputStream = urlConnectionHelper.getStream();
			
			response = newsXmlParser.parse(inputStream);
			
		}
		catch ( Exception e )
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