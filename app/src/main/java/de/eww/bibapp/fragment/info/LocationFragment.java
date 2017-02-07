package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ListIterator;

import de.eww.bibapp.R;
import de.eww.bibapp.model.LocationItem;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationFragment extends Fragment implements
        OnMapReadyCallback {

    private View mFragmentView;
    private TextView mTitleView;
    private TextView mAddressView;
    private TextView mOpeningHoursView;
    private TextView mEmailView;
    private TextView mUrlView;
    private TextView mPhoneView;
    private TextView mDescriptionView;

    private LocationItem mLocationItem = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_location, container, false);

        mFragmentView = view.findViewById(R.id.map);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mAddressView = (TextView) view.findViewById(R.id.address);
        mOpeningHoursView = (TextView) view.findViewById(R.id.opening_hours);
        mEmailView = (TextView) view.findViewById(R.id.email);
        mUrlView = (TextView) view.findViewById(R.id.url);
        mPhoneView = (TextView) view.findViewById(R.id.phone);
        mDescriptionView = (TextView) view.findViewById(R.id.description);

        displayLocation();

        return view;
    }

    public void setLocation(LocationItem location) {
        mLocationItem = location;
        displayLocation();
    }

    private void displayLocation() {
        if (mTitleView == null || mLocationItem == null) {
            return;
        }

        mTitleView.setText(mLocationItem.getName());

        if (mLocationItem.hasOpeningHours()) {
            String finalOpeningHours = "";

            ListIterator<String> it = mLocationItem.getOpeningHours().listIterator();
            if (it.hasNext()) {
                String openingHours = it.next();
                finalOpeningHours += openingHours + "\n";
            }

            mOpeningHoursView.setText(finalOpeningHours);
            mOpeningHoursView.setVisibility(View.VISIBLE);
        } else {
            mOpeningHoursView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasAddress()) {
            mAddressView.setText(mLocationItem.getAddress());
            mAddressView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mAddressView, Linkify.MAP_ADDRESSES);
        } else {
            mAddressView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasEmail()) {
            mEmailView.setText(mLocationItem.getEmail());
            mEmailView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mEmailView, Linkify.EMAIL_ADDRESSES);
        } else {
            mEmailView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasUrl()) {
            mUrlView.setText(mLocationItem.getUrl());
            mUrlView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mUrlView, Linkify.WEB_URLS);
        } else {
            mUrlView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasPhone()) {
            mPhoneView.setText(mLocationItem.getPhone());
            mPhoneView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mPhoneView, Linkify.PHONE_NUMBERS);
        } else {
            mPhoneView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasDescription()) {
            mDescriptionView.setText(mLocationItem.description);
            mDescriptionView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mDescriptionView, Linkify.WEB_URLS);
        } else {
            mDescriptionView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasPosition()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            mFragmentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // everything fine
        mFragmentView.setVisibility(View.VISIBLE);

        // Move camera
        float zoomLevel = (float) (map.getMinZoomLevel() + (map.getMaxZoomLevel() - map.getMinZoomLevel()) * 0.7);
        LatLng latLng = new LatLng(Double.valueOf(mLocationItem.getLat()), Double.valueOf(mLocationItem.getLong()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        // Add marker
        map.addMarker(new MarkerOptions().position(latLng));
    }
}
