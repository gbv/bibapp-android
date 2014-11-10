//package de.eww.bibapp.fragments.watchlist;
//
//import android.app.ActionBar;
//import android.content.Context;
//import android.content.res.Resources;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.SearchAdapterInterface;
//import de.eww.bibapp.adapters.SearchAdapter;
//import de.eww.bibapp.data.SearchEntry;
//import de.eww.bibapp.fragments.AbstractListFragment;
//import de.eww.bibapp.fragment.search.DetailFragment;
//import de.eww.bibapp.fragments.detail.DetailPagerFragment;
//import de.eww.bibapp.tasks.WatchlistPreferencesLoader;
//
//public class WatchlistFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<List<SearchEntry>>,
//	SearchAdapterInterface
//{
//	// This is the Adapter being used to display the list's data.
//    SearchAdapter mAdapter;
//
//    public ArrayList<SearchEntry> checkedItems;
//    public MenuItem menuItem;
//    private ArrayList<SearchEntry> previousResults = new ArrayList<SearchEntry>();
//    private DetailFragment padDetailFragment;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		this.setHasOptionsMenu(true);
//		this.checkedItems = new ArrayList<SearchEntry>();
//	}
//
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState)
//	{
//        super.onActivityCreated(savedInstanceState);
//
//        // create and auto start loader
//        getLoaderManager().initLoader(0, null, this);
//
//        this.mAdapter = new SearchAdapter(getActivity(), R.layout.fragment_watchlist_item_view);
//        this.mAdapter.setWatchlistFragment(this);
//
//        this.setListAdapter(mAdapter);
//    }
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id)
//	{
//		this.lastClickedPosition = position;
//		DetailPagerFragment.listFragment = this;
//
//		if ( !MainActivity.isPadVersion )
//		{
//			WatchlistContainerFragment containerFragment = (WatchlistContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("watchlist");
//			containerFragment.switchContent(R.id.watchlist_container, DetailPagerFragment.class.getName(), "watchlist_pager", true);
//		}
//		else
//		{
//	    	DetailFragment detailFragment = (DetailFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("large_watchlist_detail");
//			detailFragment.setSearchEntry(this.mAdapter.getItem(position));
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_watchlist_content, container, false);
//
//		// set title
//		ActionBar actionBar = this.getActivity().getActionBar();
//		actionBar.setTitle(R.string.actionbar_watchlist);
//		actionBar.setSubtitle(null);
//		actionBar.setDisplayHomeAsUpEnabled(false);
//
//		if ( MainActivity.isPadVersion )
//		{
//	    	FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
//	    	if ( this.padDetailFragment == null )
//	    	{
//	    		this.padDetailFragment = (DetailFragment) Fragment.instantiate(this.getActivity(), DetailFragment.class.getName());
//	    		transaction.add(R.id.large_watchlist_detail, this.padDetailFragment, "large_watchlist_detail");
//	    	}
//	    	else
//	    	{
//	    		transaction.remove(this.padDetailFragment);
//	    		this.padDetailFragment = (DetailFragment) Fragment.instantiate(this.getActivity(), DetailFragment.class.getName());
//	    		transaction.add(R.id.large_watchlist_detail, this.padDetailFragment, "large_watchlist_detail");
//	    	}
//
//	    	transaction.commit();
//		}
//
//		return v;
//	}
//
//	@Override
//	public Loader<List<SearchEntry>> onCreateLoader(int arg0, Bundle arg1)
//	{
//		Loader<List<SearchEntry>> loader = new WatchlistPreferencesLoader(getActivity().getApplicationContext());
//		((WatchlistPreferencesLoader) loader).setFragment(this);
//
//		return loader;
//	}
//
//	@Override
//	public void onLoadFinished(Loader<List<SearchEntry>> loader, List<SearchEntry> data)
//	{
//		this.previousResults = (ArrayList<SearchEntry>) data;
//		this.mAdapter.clear();
//		this.mAdapter.addAll(data);
//		this.setListShown(true);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<List<SearchEntry>> arg0)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
//	{
//		menu.clear();
//		inflater.inflate(R.menu.watchlist, menu);
//
//		super.onCreateOptionsMenu(menu, inflater);
//	}
//	*/
//
//	@Override
//	public void onPrepareOptionsMenu(Menu menu)
//	{
//		super.onPrepareOptionsMenu(menu);
//		menu.clear();
//
//		this.getActivity().getMenuInflater().inflate(R.menu.watchlist, menu);
//
//		// disable menu item
//		this.menuItem = menu.findItem(R.id.menu_watchlist_remove);
//		if ( this.menuItem != null )
//		{
//			this.menuItem.setEnabled(false);
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		switch ( item.getItemId() )
//	    {
//	        case R.id.menu_watchlist_remove:
//	        	// get actual watchlist
//	        	ArrayList<SearchEntry> watchlistEntries = new ArrayList<SearchEntry>();
//
//	        	File file = this.getActivity().getFileStreamPath("watchlist");
//	        	if ( file.isFile() )
//	        	{
//	        		try
//	        		{
//		    			FileInputStream fis = this.getActivity().openFileInput("watchlist");
//
//		    			ObjectInputStream ois = new ObjectInputStream(fis);
//		    			watchlistEntries = (ArrayList<SearchEntry>) ois.readObject();
//
//		    			fis.close();
//		    		}
//		    		catch (Exception e)
//		    		{
//		    			// TODO Auto-generated catch block
//		    			e.printStackTrace();
//		    		}
//	        	}
//
//	        	// remove entries and save watchlist
//	        	Iterator<SearchEntry> it = this.checkedItems.iterator();
//
//	        	while ( it.hasNext() )
//	        	{
//	        		SearchEntry checkedItem = it.next();
//	        		watchlistEntries.remove(checkedItem);
//
//					this.mAdapter.remove(checkedItem);
//	        	}
//
//	        	try
//				{
//					FileOutputStream fos = this.getActivity().openFileOutput("watchlist", Context.MODE_PRIVATE);
//					ObjectOutputStream oos = new ObjectOutputStream(fos);
//					oos.writeObject(watchlistEntries);
//					oos.close();
//				}
//				catch (Exception e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//	        	// display toast
//				Context context = this.getActivity().getApplicationContext();
//				Resources resource = this.getActivity().getResources();
//
//				Toast toast = Toast.makeText(context, resource.getText(R.string.toast_watchlist_removed), Toast.LENGTH_SHORT);
//				toast.show();
//
//				// reset checked items
//				this.checkedItems.clear();
//				this.mAdapter.notifyDataSetChanged();
//
//				// reset menu item
//				this.menuItem.setEnabled(false);
//
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
//
//	@Override
//	public SearchAdapter getSearchAdapter()
//	{
//		return this.mAdapter;
//	}
//
//	@Override
//	public int getHits()
//	{
//		return this.getSearchAdapter().getCount();
//	}
//
//	@Override
//	public ArrayList<SearchEntry> getResults()
//	{
//		return this.previousResults;
//	}
//}