package de.eww.bibapp.fragment.watchlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.activity.DrawerActivity;
import de.eww.bibapp.activity.ModsActivity;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.model.source.WatchlistSource;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 11.11.14.
 */
public class WatchlistFragment extends RoboFragment implements
        ModsPagerAdapter.SearchListLoaderInterface,
        WatchlistListFragment.OnModsItemSelectedListener {

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    WatchlistListFragment mWatchlistListFragment;
    ModsFragment mModsFragment;

    @Inject WatchlistSource mWatchlistSource;

    // The mods item index currently beeing displayed
    int mCurrentModsItemIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load watchlist data
        mWatchlistSource.clear();
        mWatchlistSource.loadFromFile(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register ourselves as the listener for the search list fragment events.
        mWatchlistListFragment.setOnModsItemSelectedListener(this);

        // Set up search list fragment
        restoreSelection(savedInstanceState);

        // If we are displaying the mods item on the right, we have to update it
        if (mIsDualPane) {
            if (mWatchlistSource.getTotalItems() > 0) {
                mModsFragment.setIsWatchlistFragment(true);
                mModsFragment.setModsItem(mWatchlistSource.getModsItem(0));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watchlist, container, false);

        // Find our fragments
        mWatchlistListFragment = (WatchlistListFragment) getChildFragmentManager().findFragmentById(R.id.watchlist_list);
        mModsFragment = (ModsFragment) getChildFragmentManager().findFragmentById(R.id.mods_item);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the mods view
        View modsView = view.findViewById(R.id.mods_item);
        mIsDualPane = modsView != null && modsView.getVisibility() == View.VISIBLE;

        return view;
    }

    /**
     * Restore mods item selection from saved state
     */
    private void restoreSelection(Bundle savedInstancteState) {
        if (savedInstancteState != null) {
            if (mIsDualPane) {
                int modsItemIndex = savedInstancteState.getInt("modsItemIndex", 0);
                mWatchlistListFragment.setSelection(modsItemIndex);
                onModsItemSelected(modsItemIndex);
            }
        }
    }

    /**
     * Called when a mods item is selected.
     *
     * @param index the index of the selected mods item.
     */
    @Override
    public void onModsItemSelected(int index) {
        mCurrentModsItemIndex = index;

        if (mIsDualPane) {
            // display it on the mods fragment
            mModsFragment.setModsItem(mWatchlistSource.getModsItem(index));
        } else {
            // use separate activity
            Intent modsIntent = new Intent(getActivity(), ModsActivity.class);
            modsIntent.putExtra("modsItemIndex", index);
            modsIntent.putExtra("modsItemSource", WatchlistSource.class.getName());
            startActivityForResult(modsIntent, 1);
        }
    }

    @Override
    public void onEmptyList() {
        if (mIsDualPane) {
            mModsFragment.removeModsItem();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("modsItemIndex", mCurrentModsItemIndex);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Set navigation position
                if (data.hasExtra("navigationIndex")) {
                    int navigationPosition = data.getIntExtra("navigationIndex", 0);
                    ((DrawerActivity) getActivity()).selectItem(navigationPosition);
                }
            }
        }
    }
}