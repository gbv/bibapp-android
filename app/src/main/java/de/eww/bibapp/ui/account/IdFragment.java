package de.eww.bibapp.ui.account;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.eww.bibapp.databinding.FragmentIdBinding;
import de.eww.bibapp.network.model.LoggedInUser;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class IdFragment extends Fragment {

    private FragmentIdBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentIdBinding.inflate(inflater, container, false);

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

        AccountViewModel accountViewModel = new ViewModelProvider(requireActivity(), new AccountViewModelFactory(requireActivity().getApplication())).get(AccountViewModel.class);

        accountViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result == null) {
                return;
            }

            LoggedInUser user = result.getSuccess();

            try {
                createBarcode(user.getUsername());
            } catch (WriterException e) {
                e.printStackTrace();
            }

            binding.username.setText(user.getUsername());
        });

        accountViewModel.getPatronResult().observe(getViewLifecycleOwner(), patronResult -> {
            if (patronResult == null) {
                return;
            }

            PaiaPatron patron = patronResult.getSuccess();

            binding.name.setText(patron.getName());
            binding.email.setText(patron.getEmail());
        });
    }

    private void createBarcode(String data) throws WriterException {
        final int height = 300;
        final int width = 1200;

        MultiFormatWriter barcodeWriter = new MultiFormatWriter();
        BitMatrix barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_39, width, height);
        Bitmap barcodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        binding.barcode.setImageBitmap(barcodeBitmap);
    }
}