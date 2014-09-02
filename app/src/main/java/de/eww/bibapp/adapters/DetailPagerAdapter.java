package de.eww.bibapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.Loader;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.eww.bibapp.MainActivity;
import de.eww.bibapp.SearchAdapterInterface;
import de.eww.bibapp.data.SearchEntry;
import de.eww.bibapp.fragments.AbstractContainerFragment;
import de.eww.bibapp.fragments.detail.DetailFragment;
import de.eww.bibapp.fragments.dialogs.SwipeLoadingDialogFragment;

public class DetailPagerAdapter extends FragmentStatePagerAdapter
{
	private Map<Integer, DetailFragment> mPageReferenceMap = new HashMap<Integer, DetailFragment>();
	private SearchAdapterInterface searchAdapterInterface;
	private boolean loadinBackground = false;
	
	private ArrayList<SearchEntry> entryArray = new ArrayList<SearchEntry>();
	
	private static final int LOADING_OFFSET = 3; 
	
	public DetailPagerAdapter(FragmentManager fm)
	{
		super(fm);
		
		AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
		
		if ( MainActivity.currentTabId.equals("search") )
		{
			this.searchAdapterInterface = (SearchAdapterInterface) containerFragment.getFragment("de.eww.bibapp.fragments.search.SearchFragment");
		}
		else
		{
			this.searchAdapterInterface = (SearchAdapterInterface) containerFragment.getFragment("de.eww.bibapp.fragments.watchlist.WatchlistFragment");
		}
		
		// take all entries from parent list
		this.entryArray = this.searchAdapterInterface.getResults();
	}
	
	@Override
    public int getCount()
	{
		if ( this.searchAdapterInterface != null )
		{
			return this.searchAdapterInterface.getHits();
		}
		
		return 0;
    }

    @Override
    public Fragment getItem(int position)
    {
    	if ( this.loadinBackground == false && MainActivity.currentTabId.equals("search") ) {
    		if (
    			this.searchAdapterInterface.getHits() > position + DetailPagerAdapter.LOADING_OFFSET &&
    			this.searchAdapterInterface.getResults().size() <= position + DetailPagerAdapter.LOADING_OFFSET
    		) {
	    		this.loadinBackground = true;
	    		
	    		// create a dialog
	    		final SwipeLoadingDialogFragment dialogFragment = new SwipeLoadingDialogFragment();
	    		dialogFragment.show(MainActivity.instance.getSupportFragmentManager(), "swipe_dialog");
	    		
	    		// get the list loader
	    		final Loader<Object> listLoader = this.searchAdapterInterface.getLoaderManager().getLoader(0);
	    		
    			// register a listener
        		listLoader.registerListener(0, new Loader.OnLoadCompleteListener<Object>()
        		{
    				@SuppressWarnings("unchecked")
    				@Override
    				public void onLoadComplete(Loader<Object> loader, Object data)
    				{
    					// append the data
    					HashMap<String, Object> dataHashMap = (HashMap<String, Object>) data;
    					DetailPagerAdapter.this.entryArray.addAll((Collection<SearchEntry>) dataHashMap.get("list"));
    					
    					// unregister
    					listLoader.unregisterListener(this);
    					DetailPagerAdapter.this.loadinBackground = false;
    					
    					// dismiss dialog
    					dialogFragment.dismiss();
    				}
    			});
        		
        		this.searchAdapterInterface.getLoaderManager().getLoader(0).forceLoad();
    		}
    	}
    	
    	Fragment detailFragment = DetailFragment.newInstance(this.entryArray.get(position), position);
    	
    	this.mPageReferenceMap.put(position, (DetailFragment) detailFragment);
    	
        return detailFragment;
    }
    
    @Override
	public void destroyItem(ViewGroup container, int position, Object object)
    {
		super.destroyItem(container, position, object);
		
		this.mPageReferenceMap.remove(position);
	}
    
    public DetailFragment getFragment(int key)
    {
		return mPageReferenceMap.get(key);
	}
}