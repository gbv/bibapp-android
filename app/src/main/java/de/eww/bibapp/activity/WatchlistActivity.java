package de.eww.bibapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.watchlist.WatchlistListFragment;
import de.eww.bibapp.model.source.WatchlistSource;

public class WatchlistActivity extends BaseActivity implements
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
        setContentView(R.layout.activity_watchlist);

        // Find our fragments
        mWatchlistListFragment = (WatchlistListFragment) getSupportFragmentManager().findFragmentById(R.id.watchlist_list);
        mModsFragment = (ModsFragment) getSupportFragmentManager().findFragmentById(R.id.mods_item);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the mods view
        View modsView = findViewById(R.id.mods_item);
        mIsDualPane = modsView != null && modsView.getVisibility() == View.VISIBLE;

        // load watchlist data
        mWatchlistSource.clear("watchlist");
        mWatchlistSource.loadFromFile(this);

        // Register ourselves as the listener for the search list fragment events.
        mWatchlistListFragment.setOnModsItemSelectedListener(this);

        // Set up search list fragment
        restoreSelection(savedInstanceState);

        // If we are displaying the mods item on the right, we have to update it
        if (mIsDualPane) {
            if (mWatchlistSource.getTotalItems("watchlist") > 0) {
                mModsFragment.setIsWatchlistFragment(true);
                mModsFragment.setModsItem(mWatchlistSource.getModsItem("watchlist", 0));
            }
        }
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
            mModsFragment.setModsItem(mWatchlistSource.getModsItem("watchlist", index));
        } else {
            // use separate activity
            Intent modsIntent = new Intent(this, ModsActivity.class);
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
                    ((BaseActivity) this).selectItem(navigationPosition);
                }
            }
        } else if (requestCode == 99) {
            if (resultCode == Activity.RESULT_OK) {
                // Set navigation position
                if (data.hasExtra("navigationIndex")) {
                    int navigationPosition = data.getIntExtra("navigationIndex", 0);
                    ((BaseActivity) this).selectItem(navigationPosition);
                }
            }
        }
    }
}