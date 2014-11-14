package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ListIterator;

import de.eww.bibapp.R;
import de.eww.bibapp.fragment.GoogleMapsFragment;
import de.eww.bibapp.model.LocationItem;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationFragment extends RoboFragment {

    @InjectView(R.id.map) FrameLayout mFrameLayout;
    @InjectView(R.id.title) TextView mTitleView;
    @InjectView(R.id.address) TextView mAddressView;
    @InjectView(R.id.opening_hours) TextView mOpeningHoursView;
    @InjectView(R.id.email) TextView mEmailView;
    @InjectView(R.id.url) TextView mUrlView;
    @InjectView(R.id.phone) TextView mPhoneView;
    @InjectView(R.id.description) TextView mDescriptionView;

    LocationItem mLocationItem = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        displayLocation();
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
        } else {
            mDescriptionView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasPosition()) {
            mFrameLayout.setVisibility(View.VISIBLE);

			GoogleMapsFragment mapFragment = (GoogleMapsFragment) Fragment.instantiate(this.getActivity(), GoogleMapsFragment.class.getName());
			LatLng latLng = new LatLng(Double.valueOf(mLocationItem.posLat), Double.valueOf(mLocationItem.posLong));
			mapFragment.setLatLng(latLng);

			FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.map, mapFragment);
			transaction.commit();
        } else {
            mFrameLayout.setVisibility(View.GONE);
        }
    }
}
