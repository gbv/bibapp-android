package de.eww.bibapp.ui.info;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import de.eww.bibapp.R;
import de.eww.bibapp.adapter.RssAdapter;
import de.eww.bibapp.databinding.FragmentInfoBinding;
import de.eww.bibapp.network.model.RssFeed;
import de.eww.bibapp.network.model.StatefullData;
import de.eww.bibapp.viewmodel.RssViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    FragmentInfoBinding binding;

    private RssViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);

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

        Resources resources = this.getResources();

        // contact navigation
        binding.infoButtonContact.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_navigation_info_to_navigation_contact));

        // impressum navigation
        binding.infoButtonImpressum.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_info_to_nav_impressum));

        // locations navigation
        binding.infoButtonLocations.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_info_to_nav_locations));

        // privacy url
        if (resources.getString(R.string.bibapp_dataprivacy_url).isEmpty()) {
            binding.infoButtonDataprivacy.setVisibility(View.GONE);
        } else {
            binding.infoButtonDataprivacy.setOnClickListener(v -> {
                Uri uri = Uri.parse(resources.getString(R.string.bibapp_dataprivacy_url));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(launchBrowser);
            });
        }

        // Do we have a rss feed to display?
        String rssUrl = getResources().getString(R.string.bibapp_rss_url);
        if (!rssUrl.isEmpty()) {
            RecyclerView recyclerView = binding.itemList;
            recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));

            RssAdapter rssAdapter = new RssAdapter();
            recyclerView.setAdapter(rssAdapter);

            viewModel = new ViewModelProvider(requireActivity()).get(RssViewModel.class);
            LiveData<StatefullData<RssFeed>> liveData = viewModel.getFeed();
            if (liveData.getValue() == null) {
                binding.swiperefresh.setRefreshing(true);
            }
            liveData.observe(getViewLifecycleOwner(), rssFeed -> {
                binding.empty.setVisibility(View.GONE);
                binding.swiperefresh.setRefreshing(false);

                if (rssFeed.getError()) {
                    Snackbar.make(binding.swiperefresh, R.string.toast_info_rss_error, Snackbar.LENGTH_LONG).show();
                } else {
                    if (rssFeed.getData().getItems().isEmpty()) {
                        binding.empty.setVisibility(View.VISIBLE);
                    } else {
                        rssAdapter.setRssFeed(rssFeed.getData());
                    }
                }
            });

            binding.swiperefresh.setOnRefreshListener(() -> viewModel.refreshFeed());
        } else {
            binding.itemList.setVisibility(View.GONE);
        }
    }
}