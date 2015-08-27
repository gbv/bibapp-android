package de.eww.bibapp.fragment.search;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.adapter.ModsWatchlistPagerAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.fragment.dialog.SwipeLoadingDialogFragment;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.model.source.WatchlistSource;
import de.eww.bibapp.tasks.DBSPixelTask;
import de.eww.bibapp.tasks.SearchXmlLoader;
import de.eww.bibapp.util.PrefUtils;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
        AsyncCanceledInterface {

    private FragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private int mCurrentItem = 0;

    private boolean mUseWatchlistSource = false;
    private String mSearchMode;
    private String mSearchString;

    private SwipeLoadingDialogFragment mLoadingDialogFragment;

    @Inject ModsSource mModsSource;
    @Inject WatchlistSource mWatchlistSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mUseWatchlistSource) {
            mPagerAdapter = new ModsPagerAdapter(this, getChildFragmentManager(), mModsSource, mSearchMode);
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

        return view;
    }

    public void useWatchlistSource() {
        mUseWatchlistSource = true;
    }

    public void setSearchMode(String searchMode) {
        mSearchMode = searchMode;
    }

    public void setSearchString(String searchString) {
        mSearchString = searchString;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == getActivity().RESULT_OK) {
                // Set navigation position
                int navigationPosition = data.getIntExtra("navigationIndex", 0);
                ((BaseActivity) getActivity()).selectItem(navigationPosition);
            }
        }
    }

    public void onLoadMore() {
        // Create a dialog
        mLoadingDialogFragment = new SwipeLoadingDialogFragment();
        mLoadingDialogFragment.show(getChildFragmentManager(), "swipe_dialog");

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<HashMap<String, Object>> onCreateLoader(int arg0, Bundle arg1) {
        SearchXmlLoader loader = new SearchXmlLoader(getActivity().getApplicationContext(), this);
        loader.setSearchString(mSearchString);
        loader.setOffset(mModsSource.getLoadedItems(mSearchMode) + 1);
        loader.setIsLocalSearch(mSearchMode.equals(SearchListFragment.SEARCH_MODE.LOCAL.toString()));

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data) {
        // Add data
        List<ModsItem> modsItems = (List<ModsItem>) data.get("list");
        mModsSource.addModsItems(mSearchMode, modsItems);

        // Dismiss dialog
        mLoadingDialogFragment.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, Object>> loader) {
        // empty
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
        toast.show();

        getLoaderManager().destroyLoader(0);
    }
}