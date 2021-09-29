package de.eww.bibapp.ui.mods;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.activity.WebViewActivity;
import de.eww.bibapp.adapter.DaiaAdapter;
import de.eww.bibapp.databinding.FragmentModsBinding;
import de.eww.bibapp.fragment.dialog.DetailActionsDialogFragment;
import de.eww.bibapp.fragment.dialog.PaiaActionDialogFragment;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.DaiaItems;
import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.tasks.DownloadImageTask;
import de.eww.bibapp.util.DaiaHelper;
import de.eww.bibapp.util.UrlHelper;
import de.eww.bibapp.viewmodel.ModsViewModel;
import de.eww.bibapp.viewmodel.ModsViewModelFactory;

public class ModsFragment extends Fragment implements
        DetailActionsDialogFragment.DetailActionsDialogLisener,
//        PaiaActionDialogFragment.PaiaActionDialogListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private FragmentModsBinding binding;
    private ModsViewModel viewModel;
    ModsItem modsItem = null;
    private List<ModsItem> watchlistItems;
    private DaiaAdapter daiaAdapter;
    private boolean mIsWatchlistFragment = false;
    private DaiaItem lastSelectedDaiaItem;
    private MenuItem addToWatchlistItem;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;

//    private PaiaActionDialogFragment mPaiaDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (this.googleApiClient == null) {
            this.googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        this.googleApiClient.connect();

        super.onStart();
    }

    @Override
    public void onStop() {
        this.googleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentModsBinding.inflate(inflater, container, false);

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

        viewModel = new ViewModelProvider(requireActivity(), new ModsViewModelFactory(requireActivity().getApplication())).get(ModsViewModel.class);

        RecyclerView recyclerView = binding.list.itemList;
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        viewModel.getWatchlistResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                return;
            }

            if (result.getSuccess() != null) {
                watchlistItems = result.getSuccess();
            }
        });

        if (viewModel.getWatchlistResult().getValue() == null) {
            viewModel.loadWatchlist();
        }

        viewModel.getISBDResult().observe(getViewLifecycleOwner(), isbdResult -> {
            if (isbdResult.getError() != null) {
                onRequestFailed(isbdResult.getError());
            }
            if (isbdResult.getSuccess() != null) {
                onISBDSuccess(isbdResult.getSuccess());
            }
        });

        viewModel.getAvailabilityResult().observe(getViewLifecycleOwner(), availabilityResult -> {
            binding.list.empty.setVisibility(View.GONE);
            binding.list.swiperefresh.setRefreshing(false);

            if (availabilityResult.getError() != null) {
                onRequestFailed(availabilityResult.getError());
            }
            if (availabilityResult.getSuccess() != null) {
                onAvailabilitySuccess(availabilityResult.getSuccess());
            }
        });

        viewModel.getSelected().observe(getViewLifecycleOwner(), modsItem -> {
            this.modsItem = modsItem;

            viewModel.loadISBD(modsItem);

            binding.list.swiperefresh.setRefreshing(true);
            binding.list.empty.setVisibility(View.GONE);

            daiaAdapter = new DaiaAdapter(modsItem, requireContext(), onDaiaClickListener);
            recyclerView.setAdapter(daiaAdapter);

            viewModel.loadAvailability(modsItem);
            binding.list.swiperefresh.setOnRefreshListener(() -> viewModel.loadAvailability(modsItem));

            binding.modsLayout.setVisibility(View.VISIBLE);
            binding.modsNone.setVisibility(View.GONE);

            requireActivity().invalidateOptionsMenu();

            // title
            binding.title.setText(modsItem.title);

            // sub
            String subTitle = modsItem.subTitle;
            if (modsItem.partName.isEmpty() && modsItem.partNumber.isEmpty() && !subTitle.isEmpty()) {
                subTitle += "\n";
            }
            if (!modsItem.partName.isEmpty()) {
                subTitle += modsItem.partName;
            }
            if (!modsItem.partName.isEmpty()) {
                subTitle += "; " + modsItem.partNumber;
            }
            binding.sub.setText(subTitle);

            // image
            AsyncTask<String, Void, Bitmap> imageTask = new DownloadImageTask(binding.image, modsItem, getActivity());
            imageTask.execute(String.format(this.getResources().getString(R.string.bibapp_cover_url), modsItem.isbn));

            // index
            if (!modsItem.indexArray.isEmpty()) {
                binding.indexContainer.setVisibility(View.VISIBLE);

                binding.indexContainer.setOnClickListener((View v) -> {
                    onClickIndex();
                });
            } else {
                binding.indexContainer.setVisibility(View.GONE);
            }

            // interlanding
            if (!modsItem.isLocalSearch) {
                binding.interlandingContainer.setVisibility(View.VISIBLE);

                binding.interlandingContainer.setOnClickListener((View v) -> {
                    onClickInterlanding();
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the toolbar
        inflater.inflate(R.menu.mods_fragment_mode_actions, menu);

        addToWatchlistItem = menu.findItem(R.id.menu_mods_add_to_watchlist);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (modsItem != null) {
            if (mIsWatchlistFragment) {
                addToWatchlistItem.setVisible(false);
                addToWatchlistItem.setEnabled(false);
                addToWatchlistItem.setTitle(R.string.menu_detail_addtowatchlist_duplicate);
            } else {
                if (watchlistItems != null) {
                    // Disable "add to watchlist" if the item is already in it and change the text
                    if (watchlistItems.contains(modsItem)) {
                        addToWatchlistItem.setTitle(R.string.menu_detail_addtowatchlist_duplicate);
                        addToWatchlistItem.setEnabled(false);
                    } else {
                        addToWatchlistItem.setEnabled(true);
                    }
                }
            }
        } else {
            addToWatchlistItem.setVisible(false);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_mods_add_to_watchlist) {
            // Add to watchlist
            if (modsItem != null) {
                viewModel.addToWatchlist(modsItem);

                // Display Snackbar
                Snackbar.make(binding.modsLayout, getResources().getString(R.string.toast_watchlist_added), Snackbar.LENGTH_LONG).show();

                // Disable menu item
                item.setTitle(R.string.menu_detail_addtowatchlist_duplicate);
                item.setEnabled(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setIsWatchlistFragment(boolean isWatchlistFragment) {
        mIsWatchlistFragment = isWatchlistFragment;
    }

    @Override
    public void onActionLocation(DialogFragment dialog) {
        try {
            String requestUrl = DaiaHelper.getUriUrl(lastSelectedDaiaItem);

            ModsFragmentDirections.ActionNavModsToNavLocation action = ModsFragmentDirections.actionNavModsToNavLocation();
            action.setLocationUrl(requestUrl);
            NavHostFragment.findNavController(this).navigate(action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActionRequest(DialogFragment dialog) {
        String requestItem = lastSelectedDaiaItem.getId();

        ModsFragmentDirections.ActionNavModsToNavAccount action = ModsFragmentDirections.actionNavModsToNavAccount();
        action.setRequestItem(requestItem);
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onActionOrder(DialogFragment dialog) {
        String requestItem = lastSelectedDaiaItem.getId();

        ModsFragmentDirections.ActionNavModsToNavAccount action = ModsFragmentDirections.actionNavModsToNavAccount();
        action.setRequestItem(requestItem);
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void onActionOnline(DialogFragment dialog) {
        Uri uri = Uri.parse(modsItem.onlineUrl);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }

    private void onISBDSuccess(String isbd) {
        // Set extended author information
        binding.authorExtended.setText(isbd);

        // Hide UnApi loading animation
        binding.unapiProgressbar.setVisibility(View.GONE);
    }

    private void onAvailabilitySuccess(DaiaItems daiaItems) {
        // Workaround
        if (daiaAdapter == null) {
            return;
        }

        daiaAdapter.submitList(daiaItems.getItems());

        if (daiaItems.getItems().isEmpty()) {
            binding.list.empty.setVisibility(View.VISIBLE);
        }

        // distance
        requestGeoLocation();
    }

    private void onRequestFailed(@StringRes Integer errorString) {
        Snackbar.make(binding.modsLayout, errorString, Snackbar.LENGTH_LONG).show();

        binding.list.swiperefresh.setRefreshing(false);
        binding.unapiProgressbar.setVisibility(View.GONE);
    }

    private View.OnClickListener onDaiaClickListener = itemView -> {
        DaiaItem daiaItem = (DaiaItem) itemView.getTag();

        lastSelectedDaiaItem = daiaItem;

        String actions = daiaItem.getActions();

        // Determine which actions are available for this location
        ArrayList<String> actionList = new ArrayList<>();
        if (modsItem.onlineUrl.isEmpty()) {            // This is not an online resource
            if (actions.contains("no_barcode_reset")) {
                return;
            } else {
                // location | request | order
                if (actions.contains("location")) {
                    actionList.add("location");
                }
                if (actions.contains("request")) {
                    actionList.add("request");
                }
                if (actions.contains("order")) {
                    actionList.add("order");
                }
            }
        } else {
            // location
            if (actions.contains("location")) {
                actionList.add("location");
            }
            actionList.add("online");
        }

        // Open a dialog with all available actions
        DetailActionsDialogFragment dialogFragment = new DetailActionsDialogFragment();
        dialogFragment.setActionList(actionList);
        dialogFragment.show(getChildFragmentManager(), "details_actions");
    };

    private void onClickInterlanding() {
        Uri uri = Uri.parse(UrlHelper.getInterlendingUrl(requireContext(), modsItem.ppn));
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }

    private void onClickIndex() {
        if (modsItem.indexArray.size() == 1) {
            // open directly
            openIndexAsset(modsItem.indexArray.get(0));
        } else {
            // create a dialag allowing selecting of an url to open
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

            List<String> indexList = modsItem.indexArray;
            CharSequence[] indexItems = indexList.toArray(new CharSequence[indexList.size()]);

            builder.setTitle(R.string.indexactionsdialog_title)
                    .setItems(indexItems, (DialogInterface dialogInterface, int which) -> {
                        openIndexAsset(indexList.get(which));
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void openIndexAsset(String url) {
        Intent webIntent = new Intent(getActivity(), WebViewActivity.class);
        webIntent.putExtra("url", UrlHelper.getSecureUrl(url));
        requireActivity().startActivity(webIntent);
    }





//            // show the action dialog
//            mPaiaDialog = new PaiaActionDialogFragment();
//            mPaiaDialog.show(this.getChildFragmentManager(), "paia_action");

//    @Override
//    public void onActionDialogPositiveClick(DialogFragment dialog) {
//        // Close dialog
//        mPaiaDialog.dismiss();
//    }

    private void requestGeoLocation() {
        if (modsItem == null || modsItem.isLocalSearch) {
            return;
        }

        if (daiaAdapter == null) {
            return;
        }

        if (lastLocation != null) {
            return;
        }

        if (!googleApiClient.isConnected()) {
            return;
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);

            if (lastLocation != null) {
                // determine distance
                for(DaiaItem daiaItem: daiaAdapter.getCurrentList()) {
                    if (daiaItem.hasLocation()) {
                        LocationItem locationItem = daiaItem.getLocation();

                        if (locationItem.hasPosition()) {
                            // Determine distance and update item in adapter
                            double itemLat = Double.parseDouble(locationItem.getLat());
                            double itemLong = Double.parseDouble(locationItem.getLong());

                            LatLng itemLocation = new LatLng(itemLat, itemLong);
                            LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            double itemDistance = SphericalUtil.computeDistanceBetween(itemLocation, currentLocation) / 1000;
                            daiaItem.setDistance(itemDistance);
                        }
                    }
                }

                // Sort by distance
                daiaAdapter.sortByDistance();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        requestGeoLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}