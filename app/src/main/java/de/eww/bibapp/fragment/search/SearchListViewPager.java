package de.eww.bibapp.fragment.search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.SearchListPagerAdapter;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 24.08.15.
 */
public class SearchListViewPager extends RoboFragment {

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

        /**
         * Workaround for #183123 @see https://code.google.com/p/android/issues/detail?id=183123
         */
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));

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

    private static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final WeakReference<TabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                final boolean updateText = (mScrollState == ViewPager.SCROLL_STATE_DRAGGING)
                        || (mScrollState == ViewPager.SCROLL_STATE_SETTLING
                        && mPreviousScrollState == ViewPager.SCROLL_STATE_DRAGGING);
                tabLayout.setScrollPosition(position, positionOffset, updateText);
            }
        }

        @Override
        public void onPageSelected(int position) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                tabLayout.getTabAt(position).select();
            }
        }
    }
}
