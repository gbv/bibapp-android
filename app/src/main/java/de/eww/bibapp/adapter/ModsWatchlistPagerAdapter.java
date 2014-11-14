package de.eww.bibapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.model.source.WatchlistSource;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsWatchlistPagerAdapter extends FragmentStatePagerAdapter {

    FragmentManager mFragmentManager;
    WatchlistSource mWatchlistSource;

    public ModsWatchlistPagerAdapter(FragmentManager fragmentManager, WatchlistSource watchlistSource) {
        super(fragmentManager);

        mFragmentManager = fragmentManager;
        mWatchlistSource = watchlistSource;
    }

    @Override
    public int getCount() {
        return mWatchlistSource.getTotalItems();
    }

    @Override
    public Fragment getItem(int position) {
        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(true);
        modsFragment.setModsItem(mWatchlistSource.getModsItem(position));

        return modsFragment;
    }
}