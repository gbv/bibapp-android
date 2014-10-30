package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.AsyncCanceledInterface;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.LocationAdapter;
import de.eww.bibapp.model.LocationItem;
import de.eww.bibapp.tasks.LocationsJsonLoader;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationsFragment extends RoboFragment implements
        LoaderManager.LoaderCallbacks<List<LocationItem>>,
        AsyncCanceledInterface {

    @InjectView(R.id.locations_view) RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<LocationItem> mItemList = new ArrayList<LocationItem>();

    // The listener we are to notify when a location is selected
    OnLocationSelectedListener mLocationSelectedListener = null;

    /**
     * Represents a listener that will be notified of location selections.
     */
    public interface OnLocationSelectedListener {
        /**
         * Call when a given location is selected.
         *
         * @param index the index of the selected location.
         */
        public void onLocationSelected(int index);
    }

    /**
     * Sets the listener that should be notified of location selection events.
     *
     * @param listener the listener to notify.
     */
    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        mLocationSelectedListener = listener;
    }

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

        // Start the Request
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.destroyLoader(0);
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<List<LocationItem>> onCreateLoader(int arg0, Bundle arg1) {
        return new LocationsJsonLoader(getActivity().getApplicationContext(), this);
    }

    @Override
    public void onLoadFinished(Loader<List<LocationItem>> loader, List<LocationItem> data) {
        mItemList.addAll(data);

        getActivity().setProgressBarVisibility(false);

        mAdapter = new LocationAdapter(mItemList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<LocationItem>> loader) {
        // empty
    }

    @Override
    public void onAsyncCanceled() {
        // TODO:
        //		this.setListShown(true);
//
//		if ( this.getView() != null )
//		{
//			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
//			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
//		}
    }
}
