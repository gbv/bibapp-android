package de.eww.bibapp.fragments;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsFragment extends SupportMapFragment
{
	private LatLng latLng;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		GoogleMap googleMap = this.getMap();
		
		float zoomLevel = (float) (googleMap.getMinZoomLevel() + (googleMap.getMaxZoomLevel() - googleMap.getMinZoomLevel()) * 0.7);
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, zoomLevel));
		googleMap.addMarker(new MarkerOptions().position(latLng));
	}
	
	public void setLatLng(LatLng latLng)
	{
		this.latLng = latLng;
	}
}