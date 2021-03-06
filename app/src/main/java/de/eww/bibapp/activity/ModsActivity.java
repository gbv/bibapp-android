package de.eww.bibapp.activity;

/**
 * Created by christoph on 08.11.14.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.fragment.search.ModsPagerFragment;
import de.eww.bibapp.model.source.WatchlistSource;

/**
 * Activity that displays a particular location onscreen.
 *
 * This activity is started only when the screen in not large enough for a two-pane layout, in
 * which case this separate activity is shown in order to display the location. This activity
 * kills itself if the display is reconfigured into a shape that allows a two-pane layout, since
 * in that case the location article will be displayed by the {@link de.eww.bibapp.activity.LocationsActivity}
 * and this Activity becomes unnecessary.
 */
public class ModsActivity extends BaseActivity implements
        AsyncCanceledInterface {

    // The mods item index we are to display
    int mModsItemIndex;

    ModsPagerFragment mModsPagerFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mods);

        mModsItemIndex = getIntent().getExtras().getInt("modsItemIndex", 0);

        // Place a ModsFragment in our container
        mModsPagerFragment = new ModsPagerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, mModsPagerFragment).commit();

        boolean isFromWatchlist = false;
        if (getIntent().hasExtra("modsItemSource")) {
            String modsItemSource = getIntent().getExtras().getString("modsItemSource");
            if (modsItemSource.equals(WatchlistSource.class.getName())) {
                mModsPagerFragment.useWatchlistSource();
                isFromWatchlist = true;
            }
        }

        if (getIntent().hasExtra("searchMode")) {
            String searchMode = getIntent().getExtras().getString("searchMode");
            mModsPagerFragment.setSearchMode(searchMode);
        }

        if (getIntent().hasExtra("searchString")) {
            String searchString = getIntent().getExtras().getString("searchString");
            mModsPagerFragment.setSearchString(searchString);
        }

        setActiveNavigationItem(isFromWatchlist ? 2 : 0);

        // Display the correct mods item on the fragment
        mModsPagerFragment.setModsItem(mModsItemIndex);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("pagerItemPosition", mModsPagerFragment.getCurrentItemPosition());
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(this, R.string.toast_account_error, Toast.LENGTH_LONG);
        toast.show();
    }
}
