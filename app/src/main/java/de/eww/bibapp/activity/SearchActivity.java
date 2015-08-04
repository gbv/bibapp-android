package de.eww.bibapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;

import com.google.inject.Inject;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.fragment.search.ModsFragment;
import de.eww.bibapp.fragment.search.SearchListFragment;
import de.eww.bibapp.model.source.ModsSource;

public class SearchActivity extends BaseActivity implements
        SearchListFragment.OnModsItemSelectedListener,
        ModsPagerAdapter.SearchListLoaderInterface {

    public static SearchActivity searchActivityInstance;

    // Whether or not we are in dual-pane mode
    boolean mIsDualPane = false;

    SearchListFragment mSearchListFragment;
    ModsFragment mModsFragment;

    @Inject ModsSource mModsSource;

    // The mods item index currently beeing displayed
    int mCurrentModsItemIndex = 0;

    private SEARCH_MODE mSearchMode = SEARCH_MODE.LOCAL;

    public enum SEARCH_MODE {
        LOCAL,
        GVK
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchActivityInstance = this;

        // Find our fragments
        mSearchListFragment = (SearchListFragment) getSupportFragmentManager().findFragmentById(R.id.search_list);
        mModsFragment = (ModsFragment) getSupportFragmentManager().findFragmentById(R.id.mods_item);

        // Determine whether we are in single-pane or dual-pane mode by testing the visibility
        // of the mods view
        View modsView = findViewById(R.id.mods_item);
        mIsDualPane = modsView != null && modsView.getVisibility() == View.VISIBLE;

        mSearchListFragment.setSearchMode(mSearchMode);

        // Register ourselves as the listener for the search list fragment events.
        mSearchListFragment.setOnModsItemSelectedListener(this);

        setActiveNavigationItem(0);
    }

    /**
     * Restore mods item selection from saved state
     */
    private void restoreSelection(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (mIsDualPane) {
                int modsItemIndex = savedInstanceState.getInt("modsItemIndex", 0);
                mSearchListFragment.setSelection(modsItemIndex);
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
            mModsFragment.setModsItem(mModsSource.getModsItem(index));
        } else {
            // use separate activity
            Intent modsIntent = new Intent(this, ModsActivity.class);
            modsIntent.putExtra("modsItemIndex", index);
            startActivityForResult(modsIntent, 1);
        }
    }

    @Override
    public void onNewSearchResultsLoaded() {
        // If we are displaying the mods item on the right, we have to update it
        if (mIsDualPane) {
            if (mModsSource.getTotalItems() > 0) {
                mModsFragment.setModsItem(mModsSource.getModsItem(0));
            }
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
                // Reset list adapter
                mSearchListFragment.resetAdapter();

                // Scroll to item
                int pagerItemPosition = data.getIntExtra("pagerItemPosition", 0);
                mSearchListFragment.setSelection(pagerItemPosition);

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

    public LoaderManager getListLoaderManager() {
        return mSearchListFragment.getLoaderManager();
    }

    public void setSearchMode(SEARCH_MODE searchMode) {
        mSearchMode = searchMode;
    }
}
