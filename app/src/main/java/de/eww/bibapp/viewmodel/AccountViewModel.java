package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.Result;
import de.eww.bibapp.network.model.LoggedInUser;
import de.eww.bibapp.network.model.paia.PaiaFees;
import de.eww.bibapp.network.model.paia.PaiaItem;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.network.model.paia.PaiaLogout;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import de.eww.bibapp.network.repository.AccountRepository;
import de.eww.bibapp.ui.account.LoginFormState;
import de.eww.bibapp.util.PrefUtils;

public class AccountViewModel extends AndroidViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult;
    private MutableLiveData<LogoutResult> logoutResult;
    private MutableLiveData<PatronResult> patronResult;
    private MutableLiveData<ItemsResult> itemsResult;
    private MutableLiveData<FeesResult> feesResult;
    private MutableLiveData<RenewResult> renewResult = new MutableLiveData<>();
    private MutableLiveData<CancelResult> cancelResult = new MutableLiveData<>();
    private MutableLiveData<RequestResult> requestResult = new MutableLiveData<>();

    private final AccountRepository accountRepository;

    public AccountViewModel(Application application, AccountRepository accountRepository) {
        super(application);

        this.accountRepository = accountRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        if (loginResult == null) {
            loginResult = new MutableLiveData<>();
            loginResult.setValue(null);
        }
        return loginResult;
    }

    public LiveData<LogoutResult> getLogoutResult() {
        if (logoutResult == null) {
            logoutResult = new MutableLiveData<>();
        }
        return logoutResult;
    }

    public LiveData<PatronResult> getPatronResult() {
        if (patronResult == null) {
            patronResult = new MutableLiveData<>();
        }
        return patronResult;
    }

    public LiveData<ItemsResult> getItemsResult() {
        if (itemsResult == null) {
            itemsResult = new MutableLiveData<>();
            itemsResult.setValue(null);
            loadItems();
        }
        return itemsResult;
    }

    public LiveData<FeesResult> getFeesResult() {
        if (feesResult == null) {
            feesResult = new MutableLiveData<>();
            feesResult.setValue(null);
            loadFees();
        }
        return feesResult;
    }

    public LiveData<RenewResult> getRenewResult() {
        return renewResult;
    }

    public LiveData<CancelResult> getCancelResult() {
        return cancelResult;
    }

    public LiveData<RequestResult> getRequestResult() {
        return requestResult;
    }

    public boolean isLoginStored() {
        return PrefUtils.isLoginStored(getApplication());
    }

    public void login(String username, String password) {
        accountRepository.login(username, password, result -> {
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                loginResult.setValue(new LoginResult(new LoggedInUser(data.getUsername(), data.getToken(), data.getStatus())));
            } else {
                loginResult.setValue(new LoginResult(R.string.logindialog_wrong));
                PrefUtils.setLoginStored(getApplication(), false);
            }
        });
    }

    public void logout() {
        if (accountRepository.isLoggedIn()) {
            accountRepository.logout(result -> {
                if (result instanceof Result.Success) {
                    PrefUtils.unsetStoredCredentials(getApplication());
                    PaiaLogout data = ((Result.Success<PaiaLogout>) result).getData();
                    logoutResult.setValue(new LogoutResult(data));
                } else {
                    logoutResult.setValue(new LogoutResult(R.string.paiadialog_general_failure));
                }
            });
        }
    }

    public void getPetron() {
        if (accountRepository.isLoggedIn()) {
            accountRepository.loadPatron(result -> {
                if (result instanceof Result.Success) {
                    PaiaPatron data = ((Result.Success<PaiaPatron>) result).getData();
                    patronResult.setValue(new PatronResult(data));
                } else {
                    patronResult.setValue(new PatronResult(R.string.toast_account_error));
                }
            });
        }
    }

    public void loadItems() {
        if (accountRepository.isLoggedIn()) {
            accountRepository.loadItems(result -> {
                if (result instanceof Result.Success) {
                    PaiaItems data = ((Result.Success<PaiaItems>) result).getData();
                    itemsResult.setValue(new ItemsResult(data));
                } else {
                    itemsResult.setValue(new ItemsResult(R.string.toast_account_error));
                }
            });
        }
    }

    public void loadFees() {
        if (accountRepository.isLoggedIn()) {
            accountRepository.loadFees(result -> {
                if (result instanceof Result.Success) {
                    PaiaFees data = ((Result.Success<PaiaFees>) result).getData();
                    feesResult.setValue(new FeesResult(data));
                } else {
                    feesResult.setValue(new FeesResult(R.string.toast_account_error));
                }
            });
        }
    }

    public void requestRenew(List<PaiaItem> items) {
        if (accountRepository.isLoggedIn()) {
            accountRepository.requestRenew(new PaiaItems(items), result -> {
                if (result instanceof Result.Success) {
                    PaiaItems data = ((Result.Success<PaiaItems>) result).getData();
                    renewResult.setValue(new RenewResult(data));
                } else {
                    renewResult.setValue(new RenewResult(R.string.paiadialog_general_failure));
                }
            });
        }
    }

    public void requestCancel(List<PaiaItem> items) {
        if (accountRepository.isLoggedIn()) {
            accountRepository.requestCancel(new PaiaItems(items), result -> {
                if (result instanceof Result.Success) {
                    PaiaItems data = ((Result.Success<PaiaItems>) result).getData();
                    cancelResult.setValue(new CancelResult(data));
                } else {
                    cancelResult.setValue(new CancelResult(R.string.paiadialog_general_failure));
                }
            });
        }
    }

    public void requestRequest(List<PaiaItem> items) {
        if (accountRepository.isLoggedIn()) {
            accountRepository.requestRequest(new PaiaItems(items), result -> {
                if (result instanceof Result.Success) {
                    PaiaItems data = ((Result.Success<PaiaItems>) result).getData();
                    requestResult.setValue(new RequestResult(data));
                } else {
                    requestResult.setValue(new RequestResult(R.string.paiadialog_general_failure));
                }
            });
        }
    }

    public void autoLogin() {
        String username = PrefUtils.getStoredUsername(getApplication());
        String password = PrefUtils.getStoredPassword(getApplication());

        login(username, password);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUsernameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.logindialog_wrong, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.logindialog_wrong));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }

        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && !password.trim().isEmpty();
    }
}
