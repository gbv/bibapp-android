package de.eww.bibapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.activity.SearchActivity;
import de.eww.bibapp.fragment.dialog.SwipeLoadingDialogFragment;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.fragment.search.SearchListFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerAdapter extends FragmentStatePagerAdapter {

    private ModsPagerFragment mFragment;
    private ModsSource mModsSource;
    private String mSearchMode;

    private static final int LOADING_OFFSET = 3;

    public ModsPagerAdapter(ModsPagerFragment fragment, FragmentManager fragmentManager, ModsSource modsSource, String searchMode) {
        super(fragmentManager);

        mFragment = fragment;
        mModsSource = modsSource;
        mSearchMode = searchMode;
    }

    @Override
    public int getCount() {
        return mModsSource.getTotalItems(mSearchMode);
    }

    @Override
    public Fragment getItem(int position) {
        if (mModsSource.getTotalItems(mSearchMode) > position + LOADING_OFFSET) {
            if (mModsSource.getLoadedItems(mSearchMode) <= position + LOADING_OFFSET) {
                mFragment.onLoadMore();
            }
        }

        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(false);
        modsFragment.setModsItem(mModsSource.getModsItem(mSearchMode, position));

        return modsFragment;
    }
}