//package de.eww.bibapp.tasks;
//
//import android.content.Context;
//import android.support.v4.content.AsyncTaskLoader;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.eww.bibapp.data.SearchEntry;
//import de.eww.bibapp.fragments.watchlist.WatchlistFragment;
//
//public final class WatchlistPreferencesLoader extends AsyncTaskLoader<List<SearchEntry>>
//{
//	private List<SearchEntry> entries;
//	private WatchlistFragment fragment;
//
//	public WatchlistPreferencesLoader(Context context)
//	{
//		super(context);
//		// TODO Auto-generated constructor stub
//	}
//
//	public void setFragment(WatchlistFragment fragment)
//	{
//		this.fragment = fragment;
//	}
//
//	@Override
//	protected void onStartLoading()
//	{
//		if ( this.entries != null )
//		{
//			this.deliverResult(this.entries);
//		}
//
//		if ( this.takeContentChanged() || this.entries == null )
//		{
//			this.forceLoad();
//		}
//	}
//
//	@Override
//	protected void onStopLoading()
//	{
//		this.cancelLoad();
//	}
//
//	@Override
//	protected void onReset()
//	{
//		super.onReset();
//
//		this.onStopLoading();
//
//		this.entries = null;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<SearchEntry> loadInBackground()
//	{
//		List<SearchEntry> response = new ArrayList<SearchEntry>();
//
//		try
//		{
//			File file = this.fragment.getActivity().getFileStreamPath("watchlist");
//	    	if ( file.isFile() )
//	    	{
//	    		FileInputStream fis = this.fragment.getActivity().openFileInput("watchlist");
//
//				ObjectInputStream ois = new ObjectInputStream(fis);
//				response = (ArrayList<SearchEntry>) ois.readObject();
//
//				fis.close();
//	    	}
//		}
//		catch (FileNotFoundException e1)
//		{
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ClassNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return response;
//	}
//}