package de.eww.bibapp.tasks;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.DaiaLoaderInterface;
import de.eww.bibapp.adapters.AvailableAdapter;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.data.AvailableEntry;
import de.eww.bibapp.fragments.AbstractListFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Callback for daia communication
 */
public class DaiaLoaderCallback implements
	LoaderManager.LoaderCallbacks<List<AvailableEntry>>,
	LocationListener
{
	private DaiaLoaderInterface daiaLoaderInterface = null;
	private Location userLocation = null;
	private List<AvailableEntry> loaderData = null;
	private boolean isLocalSearch;
	private boolean locationServiceAvailable = false;
	
	public DaiaLoaderCallback(DaiaLoaderInterface daiaLoaderInterface) {
		this.daiaLoaderInterface = daiaLoaderInterface;
	}

	@Override
	public Loader<List<AvailableEntry>> onCreateLoader(int loaderIndex, Bundle arg1)
	{
		Loader<List<AvailableEntry>> loader = new DaiaLoader(((Fragment) this.daiaLoaderInterface).getActivity(), (Fragment) this.daiaLoaderInterface);
		((DaiaLoader) loader).setPpn(this.daiaLoaderInterface.getSearchItem().ppn);
		((DaiaLoader) loader).setFromLocalSearch(this.daiaLoaderInterface.getSearchItem().isLocalSearch);
		((DaiaLoader) loader).setItem(this.daiaLoaderInterface.getSearchItem());
		this.isLocalSearch = this.daiaLoaderInterface.getSearchItem().isLocalSearch;
		
		this.loaderData = null;
		
		// if in gvk search, get the current user location
		if ( this.isLocalSearch == false )
		{
			// acquire a reference to the system location manager
			LocationManager locationManager = (LocationManager) ((Fragment) this.daiaLoaderInterface).getActivity().getSystemService(Context.LOCATION_SERVICE);
			
			if ( locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
			{
				locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
				locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
				
				// reset user location
				this.userLocation = null;
				
				this.locationServiceAvailable = true;
			}
		}
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<AvailableEntry>> loader, List<AvailableEntry> data)
	{
		this.loaderData = data;
		
		// if the location request is done
		if ( this.isLocalSearch == true || this.userLocation != null || this.locationServiceAvailable == false )
		{
			this.loadingDone();
		}
	}
	
	private void loadingDone()
	{
		/**
		 * post process data if in gvk search
		 */
		if ( this.isLocalSearch == false && this.locationServiceAvailable == true )
		{	
			HashMap<String, AvailableEntry> hashMap = new HashMap<String, AvailableEntry>();
			
			Iterator<AvailableEntry> it = this.loaderData.iterator();
			while ( it.hasNext() )
			{
				AvailableEntry entry = it.next();
				
				if ( hashMap.containsKey(entry.department) )
				{
					AvailableEntry hashEntry = hashMap.get(entry.department);
					hashEntry.label += ", " + entry.label;
					hashMap.put(entry.department, hashEntry);
				}
				else
				{
					hashMap.put(entry.department, entry);
				}
				
				if ( entry.locationsEntry != null )
				{
					// get geo data
					double b1 = Double.parseDouble(entry.locationsEntry.posLat);
					double l1 = Double.parseDouble(entry.locationsEntry.posLong);
					
					double b2 = this.userLocation.getLatitude();
					double l2 = this.userLocation.getLongitude();
					
					// calculcate distance
					double f = Constants.EARTH_FLATTENING;
					double a = Constants.EQUATORIAL_RADIUS;
					
					double F = (b1 + b2) / 2;
					double G = (b1 - b2) / 2;
					double l = (l1 - l2) / 2;
					
					// transform into radian measure
					F = Math.PI / 180 * F;
					G = Math.PI / 180 * G;
					l = Math.PI / 180 * l;
					
					// calculate coarsely distance
					double S = Math.pow(Math.sin(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.cos(F), 2) * Math.pow(Math.sin(l), 2);
					double C = Math.pow(Math.cos(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.sin(F), 2) * Math.pow(Math.sin(l), 2);
					double w = Math.atan(Math.sqrt(S / C));
					double D = 2 * w * a;
					
					// adjust distance with factors H1 and H2
					double R = Math.sqrt(S * C) / w;
					double H1 = (3 * R - 1) / (2 * C);
					double H2 = (3 * R + 1) / (2 * S);
					
					// calculate the final distance
					double s = D * (1 + f * H1 * Math.pow(Math.sin(f), 2) * Math.pow(Math.cos(G), 2) - f * H2 * Math.pow(Math.cos(f), 2) * Math.pow(Math.sin(G), 2));
					
					entry.distance = s;
				}
			}
			
			this.loaderData = new ArrayList<AvailableEntry>(hashMap.values());
			
			// sort by distance
			Collections.sort(this.loaderData);
		}
		
		AvailableAdapter adapter = this.daiaLoaderInterface.getAdapter();
		
		adapter.clear();
		adapter.addAll(this.loaderData);
		
		// Show the list
		if ( ((Fragment) this.daiaLoaderInterface).isResumed() )
		{
			((AbstractListFragment) this.daiaLoaderInterface).setListShown(true);
		}
		else
		{
			((AbstractListFragment) this.daiaLoaderInterface).setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<AvailableEntry>> arg0)
	{
		// Clear the data in the adapter.
		AvailableAdapter adapter = this.daiaLoaderInterface.getAdapter();
		adapter.clear();
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.userLocation = location;
		
		if ( ((Fragment) this.daiaLoaderInterface).isAdded() )
		{
			LocationManager locationManager = (LocationManager) ((Fragment) this.daiaLoaderInterface).getActivity().getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(this);
		}
		
		if ( this.loaderData != null )
		{
			this.loadingDone();
		}
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		
	}
}
