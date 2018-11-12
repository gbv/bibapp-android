package de.eww.bibapp.fragment.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.BaseActivity;
import de.eww.bibapp.adapter.ModsPagerAdapter;
import de.eww.bibapp.adapter.ModsWatchlistPagerAdapter;
import de.eww.bibapp.fragment.dialog.SwipeLoadingDialogFragment;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.tasks.SearchXmlLoader;

/**
 * Created by christoph on 09.11.14.
 */
public class ModsPagerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
        AsyncCanceledInterface {

    private FragmentStatePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private int mCurrentItem = 0;

    private boolean mUseWatchlistSource = false;
    private String mSearchMode;
    private String mSearchString;

    private SwipeLoadingDialogFragment mLoadingDialogFragment;

    private boolean isLoadingMore = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!mUseWatchlistSource) {
            mPagerAdapter = new ModsPagerAdapter(this, getChildFragmentManager(), mSearchMode);
        } else {
            mPagerAdapter = new ModsWatchlistPagerAdapter(getChildFragmentManager());
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
        if (this.isLoadingMore == false) {
            // Create a dialog
            mLoadingDialogFragment = new SwipeLoadingDialogFragment();
            mLoadingDialogFragment.show(getChildFragmentManager(), "swipe_dialog");

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.destroyLoader(0);
            loaderManager.initLoader(0, null, this).forceLoad();

            this.isLoadingMore = true;
        }
    }

    @Override
    public Loader<HashMap<String, Object>> onCreateLoader(int arg0, Bundle arg1) {
        SearchXmlLoader loader = new SearchXmlLoader(getActivity().getApplicationContext(), this);
        loader.setSearchString(mSearchString);
        loader.setOffset(ModsSource.getLoadedItems(mSearchMode) + 1);
        loader.setIsLocalSearch(mSearchMode.equals(SearchListFragment.SEARCH_MODE.LOCAL.toString()));

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data) {
        // Add data
        List<ModsItem> modsItems = (List<ModsItem>) data.get("list");
        ModsSource.addModsItems(mSearchMode, modsItems);

        // Dismiss dialog
        mLoadingDialogFragment.dismiss();

        this.isLoadingMore = false;
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