package de.eww.bibapp.ui.mods;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.ModsAdapter;
import de.eww.bibapp.databinding.FragmentSearchListBinding;
import de.eww.bibapp.listener.EndlessScrollListener;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.tasks.DBSPixelTask;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.viewmodel.ModsViewModel;
import de.eww.bibapp.viewmodel.ModsViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchListFragment extends Fragment {

    private static final String ARG_SEARCH_MODE = "searchMode";

    private FragmentSearchListBinding binding;
    private ModsViewModel viewModel;
    private ModsAdapter modsAdapter;
    private SearchManager.SEARCH_MODE searchMode;
    private String lastSearchQuery = "";

    public SearchListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchMode Parameter 1.
     * @return A new instance of fragment SearchListFragment.
     */
    public static SearchListFragment newInstance(SearchManager.SEARCH_MODE searchMode) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_MODE, searchMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchMode = (SearchManager.SEARCH_MODE) getArguments().getSerializable(ARG_SEARCH_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchListBinding.inflate(inflater, container, false);

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

        // Handle navigation based on having a single pane layout or a two pane layout
        View.OnClickListener onClickListener = itemView -> {
            ModsItem modsItem = (ModsItem) itemView.getTag();
            viewModel.select(modsItem);

            if (itemDetailFragmentContainer != null) {
                // two pane
                Navigation.findNavController(itemDetailFragmentContainer).navigate(R.id.search_sub_navigation);
            } else {
                // single pane
                Navigation.findNavController(itemView).navigate(R.id.action_nav_serach_to_nav_mods);
            }
        };

        RecyclerView recyclerView = binding.list.itemList;
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

        modsAdapter = new ModsAdapter(requireContext(), onClickListener);
        recyclerView.setAdapter(modsAdapter);

        recyclerView.addOnScrollListener(onScrollListener);

        viewModel.getSearchResult(searchMode).observe(getViewLifecycleOwner(), searchResult -> {
            binding.list.empty.setVisibility(View.GONE);
            binding.list.swiperefresh.setRefreshing(false);

            if (searchResult.getError() != null) {
                onSearchFailed(searchResult.getError());
            }
            if (searchResult.getSuccess() != null) {
                onSearchSuccess(searchResult.getSuccess());
            }
        });

        viewModel.getForceGvkSearch().observe(getViewLifecycleOwner(), searchQuery -> {
            if (this.searchMode == SearchManager.SEARCH_MODE.GVK) {
                forceSearch(searchQuery);
            }
        });

        // Set up search view
        binding.searchQuery.setIconifiedByDefault(false);
        binding.searchQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.list.swiperefresh.setRefreshing(true);
                binding.list.empty.setVisibility(View.GONE);

                lastSearchQuery = query;

                performSearch(1);

                // Force soft keyboard to hide
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchQuery.getWindowToken(), 0);

                binding.searchQuery.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.list.swiperefresh.setOnRefreshListener(() -> forceSearch(lastSearchQuery));
    }

    private RecyclerView.OnScrollListener onScrollListener = new EndlessScrollListener() {
        @Override
        public void onLoadMore() {
            binding.list.swiperefresh.setRefreshing(true);
            performSearch(modsAdapter.getItemCount() + 1);
        }
    };

    private void performSearch(int offset)
    {
        viewModel.setSearchQuery(lastSearchQuery);
        viewModel.setSearchOffset(offset);
        viewModel.setSearchMode(searchMode);

        if (offset == 1) {
            modsAdapter.submitList(null);
            ((EndlessScrollListener) onScrollListener).reset();
        }

        viewModel.loadSearchResults();
    }

    private void onSearchFailed(@StringRes Integer errorString) {
        Snackbar.make(binding.list.swiperefresh, errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onSearchSuccess(SruResult sruResult) {
        List<ModsItem> newList = new ArrayList<>();
        newList.addAll(modsAdapter.getCurrentList());
        newList.addAll(sruResult.getItems());
        modsAdapter.submitList(newList);

        updateSubtitle(sruResult.getNumberOfRecords());

        // dbs counting
        if (!getResources().getString(R.string.bibapp_tracking_url).isEmpty()) {
            boolean isDbsChecked = PrefUtils.isDbsChecked(getActivity());

            if (isDbsChecked) {
                AsyncTask<Void, Void, Void> dbsPixelTask = new DBSPixelTask(getActivity());
                dbsPixelTask.execute();
            }
        }

        if (sruResult.getItems().isEmpty()) {
            binding.list.empty.setVisibility(View.VISIBLE);

            /*
             * If this is a local catalog search and we could not find any results
             * suggest to use the global gvk search
             */
            if (searchMode.equals(SearchManager.SEARCH_MODE.LOCAL)) {
                if (modsAdapter.getItemCount() == 0) {
                    Snackbar
                            .make(binding.list.itemList, R.string.search_no_results, Snackbar.LENGTH_LONG)
                            .setDuration(5000)
                            .setAction(R.string.search_gvk, onClickSnackbarListener)
                            .setActionTextColor(Color.WHITE)
                            .show();
                }
            }
        } else {
//            List<ModsItem> modsItems = (List<ModsItem>) sruResult.getResult().get("list");
//            ModsSource.setTotalItems(mSearchMode.toString(), (Integer) sruResult.getResult().get("numberOfRecords"));

//            if (modsAdapter.getItemCount() == 0) {
//                binding.list.empty.setVisibility(View.VISIBLE);
//                binding.list.itemList.setVisibility(View.GONE);
//            } else {
//                binding.list.itemList.setVisibility(View.VISIBLE);
//            }

//            if (mAdapter == null) {
//                ModsSource.clear(mSearchMode.toString());
//                mAdapter = new ModsAdapter(modsItems, getActivity());
//                mRecyclerView.setAdapter(mAdapter);
//            } else {
//                mAdapter.addModsItems(modsItems);
//                mAdapter.notifyDataSetChanged();
//            }
//
//            ModsSource.addModsItems(mSearchMode.toString(), modsItems);
//            if (mModsItemSelectedListener != null) {
//                mModsItemSelectedListener.onNewSearchResultsLoaded(mSearchMode);
//            }
//
//            mIsLoading = false;
        }
    }

    private View.OnClickListener onClickSnackbarListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewModel.forceGvkSearch(lastSearchQuery);
        }
    };

    private void forceSearch(String searchQuery) {
        binding.searchQuery.setQuery(searchQuery, true);
    }

    private void updateSubtitle(int totalHits) {
//        ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setSubtitle(totalHits + " " + getResources().getString(R.string.search_hits));
//        }
    }



















//
//    public void setSelection(int position) {
//        mRecyclerView.scrollToPosition(position);
//    }
//
//    public void resetAdapter() {
//        ArrayList<ModsItem> modsItemList = new ArrayList<>();
//        modsItemList.addAll(ModsSource.getModsItems(mSearchMode.toString()));
//
//        mAdapter = new ModsAdapter(modsItemList, getActivity());
//        mRecyclerView.setAdapter(mAdapter);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        if (this.searchManager == null) {
//            this.searchManager = new SearchManager();
//
//            // Reset source
//            ModsSource.clear(mSearchMode.toString());
//            ModsSource.setTotalItems(mSearchMode.toString(), 0);
//            updateSubtitle();
//
//            this.searchManager.setSearchMode(this.mSearchMode);
//        }
//
//        RecyclerViewOnGestureListener gestureListener = new RecyclerViewOnGestureListener(getActivity(), mRecyclerView);
//        gestureListener.setOnGestureListener(this);
//        mRecyclerView.addOnItemTouchListener(gestureListener);
//    }
//
//    @Override
//    public void setMenuVisibility(final boolean visible) {
//        super.setMenuVisibility(visible);
//
//        if (visible) {
//            updateSubtitle();
//        }
//    }
}