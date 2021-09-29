package de.eww.bibapp.ui.mods;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.WatchlistAdapter;
import de.eww.bibapp.databinding.FragmentWatchlistBinding;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.viewmodel.ModsViewModel;
import de.eww.bibapp.viewmodel.ModsViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class WatchlistFragment extends Fragment {

    private FragmentWatchlistBinding binding;
    private ModsViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private ActionMode actionMode;
    private SelectionTracker<String> selectionTracker;
    private MenuItem exportMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false);

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

        // Not using view binding here because it relies on if the view is visible in the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        View.OnClickListener onClickListener = itemView -> {
            ModsItem modsItems = (ModsItem) itemView.getTag();
            viewModel.select(modsItems);

            if (itemDetailFragmentContainer != null) {
                // two pane
                Navigation.findNavController(itemDetailFragmentContainer).navigate(R.id.watchlist_sub_navigation);
            } else {
                // single pane
                Navigation.findNavController(itemView).navigate(R.id.action_nav_watchlist_to_nav_mods);
            }
        };

        RecyclerView recyclerView = binding.list.itemList;
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        watchlistAdapter = new WatchlistAdapter(requireContext(), onClickListener);
        recyclerView.setAdapter(watchlistAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "watchlist_selection",
                recyclerView,
                new WatchlistItemKeyProvider(watchlistAdapter),
                new WatchlistItemDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();
        selectionTracker.onRestoreInstanceState(savedInstanceState);
        watchlistAdapter.setSelectionTracker(selectionTracker);

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();

                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) requireActivity()).startSupportActionMode(actionModeCallback);
                }

                actionMode.setTitle(getString(R.string.menu_selected_count, selectionTracker.getSelection().size()));
            }
        });

        viewModel.getExportResult().observe(getViewLifecycleOwner(), exportResult -> {
            if (exportResult.getError() != null) {
                onExportFailed(exportResult.getError());
            }
            if (exportResult.getSuccess() != null) {
                onExportSuccess(exportResult.getSuccess());
            }
        });

        viewModel.getWatchlistResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                binding.list.swiperefresh.setRefreshing(true);
                return;
            }

            binding.list.empty.setVisibility(View.GONE);
            binding.list.swiperefresh.setRefreshing(false);

            if (result.getError() != null) {
                onWatchlistFailed(result.getError());
            }
            if (result.getSuccess() != null) {
                onWatchlistSuccess(result.getSuccess());
            }
        });

        if (viewModel.getWatchlistResult().getValue() == null) {
            viewModel.loadWatchlist();
        }

        binding.list.swiperefresh.setOnRefreshListener(() -> viewModel.loadWatchlist());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.watchlist_fragment_actions, menu);

        exportMenuItem = menu.findItem(R.id.menu_export_from_watchlist);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // enable export button, if we have some items
        exportMenuItem.setVisible(watchlistAdapter.getItemCount() > 0);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export_from_watchlist:

                viewModel.export(watchlistAdapter.getCurrentList());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectionTracker != null) {
            selectionTracker.onSaveInstanceState(outState);
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.watchlist_fragment_mode_actions, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_remove_from_watchlist:
                    viewModel.removeFromWatchlist(watchlistAdapter.getSelectedItems());

                    // Display Snackbar
                    Snackbar.make(requireActivity().findViewById(R.id.coordinator), R.string.toast_watchlist_removed, Snackbar.LENGTH_LONG).show();

                    // refresh menu
                    requireActivity().invalidateOptionsMenu();

                    actionMode.finish();

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selectionTracker.clearSelection();
            WatchlistFragment.this.actionMode = null;
        }
    };

    private void onWatchlistFailed(@StringRes Integer errorString) {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onWatchlistSuccess(List<ModsItem> modsItems) {
        watchlistAdapter.submitList(modsItems);

        if (modsItems.isEmpty()) {
            binding.list.empty.setVisibility(View.VISIBLE);
        }
    }

    private void onExportFailed(@StringRes Integer errorString) {
        Snackbar.make(binding.list.swiperefresh, errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onExportSuccess(String export) {
            // create an intent to send an email with a list of watchlist items
            Intent sendIntent = new Intent(Intent.ACTION_SEND);

            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.watchlist_export_subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, export);

            PackageManager packageManager = getActivity().getPackageManager();
            List activities = packageManager.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (activities.size() > 0) {
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.watchlist_export_send_to)));
            }
    }
}