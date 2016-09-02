package de.eww.bibapp.fragment.search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.SearchListPagerAdapter;

/**
 * Created by christoph on 24.08.15.
 */
public class SearchListViewPager extends Fragment {

    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list_viewpager, container, false);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.search_list_viewpager);
        mViewPager.setAdapter(new SearchListPagerAdapter(getChildFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.search_list_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    public void searchGvk(String searchQuery) {
        mViewPager.setCurrentItem(1, true);

        String fragmentTag = "android:switcher:" + R.id.search_list_viewpager + ":" + mViewPager.getCurrentItem();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            SearchListFragment gvkFragment = (SearchListFragment) fragment;
            gvkFragment.forceSearch(searchQuery);
        }
    }


    public void setSelection(int position) {
        String fragmentTag = "android:switcher:" + R.id.search_list_viewpager + ":" + mViewPager.getCurrentItem();
        Fragment fragment = getChildFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment != null) {
            SearchListFragment searchListFragment = (SearchListFragment) fragment;
            searchListFragment.resetAdapter();
            searchListFragment.setSelection(position);
        }
    }
}
