package de.eww.bibapp.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.FeeAdapter;
import de.eww.bibapp.databinding.FragmentAccountFeesBinding;
import de.eww.bibapp.network.model.paia.PaiaFees;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

public class AccountFeesFragment extends Fragment {

    private FragmentAccountFeesBinding binding;
    private AccountViewModel accountViewModel;
    private FeeAdapter feesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountFeesBinding.inflate(inflater, container, false);

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

        feesAdapter = new FeeAdapter();
        recyclerView.setAdapter(feesAdapter);

        accountViewModel.getFeesResult().observe(getViewLifecycleOwner(), feesResult -> {
            if (feesResult == null) {
                binding.list.swiperefresh.setRefreshing(true);
                return;
            }

            binding.list.empty.setVisibility(View.GONE);
            binding.list.swiperefresh.setRefreshing(false);

            if (feesResult.getError() != null) {
                onFeesFailed(feesResult.getError());
            }
            if (feesResult.getSuccess() != null) {
                onFeesSuccess(feesResult.getSuccess());
            }
        });

        binding.list.swiperefresh.setOnRefreshListener(() -> accountViewModel.loadFees());
    }

    private void onFeesFailed(@StringRes Integer errorString) {
        Snackbar.make(binding.list.swiperefresh, errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onFeesSuccess(PaiaFees paiaFees) {
        feesAdapter.submitList(paiaFees.getFees());

        if (paiaFees.getFees().isEmpty()) {
            binding.list.empty.setVisibility(View.VISIBLE);
        } else {
            binding.sum.setText(getResources().getString(R.string.account_fees_amount) + " " + paiaFees.getAmount());
        }
    }
}
