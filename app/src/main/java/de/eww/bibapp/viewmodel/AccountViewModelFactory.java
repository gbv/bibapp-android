package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import de.eww.bibapp.network.repository.AccountRepository;

public class AccountViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link androidx.lifecycle.AndroidViewModel}
     */
    public AccountViewModelFactory(@NonNull Application application) {
        super(application);

        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            return (T) new AccountViewModel(application, AccountRepository.getInstance(application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
