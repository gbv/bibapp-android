package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.Result;
import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.network.locations.LocationsManager;
import de.eww.bibapp.network.model.StatefullData;
import de.eww.bibapp.network.model.paia.PaiaItems;
import de.eww.bibapp.network.model.paia.PaiaPatron;
import de.eww.bibapp.util.UrlHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LocationsViewModel extends AndroidViewModel {
    private MutableLiveData<StatefullData<List<LocationItem>>> locations;
    private MutableLiveData<SingleLocationResult> singleLocationResult = new MutableLiveData<>();

    private final MutableLiveData<LocationItem> selected = new MutableLiveData<>();

    private LocationsManager locationsManager;

    private CompositeDisposable disposable = new CompositeDisposable();

    public LocationsViewModel(Application application) {
        super(application);

        locationsManager = new LocationsManager();
    }

    public LiveData<StatefullData<List<LocationItem>>> getLocations() {
        if (locations == null) {
            locations = new MutableLiveData<>();
            loadLocations();
        }

        return locations;
    }

    public LiveData<StatefullData<List<LocationItem>>> refreshLocations() {
        loadLocations();

        return locations;
    }

    public void select(LocationItem item) {
        selected.setValue(item);
    }

    public LiveData<LocationItem> getSelected() {
        return selected;
    }

    public LiveData<SingleLocationResult> getSingleLocation() {
        return singleLocationResult;
    }

    public void loadSingleLocation(String url) {
        disposable.add(locationsManager.getSingleLocation(url, getApplication())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locationResult -> {
                    singleLocationResult.setValue(new SingleLocationResult(locationResult));
                }, error -> {
                    singleLocationResult.setValue(new SingleLocationResult(R.string.toast_locations_error));
                }));
    }

    private void loadLocations() {
        String url = UrlHelper.getLocationUrl(getApplication(), "json");
        disposable.add(locationsManager.getLocations(url, getApplication())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(locationsResult -> {
                locations.setValue(new StatefullData<>(locationsResult, false));
            }, error -> {
                locations.setValue(new StatefullData<>(null, true));
            }));
    }
}
