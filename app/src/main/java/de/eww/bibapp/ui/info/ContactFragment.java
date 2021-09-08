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
import de.eww.bibapp.databinding.FragmentContactBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment {

    private FragmentContactBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater, container, false);
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
        binding.contact.setMovementMethod(LinkMovementMethod.getInstance());

        binding.contact.setText(Html.fromHtml(getResources().getString(R.string.contact_text)));

        // Linkify
        Linkify.addLinks(binding.contact, Linkify.EMAIL_ADDRESSES | Linkify.PHONE_NUMBERS);
    }
}