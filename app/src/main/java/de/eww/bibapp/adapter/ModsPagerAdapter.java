package de.eww.bibapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.network.source.ModsSource;
import de.eww.bibapp.ui.mods.ModsFragment;
import de.eww.bibapp.ui.mods.ModsPagerFragment;

public class ModsPagerAdapter extends FragmentStateAdapter {

    private ModsPagerFragment mFragment;
    private SearchManager.SEARCH_MODE mSearchMode;

    private static final int LOADING_OFFSET = 3;

    public ModsPagerAdapter(ModsPagerFragment fragment, SearchManager.SEARCH_MODE searchMode) {
        super(fragment);

        mFragment = fragment;
        mSearchMode = searchMode;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (ModsSource.getTotalItems(mSearchMode.toString()) > position + LOADING_OFFSET) {
            if (ModsSource.getLoadedItems(mSearchMode.toString()) <= position + LOADING_OFFSET) {
//                mFragment.onLoadMore();
            }
        }

        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(false);
//        modsFragment.setModsItem(ModsSource.getModsItem(mSearchMode.toString(), position));

        return modsFragment;
    }

    @Override
    public int getItemCount() {
        return ModsSource.getTotalItems(mSearchMode.toString());
    }
}