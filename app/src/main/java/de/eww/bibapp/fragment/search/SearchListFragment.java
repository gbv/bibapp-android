package de.eww.bibapp.fragment.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.decoration.DividerItemDecoration;
import de.eww.bibapp.listener.EndlessScrollListener;
import de.eww.bibapp.listener.RecyclerViewOnGestureListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.tasks.DBSPixelTask;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.util.SruHelper;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by christoph on 03.11.14.
 */
public class SearchListFragment extends Fragment implements
        SearchManager.SearchLoaderInterface,
        View.OnClickListener,
        RecyclerViewOnGestureListener.OnGestureListener {

    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;

    private ModsAdapter mAdapter;
    private SearchManager searchManager;
    private CompositeDisposable disposable = new CompositeDisposable();

    private boolean mIsLoading = false;
    private String mLastSearchQuery;

    private SearchManager.SEARCH_MODE mSearchMode = SearchManager.SEARCH_MODE.LOCAL;

    // The listener we are to notify when a mods item is selected
    private OnModsItemSelectedListener mModsItemSelectedListener = null;

    /**
     * Represents a listener that will be notified of mods item selections.
     */
    public interface OnModsItemSelectedListener {
        /**
         * Call when a given mods item is selected.
         *
         * @param index the index of the selected mods item.
         */
        void onModsItemSelected(SearchManager.SEARCH_MODE searchMode, int index, String searchString);

        void onNewSearchResultsLoaded(SearchManager.SEARCH_MODE searchMode);
    }

    public void setSelection(int position) {
        mRecyclerView.scrollToPosition(position);
    }

    public void resetAdapter() {
        ArrayList<ModsItem> modsItemList = new ArrayList<>();
        modsItemList.addAll(ModsSource.getModsItems(mSearchMode.toString()));

        mAdapter = new ModsAdapter(modsItemList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

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
    public void onDestroyView() {
        super.onDestroyView();
        this.disposable.dispose();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.searchManager == null) {
            this.searchManager = new SearchManager();

            // Reset source
            ModsSource.clear(mSearchMode.toString());
            ModsSource.setTotalItems(mSearchMode.toString(), 0);
            updateSubtitle();

            this.searchManager.setSearchMode(this.mSearchMode);
        }

        // Use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);

        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
        gestureListener.setOnGestureListener(this);
        mRecyclerView.addOnItemTouchListener(gestureListener);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                mIsLoading = true;

                mProgressBar.setVisibility(View.VISIBLE);

                SearchListFragment.this.performSearch(ModsSource.getLoadedItems(mSearchMode.toString()) + 1);
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

                mLastSearchQuery = query;

                SearchListFragment.this.performSearch(1);

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

        mRecyclerView = view.findViewById(R.id.recycler);
        mSearchView = view.findViewById(R.id.search_query);
        mProgressBar = view.findViewById(R.id.progressbar);
        mEmptyView = view.findViewById(R.id.empty);

        return view;
    }

    @Override
    public void onClick(View v) {
        SearchListViewPager searchViewPager = (SearchListViewPager) getParentFragment();
        searchViewPager.searchGvk(mLastSearchQuery);
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
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(String.valueOf(ModsSource.getTotalItems(mSearchMode.toString())) + " " + getResources().getString(R.string.search_hits));
            }
        }
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

    public void setSearchMode(SearchManager.SEARCH_MODE searchMode) {
        mSearchMode = searchMode;
    }

    private void performSearch(int offset)
    {
        this.searchManager.setSearchQuery(this.mLastSearchQuery);
        this.searchManager.setOffset(offset);

        this.searchManager.getSearchResults(
            this.disposable,
            this,
            getContext()
        );
    }

    @Override
    public void onSearchRequestDone(SruResult sruResult)
    {
        List<ModsItem> modsItems = (List<ModsItem>) sruResult.getResult().get("list");
        ModsSource.setTotalItems(mSearchMode.toString(), (Integer) sruResult.getResult().get("numberOfRecords"));

        mProgressBar.setVisibility(View.GONE);

        if (ModsSource.getTotalItems(mSearchMode.toString()) == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        if (mAdapter == null) {
            ModsSource.clear(mSearchMode.toString());
            mAdapter = new ModsAdapter(modsItems, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.addModsItems(modsItems);
            mAdapter.notifyDataSetChanged();
        }

        ModsSource.addModsItems(mSearchMode.toString(), modsItems);
        if (mModsItemSelectedListener != null) {
            mModsItemSelectedListener.onNewSearchResultsLoaded(mSearchMode);
        }

        mIsLoading = false;

        // dbs counting
        if (!getResources().getString(R.string.bibapp_tracking_url).isEmpty()) {
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
        if (mSearchMode.equals(SearchManager.SEARCH_MODE.LOCAL)) {
            if (ModsSource.getTotalItems(mSearchMode.toString()) == 0) {
                Snackbar
                    .make(getActivity().findViewById(R.id.content_frame), R.string.search_no_results, Snackbar.LENGTH_LONG)
                    .setDuration(5000)
                    .setAction(R.string.search_gvk, this)
                    .setActionTextColor(Color.WHITE)
                    .show();
            }
        }
    }

    @Override
    public void onSearchRequestFailed()
    {
        Toast toast = Toast.makeText(getActivity(), R.string.toast_search_error, Toast.LENGTH_LONG);
        toast.show();

        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }
}
