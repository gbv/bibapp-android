package de.eww.bibapp.fragment.info;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ListIterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.eww.bibapp.R;
import de.eww.bibapp.model.LocationItem;

/**
 * Created by christoph on 25.10.14.
 */
public class LocationFragment extends Fragment implements
        OnMapReadyCallback {

    @BindView(R.id.map) View fragmentView;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.address) TextView addressView;
    @BindView(R.id.opening_hours) TextView openingHoursView;
    @BindView(R.id.email) TextView emailView;
    @BindView(R.id.url) TextView urlView;
    @BindView(R.id.phone) TextView phoneView;
    @BindView(R.id.description) TextView descriptionView;

    private LocationItem mLocationItem = null;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_location, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        displayLocation();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.unbinder.unbind();
    }

    public void setLocation(LocationItem location) {
        mLocationItem = location;
        displayLocation();
    }

    private void displayLocation() {
        if (this.titleView == null || mLocationItem == null) {
            return;
        }

        this.titleView.setText(mLocationItem.getName());

        if (mLocationItem.hasOpeningHours()) {
            String finalOpeningHours = "";

            ListIterator<String> it = mLocationItem.getOpeningHours().listIterator();
            if (it.hasNext()) {
                String openingHours = it.next();
                finalOpeningHours += openingHours + "\n";
            }

            this.openingHoursView.setText(finalOpeningHours);
            this.openingHoursView.setVisibility(View.VISIBLE);
        } else {
            this.openingHoursView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasAddress()) {
            this.addressView.setText(mLocationItem.getAddress());
            this.addressView.setVisibility(View.VISIBLE);
            Linkify.addLinks(this.addressView, Linkify.MAP_ADDRESSES);
        } else {
            this.addressView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasEmail()) {
            this.emailView.setText(mLocationItem.getEmail());
            this.emailView.setVisibility(View.VISIBLE);
            Linkify.addLinks(this.emailView, Linkify.EMAIL_ADDRESSES);
        } else {
            this.emailView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasUrl()) {
            this.urlView.setText(mLocationItem.getUrl());
            this.urlView.setVisibility(View.VISIBLE);
            Linkify.addLinks(this.urlView, Linkify.WEB_URLS);
        } else {
            this.urlView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasPhone()) {
            this.phoneView.setText(mLocationItem.getPhone());
            this.phoneView.setVisibility(View.VISIBLE);
            Linkify.addLinks(this.phoneView, Linkify.PHONE_NUMBERS);
        } else {
            this.phoneView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasDescription()) {
            this.descriptionView.setText(mLocationItem.description);
            this.descriptionView.setVisibility(View.VISIBLE);
            Linkify.addLinks(this.descriptionView, Linkify.WEB_URLS);
        } else {
            this.descriptionView.setVisibility(View.GONE);
        }

        if (mLocationItem.hasPosition()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            this.fragmentView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // everything fine
        this.fragmentView.setVisibility(View.VISIBLE);

        // Move camera
        float zoomLevel = (float) (map.getMinZoomLevel() + (map.getMaxZoomLevel() - map.getMinZoomLevel()) * 0.7);
        LatLng latLng = new LatLng(Double.valueOf(mLocationItem.getLat()), Double.valueOf(mLocationItem.getLong()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        // Add marker
        map.addMarker(new MarkerOptions().position(latLng));
    }
}
