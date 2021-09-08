package de.eww.bibapp.network.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.Result;
import de.eww.bibapp.network.SruService;
import de.eww.bibapp.network.UnAPIService;
import de.eww.bibapp.network.availability.AvailabilityManager;
import de.eww.bibapp.network.model.DaiaItems;
import de.eww.bibapp.network.model.ISBD;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.network.source.WatchlistSource;
import de.eww.bibapp.util.SruHelper;
import de.eww.bibapp.util.UnAPIHelper;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class ModsRepository {

    private static volatile ModsRepository instance;

    private Context context;

    private CompositeDisposable disposable = new CompositeDisposable();
    private AvailabilityManager availabilityManager = new AvailabilityManager();

    // private constructor : singleton access
    private ModsRepository(Context context) {
        this.context = context;
    }

    public static ModsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ModsRepository(context);
        }

        return instance;
    }

    public void loadWatchlist(final RepositoryCallback<List<ModsItem>> callback) {
        WatchlistSource.clear("watchlist");
        WatchlistSource.loadFromFile(context);

        List<ModsItem> watchlistItems = WatchlistSource.getModsItems("watchlist");

        Result<List<ModsItem>> successResult = new Result.Success<>(watchlistItems);
        callback.onComplete(successResult);
    }

    public Result<ModsItem> addToWatchlist(ModsItem modsItem) {
        // Load actual watchlist
        WatchlistSource.clear("watchlist");
        WatchlistSource.loadFromFile(context);

        // Add
        List<ModsItem> watchlistItems = WatchlistSource.getModsItems("watchlist");
        watchlistItems.add(modsItem);

        // Store new
        WatchlistSource.storeInFile(context, watchlistItems);

        return new Result.Success<>(modsItem);
    }

    public Result<List<ModsItem>> removeFromWatchlist(List<ModsItem> modsItems) {
        // Load actual watchlist
        WatchlistSource.clear("watchlist");
        WatchlistSource.loadFromFile(context);

        // Remove
        List<ModsItem> watchlistItems = WatchlistSource.getModsItems("watchlist");
        for (ModsItem modsItem: modsItems) {
            watchlistItems.remove(modsItem);
        }

        // Store new
        WatchlistSource.storeInFile(context, watchlistItems);

        return new Result.Success<>(modsItems);
    }

    public void loadSearchResult(
            String catalog,
            String searchQuery,
            int offset,
            SearchManager.SEARCH_MODE searchMode,
            final RepositoryCallback<SruResult> callback) {
        disposable.add(doRequest(catalog, searchQuery, offset, searchMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<SruResult>() {
                    @Override
                    public void onSuccess(@NonNull SruResult sruResult) {
                        Result<SruResult> successResult = new Result.Success<>(sruResult);
                        callback.onComplete(successResult);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Result<SruResult> errorResult = new Result.Error(new IOException("Error loading search results", e));
                        callback.onComplete(errorResult);
                    }
                })
        );
    }

    public void export(List<ModsItem> modsItems, final RepositoryCallback<String> callback) {
        UnAPIService service = ApiClient.getClient(context, HttpUrl.parse("http://dummy.de/")).create(UnAPIService.class);

        List<Single<?>> singles = new ArrayList<>();
        for (ModsItem modsItem: modsItems) {
            String url = UnAPIHelper.getUnAPIUrl(context,  modsItem, "isbd");
            singles.add(service.getISBD(url)
                .map((Function<ISBD, Object>) unApiISBDResponse -> {
                    String authorExtended = UnAPIHelper.convert(unApiISBDResponse.getLines(), modsItem);
                    return authorExtended + "\n";
                }));
        }

        this.disposable.add(Single.concat(singles)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .reduceWith(String::new, (a, b) -> a + b)
                .subscribe(result -> {
                    Result<String> successResult = new Result.Success<>(result);
                    callback.onComplete(successResult);
                }, e -> {
                    Result<String> exportResult = new Result.Error(new IOException("Error exporting results", e));
                    callback.onComplete(exportResult);
                })
        );
    }

    public void loadISBD(ModsItem modsItem, final RepositoryCallback<String> callback) {
        UnAPIService service = ApiClient.getClient(context, HttpUrl.parse("http://dummy.de/")).create(UnAPIService.class);
        String url = UnAPIHelper.getUnAPIUrl(context, modsItem, "isbd");
        this.disposable.add(service
                .getISBD(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ISBD>() {
                    @Override
                    public void onSuccess(ISBD unApiISBDResponse) {
                        String converted = UnAPIHelper.convert(unApiISBDResponse.getLines(), modsItem);
                        Result<String> successResult = new Result.Success<>(converted);
                        callback.onComplete(successResult);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Result<String> errorResult = new Result.Error(new IOException("Error requesting isbd information", e));
                        callback.onComplete(errorResult);
                    }
                }));
    }

    public void loadAvailability(ModsItem modsItem, final RepositoryCallback<DaiaItems> callback) {
        availabilityManager.getAvailabilityList(modsItem, disposable, daiaItems -> {
            Result<DaiaItems> successResult = new Result.Success<>(daiaItems);
            callback.onComplete(successResult);
        }, context);
    }

    private Single<SruResult> doRequest(
            String catalog,
            String searchQuery,
            int offset,
            SearchManager.SEARCH_MODE searchMode) {
        SruService service = ApiClient.getClient(context.getApplicationContext(), HttpUrl.parse("http://dummy.de/")).create(SruService.class);

        String url = SruHelper.getSearchUrl(
                searchQuery,
                offset,
                Constants.SEARCH_HITS_PER_REQUEST,
                searchMode == SearchManager.SEARCH_MODE.LOCAL,
                context,
                catalog
        );

        return service.getSearchResult(url)
                .flatMap(sruResult -> {
                    if (sruResult.getNumberOfRecords() == 0 && !catalog.equals(SruHelper.CATALOG_MAK)) {
                        return doRequest(SruHelper.CATALOG_MAK, searchQuery, offset, searchMode);
                    } else {
                        List<ModsItem> modsItems = sruResult.getItems();
                        modsItems = SruHelper.injectSearchModeIntoMods(modsItems, searchMode);
                        sruResult.setItems(modsItems);

                        return Single.just(sruResult);
                    }
                });
    }
}
