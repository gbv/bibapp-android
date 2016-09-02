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

    public ModsWatchlistPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return WatchlistSource.getTotalItems("watchlist");
    }

    @Override
    public Fragment getItem(int position) {
        ModsFragment modsFragment = new ModsFragment();
        modsFragment.setIsWatchlistFragment(true);
        modsFragment.setModsItem(WatchlistSource.getModsItem("watchlist", position));

        return modsFragment;
    }
}