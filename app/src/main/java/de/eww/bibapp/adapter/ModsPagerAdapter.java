package de.eww.bibapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.fragment.dialog.SwipeLoadingDialogFragment;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.SearchFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerAdapter extends FragmentStatePagerAdapter {

    FragmentManager mFragmentManager;
    ModsSource mModsSource;

    private boolean mLoading = false;

    private static final int LOADING_OFFSET = 3;

//	private Map<Integer, DetailFragment> mPageReferenceMap = new HashMap<Integer, DetailFragment>();
//	private SearchAdapterInterface searchAdapterInterface;
//	private boolean loadinBackground = false;
//
//	private ArrayList<SearchEntry> entryArray = new ArrayList<SearchEntry>();

    public interface SearchListLoaderInterface {
        public LoaderManager getLoaderManager();
    }

    public ModsPagerAdapter(FragmentManager fragmentManager, ModsSource modsSource) {
        super(fragmentManager);

        mFragmentManager = fragmentManager;
        mModsSource = modsSource;

//		AbstractContainerFragment containerFragment = (AbstractContainerFragment) MainActivity.instance.getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
//
//		if ( MainActivity.currentTabId.equals("search") )
//		{
//			this.searchAdapterInterface = (SearchAdapterInterface) containerFragment.getFragment("de.eww.bibapp.fragments.search.SearchFragment");
//		}
//		else
//		{
//			this.searchAdapterInterface = (SearchAdapterInterface) containerFragment.getFragment("de.eww.bibapp.fragments.watchlist.WatchlistFragment");
//		}
    }

    @Override
    public int getCount() {
        return mModsSource.getTotalItems();
    }

    @Override
    public Fragment getItem(int position) {
//    	if ( this.loadinBackground == false && MainActivity.currentTabId.equals("search") ) {
        if (!mLoading) {
            if (mModsSource.getTotalItems() > position + LOADING_OFFSET) {
                if (mModsSource.getLoadedItems() <= position + LOADING_OFFSET) {
                    mLoading = true;

                    // Create a dialog
                    final SwipeLoadingDialogFragment dialogFragment = new SwipeLoadingDialogFragment();
                    dialogFragment.show(mFragmentManager, "swipe_dialog");

                    // Get the list loader
                    final Loader<Object> listLoader = SearchFragment.searchFragmentInstance.getLoaderManager().getLoader(0);

                    // Register a listener
                    listLoader.registerListener(0, new Loader.OnLoadCompleteListener<Object>() {
                        @Override
                        public void onLoadComplete(Loader<Object> loader, Object data) {
                            // Add data
                            HashMap<String, Object> dataHashMap = (HashMap<String, Object>) data;
                            List<ModsItem> modsItems = (List<ModsItem>) dataHashMap.get("list");
                            mModsSource.addModsItems(modsItems);

                            // Unregister
                            listLoader.unregisterListener(this);
                            mLoading = false;

                            // Dismiss dialog
                            dialogFragment.dismiss();
                        }
                    });

                    SearchFragment.searchFragmentInstance.getLoaderManager().getLoader(0).forceLoad();
                }
            }
        }

        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setModsItem(mModsSource.getModsItem(position));

        return modsFragment;
    }
}


//
//    @Override
//	public void destroyItem(ViewGroup container, int position, Object object)
//    {
//		super.destroyItem(container, position, object);
//
//		this.mPageReferenceMap.remove(position);
//	}
//
//    public DetailFragment getFragment(int key)
//    {
//		return mPageReferenceMap.get(key);
//	}
//}