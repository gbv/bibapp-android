package de.eww.bibapp.ui.mods;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.eww.bibapp.MainActivity;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.SearchListPagerAdapter;
import de.eww.bibapp.databinding.FragmentSearchBinding;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.viewmodel.ModsViewModel;
import de.eww.bibapp.viewmodel.ModsViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

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

        ModsViewModel viewModel = new ViewModelProvider(requireActivity(), new ModsViewModelFactory(requireActivity().getApplication())).get(ModsViewModel.class);
        
        viewModel.getForceGvkSearch().observe(getViewLifecycleOwner(), searchQuery -> binding.pager.setCurrentItem(1, true));

        SearchListPagerAdapter searchListPagerAdapter = new SearchListPagerAdapter(this);
        binding.pager.setAdapter(searchListPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
            String[] searchCatalogs = getResources().getStringArray(R.array.search_catalogs);

            // If our current local catalog contains a short title, we append it to the default title
            int localCatalogIndex = PrefUtils.getLocalCatalogIndex(requireContext());
            String[] localCatalogSuffixes = getResources().getStringArray(R.array.bibapp_local_catalog_suffixes);
            if (localCatalogSuffixes.length > localCatalogIndex + 1) {
                searchCatalogs[0] += " " + localCatalogSuffixes[localCatalogIndex + 1];
            }

            tab.setText(searchCatalogs[position]);
        }).attach();
    }
}