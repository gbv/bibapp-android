package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ListIterator;

import de.eww.bibapp.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        return view;
    }

    public void displayLocation(LocationItem location) {
        mTitleView.setText(location.getName());

        if (location.hasOpeningHours()) {
            String finalOpeningHours = "";

            ListIterator<String> it = location.getOpeningHours().listIterator();
            if (it.hasNext()) {
                String openingHours = it.next();
                finalOpeningHours += openingHours + "\n";
            }

            mOpeningHoursView.setText(finalOpeningHours);
            mOpeningHoursView.setVisibility(View.VISIBLE);
        }

        if (location.hasAddress()) {
            mAddressView.setText(location.getAddress());
            mAddressView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mAddressView, Linkify.MAP_ADDRESSES);
        }

        if (location.hasEmail()) {
            mEmailView.setText(location.getEmail());
            mEmailView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mEmailView, Linkify.EMAIL_ADDRESSES);
        }

        if (location.hasUrl()) {
            mUrlView.setText(location.getUrl());
            mUrlView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mUrlView, Linkify.WEB_URLS);
        }

        if (location.hasPhone()) {
            mPhoneView.setText(location.getPhone());
            mPhoneView.setVisibility(View.VISIBLE);
            Linkify.addLinks(mPhoneView, Linkify.PHONE_NUMBERS);
        }

        if (location.hasDescription()) {
            mDescriptionView.setText(location.description);
            mDescriptionView.setVisibility(View.VISIBLE);
        }

        if (location.hasPosition()) {
            mFrameLayout.setVisibility(View.VISIBLE);

            // TODO
//			GoogleMapsFragment mapFragment = (GoogleMapsFragment) Fragment.instantiate(this.getActivity(), GoogleMapsFragment.class.getName());
//			LatLng latLng = new LatLng(Double.valueOf(item.posLat), Double.valueOf(item.posLong));
//			mapFragment.setLatLng(latLng);
//
//			FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
//			transaction.add(R.id.locations_detail_maps_container, mapFragment);
//			transaction.commit();
        }
    }
}
