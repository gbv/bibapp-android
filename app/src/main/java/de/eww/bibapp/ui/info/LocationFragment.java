package de.eww.bibapp.ui.info;

import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ListIterator;

import de.eww.bibapp.R;
import de.eww.bibapp.databinding.FragmentLocationBinding;
import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.viewmodel.LocationsViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationFragment extends Fragment implements OnMapReadyCallback {

    FragmentLocationBinding binding;

    LocationItem locationItem;

    public LocationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocationsViewModel viewModel = new ViewModelProvider(requireActivity()).get(LocationsViewModel.class);

        // If locationUrl is set, request information to this specific location, otherwise
        // use the selected location we got from the locations list
        String locationUrl = LocationFragmentArgs.fromBundle(getArguments()).getLocationUrl();
        if (locationUrl != null) {
            viewModel.getSingleLocation().observe(getViewLifecycleOwner(), locationItem -> {
                this.locationItem = locationItem.getSuccess();
                populateView();
            });

            viewModel.loadSingleLocation(locationUrl);
        } else {
            viewModel.getSelected().observe(getViewLifecycleOwner(), locationItem -> {
                this.locationItem = locationItem;
                populateView();
            });
        }
    }

    private void populateView() {
        binding.title.setText(locationItem.getName());

        if (locationItem.hasOpeningHours()) {
            String finalOpeningHours = "";

            ListIterator<String> it = locationItem.getOpeningHours().listIterator();
            if (it.hasNext()) {
                String openingHours = it.next();
                finalOpeningHours += openingHours + "\n";
            }

            binding.openingHours.setText(finalOpeningHours);
            binding.openingHours.setVisibility(View.VISIBLE);
        } else {
            binding.openingHours.setVisibility(View.GONE);
        }

        if (locationItem.hasAddress()) {
            binding.address.setText(locationItem.getAddress());
            binding.address.setVisibility(View.VISIBLE);
            Linkify.addLinks(binding.address, Linkify.MAP_ADDRESSES);
        } else {
            binding.address.setVisibility(View.GONE);
        }

        if (locationItem.hasEmail()) {
            binding.email.setText(locationItem.getEmail());
            binding.email.setVisibility(View.VISIBLE);
            Linkify.addLinks(binding.email, Linkify.EMAIL_ADDRESSES);
        } else {
            binding.email.setVisibility(View.GONE);
        }

        if (locationItem.hasUrl()) {
            binding.url.setText(locationItem.getUrl());
            binding.url.setVisibility(View.VISIBLE);
            Linkify.addLinks(binding.url, Linkify.WEB_URLS);
        } else {
            binding.url.setVisibility(View.GONE);
        }

        if (locationItem.hasPhone()) {
            binding.phone.setText(locationItem.getPhone());
            binding.phone.setVisibility(View.VISIBLE);
            Linkify.addLinks(binding.phone, Linkify.PHONE_NUMBERS);
        } else {
            binding.phone.setVisibility(View.GONE);
        }

        if (locationItem.hasDescription()) {
            binding.description.setText(locationItem.description);
            binding.description.setVisibility(View.VISIBLE);
            Linkify.addLinks(binding.description, Linkify.WEB_URLS);
        } else {
            binding.description.setVisibility(View.GONE);
        }

        // Maps
        if (locationItem.hasPosition()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            binding.map.setVisibility(View.VISIBLE);
        } else {
            binding.map.setVisibility(View.GONE);
        }
    }

//    public void onLocationLoadFinished(LocationItem locationItem) {
//        LocationSource.clear();
//        LocationSource.addLocation(locationItem);

        // TODO
//
//        Intent locationIntent = new Intent(getActivity(), LocationActivity.class);
//        locationIntent.putExtra("locationIndex", 0);
//
//        if (mIsWatchlistFragment) {
//            locationIntent.putExtra("source", "watchlist");
//        } else {
//            locationIntent.putExtra("source", "search");
//        }
//
//        Fragment parentFragment = getParentFragment();
//        if (parentFragment != null) {
//            parentFragment.startActivityForResult(locationIntent, 99);
//        } else {
//            startActivityForResult(locationIntent, 99);
//        }
//    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Move camera
        float zoomLevel = (float) (map.getMinZoomLevel() + (map.getMaxZoomLevel() - map.getMinZoomLevel()) * 0.7);
        LatLng latLng = new LatLng(Double.valueOf(this.locationItem.getLat()), Double.valueOf(this.locationItem.getLong()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        // Add marker
        map.addMarker(new MarkerOptions().position(latLng));
    }
}