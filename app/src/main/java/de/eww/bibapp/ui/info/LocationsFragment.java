package de.eww.bibapp.ui.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.LocationAdapter;
import de.eww.bibapp.databinding.FragmentLocationsBinding;
import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.network.model.StatefullData;
import de.eww.bibapp.viewmodel.LocationsViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationsFragment extends Fragment {

    private FragmentLocationsBinding binding;

    private LocationsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationsBinding.inflate(inflater, container, false);

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

        viewModel = new ViewModelProvider(requireActivity()).get(LocationsViewModel.class);

        // Not using view binding here because it relies on if the view is visible in the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        // Handle navigation based on having a single pane layout or a two pane layout
        View.OnClickListener onClickListener = itemView -> {
            LocationItem locationItem = (LocationItem) itemView.getTag();
            viewModel.select(locationItem);

            if (itemDetailFragmentContainer != null) {
                // two pane
                Navigation.findNavController(itemDetailFragmentContainer).navigate(R.id.locations_sub_navigation);
            } else {
                // single pane
                Navigation.findNavController(itemView).navigate(R.id.action_nav_locations_to_nav_location);
            }
        };

        RecyclerView recyclerView = binding.itemList;
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        LocationAdapter locationAdapter = new LocationAdapter(onClickListener);
        recyclerView.setAdapter(locationAdapter);

        LiveData<StatefullData<List<LocationItem>>> liveData = viewModel.getLocations();
        if (liveData.getValue() == null) {
            binding.swiperefresh.setRefreshing(true);
        }
        liveData.observe(getViewLifecycleOwner(), locations -> {
            binding.empty.setVisibility(View.GONE);
            binding.swiperefresh.setRefreshing(false);

            if (locations.getError()) {
                Snackbar.make(binding.swiperefresh, R.string.toast_locations_error, Snackbar.LENGTH_LONG).show();
            } else {
                if (locations.getData().isEmpty()) {
                    binding.empty.setVisibility(View.VISIBLE);
                } else {
                    locationAdapter.setLocationList(locations.getData());

                    // select first item in two pane
                    if (itemDetailFragmentContainer != null && !locations.getData().isEmpty()) {
                        viewModel.select(locations.getData().get(0));
                    }
                }
            }
        });

        binding.swiperefresh.setOnRefreshListener(() -> viewModel.refreshLocations());
    }
}