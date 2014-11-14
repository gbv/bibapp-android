package de.eww.bibapp.tasks;

import android.content.Context;
import android.location.Criteria;
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

import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DaiaItem;
import de.eww.bibapp.model.ModsItem;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Callback for daia communication
*/
public class DaiaLoaderCallback implements
	LoaderManager.LoaderCallbacks<List<DaiaItem>>,
	LocationListener
{
	private DaiaLoaderInterface daiaLoaderInterface = null;
	private Location userLocation = null;
	private List<DaiaItem> loaderData = null;
	private boolean isLocalSearch;
	private boolean locationServiceAvailable = false;

    public interface DaiaLoaderInterface
    {
        public ModsItem getModsItem();
        public void onDaiaRequestDone(List<DaiaItem> daiaItems);
    }

	public DaiaLoaderCallback(DaiaLoaderInterface daiaLoaderInterface) {
		this.daiaLoaderInterface = daiaLoaderInterface;
	}

	@Override
	public Loader<List<DaiaItem>> onCreateLoader(int loaderIndex, Bundle arg1)
	{
		Loader<List<DaiaItem>> loader = new DaiaLoader(((Fragment) this.daiaLoaderInterface).getActivity(), (Fragment) this.daiaLoaderInterface);
		((DaiaLoader) loader).setPpn(this.daiaLoaderInterface.getModsItem().ppn);
		((DaiaLoader) loader).setFromLocalSearch(this.daiaLoaderInterface.getModsItem().isLocalSearch);
		((DaiaLoader) loader).setItem(this.daiaLoaderInterface.getModsItem());
		this.isLocalSearch = this.daiaLoaderInterface.getModsItem().isLocalSearch;

		this.loaderData = null;

		// if in gvk search, get the current user location
		if ( this.isLocalSearch == false )
		{
			// acquire a reference to the system location manager
			LocationManager locationManager = (LocationManager) ((Fragment) this.daiaLoaderInterface).getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);

            if (provider != null) {
                locationManager.requestSingleUpdate(provider, this, null);

                // reset user location
                this.userLocation = null;

                this.locationServiceAvailable = true;
            }
		}

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<DaiaItem>> loader, List<DaiaItem> data)
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
			HashMap<String, DaiaItem> hashMap = new HashMap<String, DaiaItem>();

			Iterator<DaiaItem> it = this.loaderData.iterator();
			while ( it.hasNext() )
			{
                DaiaItem entry = it.next();

				if ( hashMap.containsKey(entry.department) )
				{
                    DaiaItem hashEntry = hashMap.get(entry.department);
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

			this.loaderData = new ArrayList<DaiaItem>(hashMap.values());

			// sort by distance
			Collections.sort(this.loaderData);
		}

        this.daiaLoaderInterface.onDaiaRequestDone(this.loaderData);
	}

	@Override
	public void onLoaderReset(Loader<List<DaiaItem>> arg0) {
		// empty
	}

	@Override
	public void onLocationChanged(Location location) {
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
	public void onProviderDisabled(String provider) {
		// empty
	}

	@Override
	public void onProviderEnabled(String provider) {
		// empty
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// empty
	}
}
