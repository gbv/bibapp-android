package de.eww.bibapp.fragment.search;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.activity.SettingsActivity;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.tasks.DBSPixelTask;
import de.eww.bibapp.tasks.SearchXmlLoader;
import roboguice.activity.RoboActionBarActivity;
import roboguice.fragment.RoboFragment;

/**
 * Created by christoph on 03.11.14.
 */
public class SearchListFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
        RecyclerViewOnGestureListener.OnGestureListener,
        AsyncCanceledInterface {

    @Inject ModsSource mModsSource;

    RecyclerView mRecyclerView;
    SearchView mSearchView;
    ProgressBar mProgressBar;

    private ModsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mIsLoading = false;

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
        public void onModsItemSelected(int index);

        public void onNewSearchResultsLoaded();
    }

    /**
     * Sets the listener that should be notified of mods item selection events.
     *
     * @param listener the listener to notify.
     */
    public void setOnModsItemSelectedListener(OnModsItemSelectedListener listener) {
        mModsItemSelectedListener = listener;
    }

    public void setSelection(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    public void resetAdapter() {
        mAdapter = new ModsAdapter(mModsSource.getModsItems(), getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mRecyclerView.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                // Are we already loading?
                if (!mIsLoading) {
                    // Are there more items than we can display on one page?
                    if (totalItemCount > visibleItemCount) {
                        // Did we reach the last item from response?
                        if (totalItemCount < mModsSource.getTotalItems()) {
                            // Did we reach the end of the list?
                            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + 0)) {
                                mIsLoading = true;

                                mProgressBar.setVisibility(View.VISIBLE);
                                Loader<HashMap<String, Object>> loader = getLoaderManager().getLoader(0);
                                SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;
                                searchXmlLoader.forceLoad();
                            }
                        }
                    }
                }
            }
        });

        // Set up search view
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProgressBar.setVisibility(View.VISIBLE);

                Loader<HashMap<String, Object>> loader = getLoaderManager().getLoader(0);
                SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;

                searchXmlLoader.setSearchString(query);
                searchXmlLoader.resetOffset();
                searchXmlLoader.forceLoad();
                mAdapter = null;

                // Force soft keyboard to hide
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Start the Request
        mProgressBar.setVisibility(View.GONE);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.search_results);
        mSearchView = (SearchView) view.findViewById(R.id.search_query);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        return view;
    }

    @Override
    public Loader<HashMap<String, Object>> onCreateLoader(int arg0, Bundle arg1) {
        Loader<HashMap<String, Object>> loader = new SearchXmlLoader(getActivity().getApplicationContext(), this);
        ((SearchXmlLoader) loader).setIsLocalSearch(true);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, Object>> loader, HashMap<String, Object> data) {
        List<ModsItem> modsItems = (List<ModsItem>) data.get("list");

        mModsSource.setTotalItems((Integer) data.get("numberOfRecords"));

        mProgressBar.setVisibility(View.GONE);

        if (mAdapter == null) {
            mModsSource.clear();
            mAdapter = new ModsAdapter(modsItems, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addModsItems(modsItems);
            mAdapter.notifyDataSetChanged();
        }

        mModsSource.addModsItems(modsItems);
        if (mModsItemSelectedListener != null) {
            mModsItemSelectedListener.onNewSearchResultsLoaded();
        }

        mIsLoading = false;

        // dbs counting
        if (Constants.DBS_COUNTING_URL != null && !Constants.DBS_COUNTING_URL.isEmpty()) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean isDbsChecked = sharedPref.getBoolean(SettingsActivity.KEY_PREF_DATA_PRIVACY, true);

            if (isDbsChecked) {
                AsyncTask<Void, Void, Void> dbsPixelTask = new DBSPixelTask(getActivity());
                dbsPixelTask.execute();
            }
        }

        ActionBar actionBar = ((RoboActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(String.valueOf(mModsSource.getTotalItems()) + " " + getResources().getString(R.string.search_hits));
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, Object>> loader) {
        // empty
    }

    @Override
    public void onAsyncCanceled() {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
        toast.show();

        mProgressBar.setVisibility(View.GONE);
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onClick(View view, int position) {
        if (mModsItemSelectedListener != null) {
            mModsItemSelectedListener.onModsItemSelected(position);
        }
    }

    @Override
    public void onLongPress(View view, int position) {

    }
}
