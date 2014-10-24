//package de.eww.bibapp.fragments.info;
//
//import android.app.ActionBar;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.text.util.Linkify;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.ListIterator;
//
//import de.eww.bibapp.MainActivity;
//import de.eww.bibapp.R;
//import de.eww.bibapp.data.LocationsEntry;
//import de.eww.bibapp.fragments.AbstractContainerFragment;
//import de.eww.bibapp.fragments.GoogleMapsFragment;
//
///**
// * @author Christoph Schönfeld - effective WEBWORK GmbH
// *
// * This file is part of the Android BibApp Project
// * =========================================================
// * Locations Detail Fragment, display detail information about a location and contains a google maps view
// */
//public class LocationsDetailFragment extends DialogFragment
//{
//	public static LocationsEntry entry;
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
//		this.getActivity().invalidateOptionsMenu();
//    }
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//	{
//		LocationsEntry item = LocationsDetailFragment.entry;
//
//		// inflate the layout for this fragment
//		View v = inflater.inflate(R.layout.fragment_locations_detail_view, container, false);
//
//		ActionBar actionBar = MainActivity.instance.getActionBar();
//
//		// set title
//		if ( MainActivity.currentTabId.equals("search") )
//		{
//			actionBar.setTitle(R.string.actionbar_search);
//		}
//		else if ( MainActivity.currentTabId.equals("watchlist") )
//		{
//			actionBar.setTitle(R.string.actionbar_watchlist);
//		}
//		else
//		{
//			actionBar.setTitle(R.string.actionbar_info);
//		}
//
//		actionBar.setSubtitle(null);
//
//		// enable up navigation
//		actionBar.setDisplayHomeAsUpEnabled(true);
//
//		// set data
//		TextView titleView = (TextView) v.findViewById(R.id.locations_detail_item_title);
//		TextView addressView = (TextView) v.findViewById(R.id.locations_detail_item_address);
//		TextView openingHoursView = (TextView) v.findViewById(R.id.locations_detail_item_opening_hours);
//		TextView emailView = (TextView) v.findViewById(R.id.locations_detail_item_email);
//		TextView urlView = (TextView) v.findViewById(R.id.locations_detail_item_url);
//		TextView phoneView = (TextView) v.findViewById(R.id.locations_detail_item_phone);
//		TextView descriptionView = (TextView) v.findViewById(R.id.locations_detail_item_description);
//
//		titleView.setText(item.name);
//
//		if ( !item.openingHours.isEmpty() )
//		{
//			String finalOpeningHours = "";
//
//			ListIterator<String> it = item.openingHours.listIterator();
//			if ( it.hasNext() )
//			{
//				String openingHours = it.next();
//				finalOpeningHours += openingHours + "\n";
//			}
//
//			openingHoursView.setText(finalOpeningHours);
//			openingHoursView.setVisibility(View.VISIBLE);
//		}
//
//		if ( !item.address.isEmpty() )
//		{
//			addressView.setText(item.address);
//			addressView.setVisibility(View.VISIBLE);
//			Linkify.addLinks(addressView, Linkify.MAP_ADDRESSES);
//		}
//
//		if ( !item.email.isEmpty() )
//		{
//			emailView.setText(item.email);
//			emailView.setVisibility(View.VISIBLE);
//			Linkify.addLinks(emailView, Linkify.EMAIL_ADDRESSES);
//		}
//
//		if ( !item.url.isEmpty() )
//		{
//			urlView.setText(item.url);
//			urlView.setVisibility(View.VISIBLE);
//			Linkify.addLinks(urlView, Linkify.WEB_URLS);
//		}
//
//		if ( !item.phone.isEmpty() )
//		{
//			phoneView.setText(item.phone);
//			phoneView.setVisibility(View.VISIBLE);
//			Linkify.addLinks(phoneView, Linkify.PHONE_NUMBERS);
//		}
//
//		if ( !item.description.isEmpty() )
//		{
//			descriptionView.setText(item.description);
//			descriptionView.setVisibility(View.VISIBLE);
//		}
//
//		if ( !item.posLat.isEmpty() && !item.posLong.isEmpty() )
//		{
//			FrameLayout frameLayout = (FrameLayout) v.findViewById(R.id.locations_detail_maps_container);
//			frameLayout.setVisibility(View.VISIBLE);
//
//			GoogleMapsFragment mapFragment = (GoogleMapsFragment) Fragment.instantiate(this.getActivity(), GoogleMapsFragment.class.getName());
//			LatLng latLng = new LatLng(Double.valueOf(item.posLat), Double.valueOf(item.posLong));
//			mapFragment.setLatLng(latLng);
//
//			FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
//			transaction.add(R.id.locations_detail_maps_container, mapFragment);
//			transaction.commit();
//		}
//
//		return v;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	    switch ( item.getItemId() )
//	    {
//	        case android.R.id.home:
//	        	// app icon in action bar clicked; go up
//	        	AbstractContainerFragment containerFragment = (AbstractContainerFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag(MainActivity.currentTabId);
//	        	containerFragment.up();
//
//	            return true;
//	        default:
//	            return super.onOptionsItemSelected(item);
//	    }
//	}
//
//	@Override
//	public void onPrepareOptionsMenu(Menu menu)
//	{
//		super.onPrepareOptionsMenu(menu);
//		menu.clear();
//	}
//}