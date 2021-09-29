package de.eww.bibapp.ui.account;

import android.content.res.Resources;
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
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.BookedAdapter;
import de.eww.bibapp.databinding.FragmentAccountBookedBinding;
import de.eww.bibapp.network.model.paia.PaiaItem;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

public class AccountBookedFragment extends Fragment {

    private FragmentAccountBookedBinding binding;
    private AccountViewModel accountViewModel;
    private BookedAdapter bookedAdapter;
    private ActionMode actionMode;
    private SelectionTracker<String> selectionTracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBookedBinding.inflate(inflater, container, false);

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

        accountViewModel = new ViewModelProvider(requireActivity(), new AccountViewModelFactory(requireActivity().getApplication())).get(AccountViewModel.class);

        RecyclerView recyclerView = binding.list.itemList;
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(requireActivity(), androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

        bookedAdapter = new BookedAdapter();
        recyclerView.setAdapter(bookedAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "booked_selection",
                recyclerView,
                new BookedItemKeyProvider(bookedAdapter),
                new BookedItemDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage())
                .withSelectionPredicate(selectionPredicate)
                .build();
        selectionTracker.onRestoreInstanceState(savedInstanceState);
        bookedAdapter.setSelectionTracker(selectionTracker);

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

        accountViewModel.getItemsResult().observe(getViewLifecycleOwner(), itemsResult -> {
            if (itemsResult == null) {
                binding.list.swiperefresh.setRefreshing(true);
                return;
            }

            binding.list.empty.setVisibility(View.GONE);
            binding.list.swiperefresh.setRefreshing(false);

            if (itemsResult.getError() != null) {
                onItemsFailed(itemsResult.getError());
            }
            if (itemsResult.getSuccess() != null) {
                onItemsSuccess(itemsResult.getSuccess());
            }
        });

        accountViewModel.getCancelResult().observe(getViewLifecycleOwner(), cancelResult -> {
            if (cancelResult.getError() != null) {
                onCancelFailed(cancelResult.getError());
            }
            if (cancelResult.getSuccess() != null) {
                onCancelSuccess(cancelResult.getSuccess());
            }
        });

        binding.list.swiperefresh.setOnRefreshListener(() -> accountViewModel.loadItems());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (selectionTracker != null) {
            selectionTracker.onSaveInstanceState(outState);
        }
    }

    private SelectionTracker.SelectionPredicate<String> selectionPredicate = new SelectionTracker.SelectionPredicate<>() {
        @Override
        public boolean canSetStateForKey(@NonNull String key, boolean nextState) {
            for (PaiaItem paiaItem : bookedAdapter.getCurrentList()) {
                if (paiaItem.getItem().equals(key)) {
                    return paiaItem.isCanCancel();
                }
            }

            return true;
        }

        @Override
        public boolean canSetStateAtPosition(int position, boolean nextState) {
            return true;
        }

        @Override
        public boolean canSelectMultiple() {
            return true;
        }
    };

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.account_booked_fragment_mode_actions, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_account_booked_cancel:
                    accountViewModel.requestCancel(bookedAdapter.getSelectedItems());

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            selectionTracker.clearSelection();
            AccountBookedFragment.this.actionMode = null;
        }
    };

    private void onItemsFailed(@StringRes Integer errorString) {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onItemsSuccess(PaiaItems paiaItems) {
        bookedAdapter.submitList(paiaItems.getBooked());

        if (paiaItems.getBooked().isEmpty()) {
            binding.list.empty.setVisibility(View.VISIBLE);
        }
    }

    private void onCancelFailed(@StringRes Integer errorString) {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onCancelSuccess(PaiaItems paiaItems) {
        String successText = "";

        Resources resources = getResources();

        if (!paiaItems.getItems().isEmpty()) {
            StringBuilder failedResponseText = new StringBuilder();
            int numFailedItems = 0;

            for (PaiaItem item : paiaItems.getItems()) {
                if (item.getError() != null) {
                    failedResponseText.append("\n");
                    failedResponseText.append(item.getError());

                    numFailedItems++;
                }
            }

            if (numFailedItems == 0) {
                successText = resources.getQuantityString(R.plurals.paiadialog_cancel_success, paiaItems.getItems().size());
            } else {
                if (paiaItems.getItems().size() == numFailedItems) {
                    successText = resources.getString(R.string.paiadialog_cancel_failure);
                } else {
                    successText = resources.getString(R.string.paiadialog_cancel_partial);
                }

                successText += "\n";
                successText += failedResponseText;
            }
        }

        Snackbar.make(requireActivity().findViewById(R.id.coordinator), successText, Snackbar.LENGTH_LONG).show();

        actionMode.finish();
        accountViewModel.loadItems();
    }
}
