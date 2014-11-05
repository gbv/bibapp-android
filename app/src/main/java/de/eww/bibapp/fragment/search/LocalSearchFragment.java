package de.eww.bibapp.fragment.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.listener.RecyclerItemClickListener;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.model.source.ModsSource;
import de.eww.bibapp.tasks.SearchXmlLoader;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 03.11.14.
 */
public class LocalSearchFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<HashMap<String, Object>>,
        AsyncCanceledInterface {

    @Inject ModsSource mModsSource;

    @InjectView(R.id.search_results) RecyclerView mRecyclerView;

    @InjectView(R.id.search_query) SearchView mSearchView;

    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean mIsLoading = false;
    private int mTotalHits = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Improve performance for RecyclerView by setting it to a fixed size,
        // since we now that changes in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                /*
                if (mLocationSelectedListener != null) {
                    mLocationSelectedListener.onLocationSelected(position);
                }
                */
            }
        }));
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
                        if (totalItemCount < mTotalHits) {
                            // Did we reach the end of the list?
                            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + 0)) {
                                mIsLoading = true;

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
                getActivity().setProgressBarVisibility(false);

                Loader<HashMap<String, Object>> loader = getLoaderManager().getLoader(0);
                SearchXmlLoader searchXmlLoader = (SearchXmlLoader) loader;

                searchXmlLoader.setSearchString(query);
                searchXmlLoader.resetOffset();
                searchXmlLoader.forceLoad();
                //this.previousResults.clear();

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
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_search, container, false);
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

        mTotalHits = (Integer) data.get("numberOfRecords");

        mModsSource.clear();
        mModsSource.addModsItems(modsItems);

        getActivity().setProgressBarVisibility(false);

        mAdapter = new ModsAdapter(modsItems, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mIsLoading = false;
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, Object>> loader) {
        // empty
    }

    @Override
    public void onAsyncCanceled() {
        // TODO:
//		this.setListShown(true);
//		this.getLoaderManager().destroyLoader(0);
//		this.getLoaderManager().initLoader(0, null, this);
//
//		if ( this.getView() != null )
//		{
//			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
//			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
//		}
    }
}
