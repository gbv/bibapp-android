//package de.eww.bibapp.fragments.info;
//
//import android.app.ActionBar;
//import android.os.Bundle;
//import android.support.v4.app.LoaderManager;
//import android.support.v4.content.Loader;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ListView;
//
//import java.util.List;
//
//import de.eww.bibapp.AsyncCanceledInterface;
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.adapters.LocationsAdapter;
//import de.eww.bibapp.data.LocationsEntry;
//import de.eww.bibapp.fragments.AbstractListFragment;
//import de.eww.bibapp.fragments.dialogs.LoadCanceledDialogFragment;
//import de.eww.bibapp.tasks.LocationsJsonLoader;
//
///**
// * @author Christoph Schönfeld - effective WEBWORK GmbH
// *
// * This file is part of the Android BibApp Project
// * =========================================================
// * Locations Fragment, providing a list of locations associated with the library
// */
//public class LocationsFragment extends AbstractListFragment implements
//	LoaderManager.LoaderCallbacks<List<LocationsEntry>>,
//	AsyncCanceledInterface
//{
//	// This is the Adapter being used to display the list's data.
//    LocationsAdapter mAdapter;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		// enable option menu
//		this.setHasOptionsMenu(true);
//	}
//
//	@Override
//    public void onActivityCreated(Bundle savedInstanceState)
//	{
//        super.onActivityCreated(savedInstanceState);
//
//        // create and auto start loader
//        LoaderManager loaderManager = this.getLoaderManager();
//        loaderManager.destroyLoader(0);
//		loaderManager.initLoader(0, null, this);
//
//        this.mAdapter = new LocationsAdapter(getActivity(), R.layout.fragment_locations_item_view);
//
//        this.setListAdapter(mAdapter);
//        this.isListShown = true;
//        this.setListShown(false);
//
//        this.getActivity().invalidateOptionsMenu();
//    }
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_locations_main, container, false);
//
//		ActionBar actionBar = MainActivity.instance.getActionBar();
//
//		// set title
//		actionBar.setTitle(R.string.actionbar_info);
//		actionBar.setSubtitle(R.string.info_button_locations);
//
//		// enable up navigation
//		actionBar.setDisplayHomeAsUpEnabled(true);
//
//		return v;
//	}
//
//	public LocationsEntry getItem()
//	{
//		return (LocationsEntry) this.getItem(this.lastClickedPosition);
//	}
//
//	public void onListItemClick(ListView l, View v, int position, long id)
//	{
//		this.lastClickedPosition = position;
//		LocationsDetailFragment.entry = this.getItem();
//
//		InfoContainerFragment infoFragment = (InfoContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("info");
//		infoFragment.switchContent(R.id.info_container, LocationsDetailFragment.class.getName(), "info_location", true);
//	}
//
//	@Override
//	public Loader<List<LocationsEntry>> onCreateLoader(int arg0, Bundle arg1)
//	{
//		return new LocationsJsonLoader(getActivity().getApplicationContext(), this);
//	}
//
//	@Override
//	public void onLoadFinished(Loader<List<LocationsEntry>> loader, List<LocationsEntry> data)
//	{
//		this.mAdapter.clear();
//		this.mAdapter.addAll(data);
//		this.setListShown(true);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<List<LocationsEntry>> arg0)
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch ( item.getItemId() )
//	    {
//	        case android.R.id.home:
//	        	// app icon in action bar clicked; go up
//	        	InfoContainerFragment infoFragment = (InfoContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("info");
//	    		infoFragment.up();
//
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
//
//	@Override
//	public void onAsyncCanceled()
//	{
//		this.setListShown(true);
//
//		if ( this.getView() != null )
//		{
//			LoadCanceledDialogFragment loadCanceledDialog = new LoadCanceledDialogFragment();
//			loadCanceledDialog.show(this.getChildFragmentManager(), "load_canceled");
//		}
//	}
//
//	@Override
//	public void onPrepareOptionsMenu(Menu menu)
//	{
//		super.onPrepareOptionsMenu(menu);
//		menu.clear();
//	}
//}