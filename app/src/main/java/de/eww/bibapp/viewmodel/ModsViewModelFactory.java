package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import de.eww.bibapp.network.repository.ModsRepository;

public class ModsViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;

    /**
     * Creates a {@code ModsViewModelFactory}
     *
     * @param application an application to pass in {@link androidx.lifecycle.AndroidViewModel}
     */
    public ModsViewModelFactory(@NonNull Application application) {
        super(application);

        this.application = application;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ModsViewModel.class)) {
            return (T) new ModsViewModel(application, ModsRepository.getInstance(application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
