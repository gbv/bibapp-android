package de.eww.bibapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.ui.mods.SearchListFragment;

public class SearchListPagerAdapter extends FragmentStateAdapter {

    public SearchListPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return SearchListFragment.newInstance(SearchManager.SEARCH_MODE.GVK);
            default:
                return SearchListFragment.newInstance(SearchManager.SEARCH_MODE.LOCAL);
        }
    }


    @Override
    public int getItemCount() {
        return 2;
    }
}
