package de.eww.bibapp.ui.account;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.MainActivity;
import de.eww.bibapp.R;
import de.eww.bibapp.adapter.AccountPagerAdapter;
import de.eww.bibapp.databinding.FragmentAccountBinding;
import de.eww.bibapp.network.model.paia.PaiaItem;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.network.model.paia.PaiaLogout;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import de.eww.bibapp.typeface.BeluginoFont;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    private AccountPagerAdapter accountPagerAdapter;

    private AccountViewModel accountViewModel;

    private MenuItem idMenuItem;

    private PaiaPatron patron;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

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

        accountViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                this.onLoggedIn();
            } else {
                NavHostFragment.findNavController(this).navigate(R.id.nav_login);
            }
        });

        accountViewModel.getPatronResult().observe(getViewLifecycleOwner(), patronResult -> {
            if (patronResult == null) {
                return;
            }

            patron = patronResult.getSuccess();

            ActionBar actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setSubtitle(patron.getName());
            }

            if (patron.getStatus() > 0) {
                if (actionBar != null) {
                    String currentTitle = actionBar.getTitle().toString();
                    actionBar.setTitle(currentTitle + " " + getResources().getText(R.string.account_inactive));
                }
            } else {
                if (idMenuItem != null) {
                    idMenuItem.setVisible(true);
                }
            }
        });

        accountViewModel.getLogoutResult().observe(getViewLifecycleOwner(), logoutResult -> {
            if (logoutResult == null) {
                return;
            }

            if (logoutResult.getError() != null) {
                onLogoutFailed(logoutResult.getError());
            }
            if (logoutResult.getSuccess() != null) {
                onLogoutSuccess(logoutResult.getSuccess());
            }
        });

        // Not using view binding here because it relies on if the view is visible in the current
        // layout configuration (layout, layout-sw600dp)
        View pager = view.findViewById(R.id.pager);

        if (pager != null) {
            accountPagerAdapter = new AccountPagerAdapter(this);
            binding.pager.setAdapter(accountPagerAdapter);

            new TabLayoutMediator(binding.tabLayout, binding.pager, (tab, position) -> {
                switch (position) {
                    case 2:
                        tab.setText(getResources().getString(R.string.account_fees));
                        break;
                    case 1:
                        tab.setText(getResources().getString(R.string.account_booked));
                        break;
                    default:
                        tab.setText(getResources().getString(R.string.account_borrowed));
                }
            }).attach();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.account_fragment_actions, menu);

        // id
        idMenuItem = menu.findItem(R.id.menu_account_account_id);
        idMenuItem.setIcon(new IconicsDrawable(requireContext())
                .icon(BeluginoFont.Icon.bel_idcard)
                .color(Color.WHITE)
                .sizeDp(24));

        // logout
        MenuItem logoutItem = menu.findItem(R.id.menu_account_account_logout);
        logoutItem.setIcon(new IconicsDrawable(requireContext())
                .icon(BeluginoFont.Icon.bel_logout)
                .color(Color.WHITE)
                .sizeDp(24));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (patron != null) {
            if (patron.getStatus() == 0) {
                menu.findItem(R.id.menu_account_account_id).setVisible(true);
            }
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_account_account_logout) {
            accountViewModel.logout();
        } else if (id == R.id.menu_account_account_id) {
            NavHostFragment.findNavController(this).navigate(R.id.action_nav_account_to_nav_id);
        }

        return super.onOptionsItemSelected(item);
    }

    private void onLoggedIn() {
        accountViewModel.getPetron();

        // Check for pending requests
        String requestItem = AccountFragmentArgs.fromBundle(getArguments()).getRequestItem();
        if (requestItem != null) {
            accountViewModel.getRequestResult().observe(getViewLifecycleOwner(), requestResult -> {
                onRequestSuccess(requestResult.getSuccess());
            });
            PaiaItem paiaItem = new PaiaItem();
            paiaItem.setItem(requestItem);
            List<PaiaItem> paiaItems = new ArrayList<>();
            paiaItems.add(paiaItem);
            accountViewModel.requestRequest(paiaItems);
        }
    }

    private void onLogoutFailed(@StringRes Integer errorString) {
        Snackbar.make(requireActivity().findViewById(R.id.coordinator), errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onLogoutSuccess(PaiaLogout logout) {
//        NavHostFragment.findNavController(this).navigate(R.id.action_nav_account_to_nav_login);
    }

    private void onRequestSuccess(PaiaItems paiaItems) {
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
                successText = resources.getString(R.string.paiadialog_general_success);
            } else {
                if (paiaItems.getItems().size() == numFailedItems) {
                    successText = resources.getString(R.string.paiadialog_general_failure);
                } else {
                    successText = resources.getString(R.string.paiadialog_general_failure);
                }

                successText += "\n";
                successText += failedResponseText;
            }
        }

        Snackbar.make(requireActivity().findViewById(R.id.coordinator), successText, Snackbar.LENGTH_LONG).show();

//        mPaiaDialog.paiaActionDone(responseText);
//        accountViewModel.loadItems();
    }
}