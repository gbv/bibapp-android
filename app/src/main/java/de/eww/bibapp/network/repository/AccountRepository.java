package de.eww.bibapp.network.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.PaiaService;
import de.eww.bibapp.network.Result;
import de.eww.bibapp.network.model.LoggedInUser;
import de.eww.bibapp.network.model.paia.PaiaFees;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.network.model.paia.PaiaLogin;
import de.eww.bibapp.network.model.paia.PaiaLogout;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import de.eww.bibapp.util.UrlHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class AccountRepository {

    private static volatile AccountRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    private List<PaiaLogin.SCOPES> scopes;

    private CompositeDisposable disposable = new CompositeDisposable();

    private Context context;

    // private constructor : singleton access
    private AccountRepository(Context context) {
        this.context = context;
    }

    public static AccountRepository getInstance(Context context) {
        if (instance == null) {
            instance = new AccountRepository(context);
        }

        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout(final RepositoryCallback<PaiaLogout> callback) {
        PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
        disposable.add(service
                .logout(user.getUsername(), user.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<>() {
                    @Override
                    public void onSuccess(@NonNull PaiaLogout paiaLogout) {
                        user = null;
                        scopes.clear();
                        Result<PaiaLogout> successResult = new Result.Success<>(paiaLogout);
                        callback.onComplete(successResult);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<PaiaLogout> errorResult = new Result.Error(new IOException("Error logging out", e));
                        callback.onComplete(errorResult);
                    }
                })
        );
    }

    public void login(String username, String password, final RepositoryCallback<LoggedInUser> callback) {
        PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
        disposable.add(service
                .login(username, password, "password")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<PaiaLogin>() {
                    @Override
                    public void onSuccess(@NonNull PaiaLogin paiaLogin) {
                        scopes = paiaLogin.getScopes();
                        LoggedInUser user = new LoggedInUser(paiaLogin.getPatron(), paiaLogin.getAccessToken(), 0);
                        setLoggedInUser(user);
                        Result<LoggedInUser> successResult = new Result.Success<>(user);
                        callback.onComplete(successResult);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<LoggedInUser> errorResult = new Result.Error(new IOException("Error logging in", e));
                        callback.onComplete(errorResult);
                    }
                })
        );
    }

    public void loadPatron(final RepositoryCallback<PaiaPatron> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.READ_PATRON)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .patron(user.getUsername(), user.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaPatron>() {
                            @Override
                            public void onSuccess(@NonNull PaiaPatron paiaPatron) {
                                Result<PaiaPatron> successResult = new Result.Success<>(paiaPatron);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaPatron> errorResult = new Result.Error(new IOException("Error requesting patron information", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaPatron> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    public void loadItems(final RepositoryCallback<PaiaItems> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.READ_ITEMS)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .borrowed(user.getUsername(), user.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaItems>() {
                            @Override
                            public void onSuccess(@NonNull PaiaItems paiaItems) {
                                Result<PaiaItems> successResult = new Result.Success<>(paiaItems);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaItems> errorResult = new Result.Error(new IOException("Error requesting borrowed items", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaItems> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    public void loadFees(final RepositoryCallback<PaiaFees> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.READ_ITEMS)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .fees(user.getUsername(), user.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaFees>() {
                            @Override
                            public void onSuccess(@NonNull PaiaFees paiaItems) {
                                Result<PaiaFees> successResult = new Result.Success<>(paiaItems);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaFees> errorResult = new Result.Error(new IOException("Error requesting fee items", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaFees> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    public void requestRenew(final PaiaItems items, final RepositoryCallback<PaiaItems> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.WRITE_ITEMS)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .renew(user.getUsername(), user.getToken(), items)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaItems>() {
                            @Override
                            public void onSuccess(@NonNull PaiaItems paiaItems) {
                                Result<PaiaItems> successResult = new Result.Success<>(paiaItems);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaItems> errorResult = new Result.Error(new IOException("Error renewing borrowed items", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaItems> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    public void requestCancel(final PaiaItems items, final RepositoryCallback<PaiaItems> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.WRITE_ITEMS)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .cancel(user.getUsername(), user.getToken(), items)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaItems>() {
                            @Override
                            public void onSuccess(@NonNull PaiaItems paiaItems) {
                                Result<PaiaItems> successResult = new Result.Success<>(paiaItems);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaItems> errorResult = new Result.Error(new IOException("Error canceling booked items", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaItems> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    public void requestRequest(final PaiaItems items, final RepositoryCallback<PaiaItems> callback) {
        if (user != null) {
            if (scopes.contains(PaiaLogin.SCOPES.WRITE_ITEMS)) {
                PaiaService service = ApiClient.getClient(context, HttpUrl.parse(UrlHelper.getPaiaUrl(context))).create(PaiaService.class);
                disposable.add(service
                        .request(user.getUsername(), user.getToken(), items)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PaiaItems>() {
                            @Override
                            public void onSuccess(@NonNull PaiaItems paiaItems) {
                                Result<PaiaItems> successResult = new Result.Success<>(paiaItems);
                                callback.onComplete(successResult);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Result<PaiaItems> errorResult = new Result.Error(new IOException("Error requesting items", e));
                                callback.onComplete(errorResult);
                            }
                        })
                );
            } else {
                Result<PaiaItems> errorResult = new Result.Error(new IOException("Insufficient rights"));
                callback.onComplete(errorResult);
            }
        }
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
