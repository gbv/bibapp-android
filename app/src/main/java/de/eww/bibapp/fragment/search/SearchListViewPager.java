package de.eww.bibapp.fragment.search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.SearchListPagerAdapter;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 24.08.15.
 */
public class SearchListViewPager extends RoboFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list_viewpager, container, false);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.search_list_viewpager);
        viewPager.setAdapter(new SearchListPagerAdapter(getChildFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.search_list_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
