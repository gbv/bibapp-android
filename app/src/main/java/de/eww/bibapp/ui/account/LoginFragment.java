package de.eww.bibapp.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import de.eww.bibapp.MainActivity;
import de.eww.bibapp.databinding.FragmentLoginBinding;
import de.eww.bibapp.network.model.LoggedInUser;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.viewmodel.AccountViewModel;
import de.eww.bibapp.viewmodel.AccountViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

    private FragmentLoginBinding binding;

    private AccountViewModel accountViewModel;

    private SavedStateHandle savedStateHandle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

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

        savedStateHandle = NavHostFragment.findNavController(this)
                .getPreviousBackStackEntry()
                .getSavedStateHandle();
        savedStateHandle.set(LOGIN_SUCCESSFUL, false);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final CheckBox storeCheckBox = binding.store;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        accountViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }

            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        accountViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }

            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                onLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                onLoginSuccess(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                accountViewModel.loginDataChanged(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                accountViewModel.login(
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }

            return false;
        });

        storeCheckBox.setVisibility(PrefUtils.isLoginStored(requireContext()) ? View.GONE : View.VISIBLE);

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);

            accountViewModel.login(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        if (accountViewModel.getLoginResult().getValue() == null && accountViewModel.isLoginStored()) {
            accountViewModel.autoLogin();
        }
    }

    private void onLoginFailed(@StringRes Integer errorString) {
        Snackbar.make(binding.login, errorString, Snackbar.LENGTH_LONG).show();
    }

    private void onLoginSuccess(LoggedInUser user) {
        if (binding.store.isChecked()) {
            PrefUtils.setLoginStored(requireContext(), true);
            PrefUtils.setStoredUsername(requireContext(), binding.username.getText().toString());
            PrefUtils.setStoredPassword(requireContext(), binding.password.getText().toString());
        }

        savedStateHandle.set(LOGIN_SUCCESSFUL, true);

        NavHostFragment.findNavController(this).popBackStack();
    }
}