package de.eww.bibapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.search.SearchListFragment;
import de.eww.bibapp.util.PrefUtils;

/**
 * Created by christoph on 24.08.15.
 */
public class SearchListPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private Context mContext;

    public SearchListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        SearchListFragment fragment = new SearchListFragment();

        if (position == 0) {
            fragment.setSearchMode(SearchListFragment.SEARCH_MODE.LOCAL);
        } else {
            fragment.setSearchMode(SearchListFragment.SEARCH_MODE.GVK);
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] searchCatalogs = mContext.getResources().getStringArray(R.array.search_catalogs);

        // If our current local catalog contains a short title, we append it to the default title
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(mContext);
        if (Constants.LOCAL_CATALOGS[localCatalogIndex].length > 2) {
            searchCatalogs[0] += " " + Constants.LOCAL_CATALOGS[localCatalogIndex][2];
        }

        return searchCatalogs[position];
    }
}
