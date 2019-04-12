package de.eww.bibapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.network.search.SearchManager;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerAdapter extends FragmentStatePagerAdapter {

    private ModsPagerFragment mFragment;
    private SearchManager.SEARCH_MODE mSearchMode;

    private static final int LOADING_OFFSET = 3;

    public ModsPagerAdapter(ModsPagerFragment fragment, FragmentManager fragmentManager, SearchManager.SEARCH_MODE searchMode) {
        super(fragmentManager);

        mFragment = fragment;
        mSearchMode = searchMode;
    }

    @Override
    public int getCount() {
        return ModsSource.getTotalItems(mSearchMode.toString());
    }

    @Override
    public Fragment getItem(int position) {
        if (ModsSource.getTotalItems(mSearchMode.toString()) > position + LOADING_OFFSET) {
            if (ModsSource.getLoadedItems(mSearchMode.toString()) <= position + LOADING_OFFSET) {
                mFragment.onLoadMore();
            }
        }

        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(false);
        modsFragment.setModsItem(ModsSource.getModsItem(mSearchMode.toString(), position));

        return modsFragment;
    }
}