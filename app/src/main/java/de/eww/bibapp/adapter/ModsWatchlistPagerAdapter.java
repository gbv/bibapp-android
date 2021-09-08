package de.eww.bibapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import de.eww.bibapp.ui.mods.ModsFragment;
import de.eww.bibapp.network.source.WatchlistSource;

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
//        modsFragment.setModsItem(WatchlistSource.getModsItem("watchlist", position));

        return modsFragment;
    }
}