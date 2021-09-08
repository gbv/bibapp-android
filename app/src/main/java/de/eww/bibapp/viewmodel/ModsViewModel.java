package de.eww.bibapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.Result;
import de.eww.bibapp.network.model.DaiaItems;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.repository.ModsRepository;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.util.SruHelper;

public class ModsViewModel extends AndroidViewModel {

    private final MutableLiveData<ModsItem> selected = new MutableLiveData<>();
    private final MutableLiveData<String> forceGvkSearch = new MutableLiveData<>();

    private MutableLiveData<WatchlistResult> watchlistResult = new MutableLiveData<>();
    private MutableLiveData<SearchResult> localResult;
    private MutableLiveData<SearchResult> gvkResult;

    private MutableLiveData<ExportResult> exportResult = new MutableLiveData<>();
    private MutableLiveData<ISBDResult> isbdResult = new MutableLiveData<ISBDResult>();
    private MutableLiveData<AvailabilityResult> availabilityResult = new MutableLiveData<AvailabilityResult>();

    private final ModsRepository modsRepository;

    private int searchOffset = 1;
    private String searchQuery = "";
    private SearchManager.SEARCH_MODE searchMode;

    public ModsViewModel(@NonNull Application application, ModsRepository modsRepository) {
        super(application);

        this.modsRepository = modsRepository;
    }

    public LiveData<WatchlistResult> getWatchlistResult() {
        return watchlistResult;
    }

    public LiveData<SearchResult> getSearchResult(SearchManager.SEARCH_MODE searchMode) {
        if (searchMode == SearchManager.SEARCH_MODE.LOCAL) {
            return getSearchResultLocal();
        } else {
            return getSearchResultGvk();
        }
    }

    public LiveData<ModsItem> getSelected() {
        return selected;
    }

    public void select(ModsItem item) {
        selected.setValue(item);
    }

    public LiveData<String> getForceGvkSearch() {
        return forceGvkSearch;
    }

    public LiveData<ExportResult> getExportResult() {
        return exportResult;
    }

    public LiveData<ISBDResult> getISBDResult() {
        return isbdResult;
    }

    public LiveData<AvailabilityResult> getAvailabilityResult() {
        return availabilityResult;
    }

    public void forceGvkSearch(String searchQuery) {
        forceGvkSearch.setValue(searchQuery);
    }

    public void setSearchOffset(int offset) {
        searchOffset = offset;
    }

    public void setSearchQuery(String query) {
        query = query.replaceAll("ü", "ue");
        query = query.replaceAll("ö", "oe");
        query = query.replaceAll("ä", "ae");
        query = query.replaceAll("Ü", "ue");
        query = query.replaceAll("Ö", "oe");
        query = query.replaceAll("Ä", "ae");
        query = query.replaceAll("\\?", "*");
        query = query.replaceAll("ß", "ss");
        try {
            searchQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setSearchMode(SearchManager.SEARCH_MODE mode) {
        searchMode = mode;
    }

    public void loadWatchlist() {
        modsRepository.loadWatchlist(result -> {
            if (result instanceof Result.Success) {
                List<ModsItem> data = ((Result.Success<List<ModsItem>>) result).getData();
                watchlistResult.setValue(new WatchlistResult(data));
            } else {
                watchlistResult.setValue(new WatchlistResult(R.string.toast_mods_error));
            }
        });
    }

    public void addToWatchlist(ModsItem modsItem) {
        Result<ModsItem> result = modsRepository.addToWatchlist(modsItem);
        if (result instanceof Result.Success) {
            loadWatchlist();
        }
    }

    public void removeFromWatchlist(List<ModsItem> modsItems) {
        Result<List<ModsItem>> result = modsRepository.removeFromWatchlist(modsItems);
        if (result instanceof Result.Success) {
            loadWatchlist();
        }
    }

    public void loadSearchResults() {
        modsRepository.loadSearchResult(
                SruHelper.CATALOG_BBG,
                searchQuery,
                searchOffset,
                searchMode,
                result -> {
            if (result instanceof Result.Success) {
                SruResult data = ((Result.Success<SruResult>) result).getData();

                if (searchMode == SearchManager.SEARCH_MODE.LOCAL) {
                    localResult.setValue(new SearchResult(data));
                } else {
                    gvkResult.setValue(new SearchResult(data));
                }
            } else {
                if (searchMode == SearchManager.SEARCH_MODE.LOCAL) {
                    localResult.setValue(new SearchResult(R.string.toast_search_error));
                } else {
                    gvkResult.setValue(new SearchResult(R.string.toast_search_error));
                }
            }
        });
    }

    public void export(List<ModsItem> modsItems) {
        modsRepository.export(modsItems, result -> {
            if (result instanceof Result.Success) {
                String data = ((Result.Success<String>) result).getData();
                exportResult.setValue(new ExportResult(data));
            } else {
                exportResult.setValue(new ExportResult(R.string.toast_mods_error));
            }
        });
    }

    public void loadISBD(ModsItem modsItem) {
        modsRepository.loadISBD(modsItem, result -> {
            if (result instanceof Result.Success) {
                String data = ((Result.Success<String>) result).getData();
                isbdResult.setValue(new ISBDResult(data));
            } else {
                isbdResult.setValue(new ISBDResult(R.string.toast_mods_error));
            }
        });
    }

    public void loadAvailability(ModsItem modsItem) {
        modsRepository.loadAvailability(modsItem, result -> {
            if (result instanceof Result.Success) {
                DaiaItems data = ((Result.Success<DaiaItems>) result).getData();
                availabilityResult.setValue(new AvailabilityResult(data));
            } else {
                availabilityResult.setValue(new AvailabilityResult(R.string.toast_mods_error));
            }
        });
    }

    private LiveData<SearchResult> getSearchResultLocal()
    {
        if (localResult == null) {
            localResult = new MutableLiveData<>();
        }

        return localResult;
    }

    private LiveData<SearchResult> getSearchResultGvk()
    {
        if (gvkResult == null) {
            gvkResult = new MutableLiveData<>();
        }

        return gvkResult;
    }
}
