package de.eww.bibapp.ui.info;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.eww.bibapp.R;
import de.eww.bibapp.databinding.FragmentInfoBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    FragmentInfoBinding binding;

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
    }
}