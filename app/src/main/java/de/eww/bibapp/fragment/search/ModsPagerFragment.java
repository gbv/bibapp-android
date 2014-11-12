package de.eww.bibapp.fragment.search;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.adapter.ModsWatchlistPagerAdapter;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.model.source.WatchlistSource;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerFragment extends RoboFragment {

    private FragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private int mCurrentItem = 0;

    private boolean mUseWatchlistSource = false;

    @Inject ModsSource mModsSource;
    @Inject WatchlistSource mWatchlistSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mUseWatchlistSource) {
            mPagerAdapter = new ModsPagerAdapter(getChildFragmentManager(), mModsSource);
        } else {
            mPagerAdapter = new ModsWatchlistPagerAdapter(getChildFragmentManager(), mWatchlistSource);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setModsItem(int position) {
        mCurrentItem = position;

        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        }
    }

    public int getCurrentItemPosition() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mods_pager, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setCurrentItem(mCurrentItem);

        //		if ( viewPager.getCurrentItem() != DetailPagerFragment.listFragment.getLastClickedPosition() )
//		{
//			this.viewPager.setCurrentItem(DetailPagerFragment.listFragment.getLastClickedPosition());
//		}

        return view;
    }

    public void useWatchlistSource() {
        mUseWatchlistSource = true;
    }
}