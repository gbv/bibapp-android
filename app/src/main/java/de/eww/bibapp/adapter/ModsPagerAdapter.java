package de.eww.bibapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.model.source.ModsSource;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerAdapter extends FragmentStatePagerAdapter {

    private ModsPagerFragment mFragment;
    private String mSearchMode;

    private static final int LOADING_OFFSET = 3;

    public ModsPagerAdapter(ModsPagerFragment fragment, FragmentManager fragmentManager, String searchMode) {
        super(fragmentManager);

        mFragment = fragment;
        mSearchMode = searchMode;
    }

    @Override
    public int getCount() {
        return ModsSource.getTotalItems(mSearchMode);
    }

    @Override
    public Fragment getItem(int position) {
        if (ModsSource.getTotalItems(mSearchMode) > position + LOADING_OFFSET) {
            if (ModsSource.getLoadedItems(mSearchMode) <= position + LOADING_OFFSET) {
                mFragment.onLoadMore();
            }
        }

        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(false);
        modsFragment.setModsItem(ModsSource.getModsItem(mSearchMode, position));

        return modsFragment;
    }
}