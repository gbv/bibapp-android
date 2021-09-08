package de.eww.bibapp.ui.info;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import de.eww.bibapp.R;
import de.eww.bibapp.databinding.FragmentImpressumBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImpressumFragment extends Fragment {

    private FragmentImpressumBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImpressumBinding.inflate(inflater, container, false);
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

        // needs to be called before setText
        binding.impressum.setMovementMethod(LinkMovementMethod.getInstance());

        binding.impressum.setText(Html.fromHtml(getResources().getString(R.string.impressum_text)));

        // Linkify
        Linkify.addLinks(binding.impressum, Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
    }
}