package de.eww.bibapp.fragment.search;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.SearchActivity;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.listener.EndlessScrollListener;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.tasks.DBSPixelTask;
import de.eww.bibapp.tasks.SearchXmlLoader;
import de.eww.bibapp.util.PrefUtils;
import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 03.11.14.
 */
public class SearchListFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
        View.OnClickListener,
        RecyclerViewOnGestureListener.OnGestureListener,
        AsyncCanceledInterface {

    @Inject ModsSource mModsSource;

    RecyclerView mRecyclerView;
    SearchView mSearchView;
    ProgressBar mProgressBar;
    TextView mEmptyView;

    private ModsAdapter mAdapter;

    private boolean mIsLoading = false;
    private String mLastSearchQuery;

    public enum SEARCH_MODE {
        LOCAL,
        GVK
    }

    private SEARCH_MODE mSearchMode = SEARCH_MODE.LOCAL;

    // The listener we are to notify when a mods item is selected
    OnModsItemSelectedListener mModsItemSelectedListener = null;

    /**
     * Represents a listener that will be notified of mods item selections.
     */
    public interface OnModsItemSelectedListener {
        /**
         * Call when a given mods item is selected.
         *
         * @param index the index of the selected mods item.
         */
        void onModsItemSelected(SEARCH_MODE searchMode, int index, String searchString);

        void onNewSearchResultsLoaded(SEARCH_MODE searchMode);
    }

//    public void setSelection(int position) {
//        mRecyclerView.scrollToPosition(position);
//    }
//
//    public void resetAdapter() {
//        mAdapter = new ModsAdapter(mModsSource.getModsItems(), getActivity());
//        mRecyclerView.setAdapter(mAdapter);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            // Register the fragments activity as the listener for select events.
            mModsItemSelectedListener = (OnModsItemSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnModsItemSelectedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemCount) {
                mIsLoading = true;

                mProgressBar.setVisibility(View.VISIBLE);
                Loader<HashMap<String, Object>> loader = getLoaderManager().getLoader(0);
                SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;
                searchXmlLoader.forceLoad();
            }
        });

        // Set up search view
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProgressBar.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.GONE);

                Loader<HashMap<String, Object>> loader = getLoaderManager().getLoader(0);
                SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;

                mLastSearchQuery = query;

                searchXmlLoader.setSearchString(query);
                searchXmlLoader.resetOffset();
                searchXmlLoader.forceLoad();
                mAdapter = null;

                // Force soft keyboard to hide
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

                mSearchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Start the Request
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.initLoader(0, null, this);
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
    }

    public void forceSearch(String searchQuery) {
        mSearchView.setQuery(searchQuery, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mSearchView = (SearchView) view.findViewById(R.id.search_query);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        mEmptyView = (TextView) view.findViewById(R.id.empty);

        return view;
    }

    @Override
    public Loader<HashMap<String, Object>> onCreateLoader(int arg0, Bundle arg1) {
        // Reset source
        mModsSource.clear(mSearchMode.toString());
        mModsSource.setTotalItems(mSearchMode.toString(), 0);
        updateSubtitle();

        Loader<HashMap<String, Object>> loader = new SearchXmlLoader(getActivity().getApplicationContext(), this);
        ((SearchXmlLoader) loader).setIsLocalSearch(mSearchMode == SearchListFragment.SEARCH_MODE.LOCAL);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data) {
        List<ModsItem> modsItems = (List<ModsItem>) data.get("list");

        mModsSource.setTotalItems(mSearchMode.toString(), (Integer) data.get("numberOfRecords"));

        mProgressBar.setVisibility(View.GONE);

        if (mModsSource.getTotalItems(mSearchMode.toString()) == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        if (mAdapter == null) {
            mModsSource.clear(mSearchMode.toString());
            mAdapter = new ModsAdapter(modsItems, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addModsItems(modsItems);
            mAdapter.notifyDataSetChanged();
        }

        mModsSource.addModsItems(mSearchMode.toString(), modsItems);
        if (mModsItemSelectedListener != null) {
            mModsItemSelectedListener.onNewSearchResultsLoaded(mSearchMode);
        }

        mIsLoading = false;

        // dbs counting
        if (Constants.DBS_COUNTING_URL != null && !Constants.DBS_COUNTING_URL.isEmpty()) {
            boolean isDbsChecked = PrefUtils.isDbsChecked(getActivity());

            if (isDbsChecked) {
                AsyncTask<Void, Void, Void> dbsPixelTask = new DBSPixelTask(getActivity());
                dbsPixelTask.execute();
            }
        }

        updateSubtitle();

        /**
         * If this is a local catalog search and we could not find any results
         * suggest to use the global gvk search
         */
        if (mSearchMode == SEARCH_MODE.LOCAL) {
            if (mModsSource.getTotalItems(mSearchMode.toString()) == 0) {
                Snackbar
                    .make(getActivity().findViewById(R.id.content_frame), R.string.search_no_results, Snackbar.LENGTH_LONG)
                    .setAction(R.string.search_gvk, this)
                    .show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        SearchListViewPager searchViewPager = (SearchListViewPager) getParentFragment();
        searchViewPager.searchGvk(mLastSearchQuery);
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, Object>> loader) {
        // empty
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (visible) {
            updateSubtitle();
        }
    }

    public void updateSubtitle() {
        Activity activity = getActivity();
        if (activity != null) {
            ActionBar actionBar = ((RoboActionBarActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(String.valueOf(mModsSource.getTotalItems(mSearchMode.toString())) + " " + getResources().getString(R.string.search_hits));
            }
        }
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
        toast.show();

        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onClick(View view, int position) {
        if (!mIsLoading) {
            if (mModsItemSelectedListener != null) {
                mModsItemSelectedListener.onModsItemSelected(mSearchMode, position, mLastSearchQuery);
            }
        }
    }

    @Override
    public void onLongPress(View view, int position) {

    }

    public void setSearchMode(SEARCH_MODE searchMode) {
        mSearchMode = searchMode;
    }
}
