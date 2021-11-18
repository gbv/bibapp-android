package de.eww.bibapp.repository;

import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.SruService;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.network.search.SearchManager;
import de.eww.bibapp.util.SruHelper;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class SruPagingSource extends RxPagingSource<Integer, ModsItem> {

    private String query;

    public SruPagingSource(String query) {
        this.query = query;
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, ModsItem> pagingState) {
        return pagingState.getAnchorPosition();
    }

    @NotNull
    @Override
    public Single<LoadResult<Integer, ModsItem>> loadSingle(@NotNull LoadParams<Integer> loadParams) {
        // Start refresh at page 1 if undefined
        Integer nextPageNumber = loadParams.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = 1;
        }

        SruService service = ApiClient.getClient(HttpUrl.parse("http://dummy.de/")).create(SruService.class);

//        String url = SruHelper.getSearchUrl(
//                searchQuery,
//                offset,
//                Constants.SEARCH_HITS_PER_REQUEST,
//                searchMode == SearchManager.SEARCH_MODE.LOCAL,
//                context,
//                catalog
//        );

        String url = "";

        return service.getSearchResult(url)
                .flatMap(sruResult -> {
//                    if (sruResult.getNumberOfRecords() == 0 && !catalog.equals(SruHelper.CATALOG_MAK)) {
//                        return doRequest(SruHelper.CATALOG_MAK, searchQuery, offset, searchMode);
//                    } else {
//                        List<ModsItem> modsItems = sruResult.getItems();
//                        modsItems = SruHelper.injectSearchModeIntoMods(modsItems, searchMode);
//                        sruResult.setItems(modsItems);
//
//                        return Single.just(sruResult);
//                    }
                    return Single.just(sruResult);
                })
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }

    private LoadResult<Integer, ModsItem> toLoadResult(SruResult result) {
        return new LoadResult.Page<>(
            result.getItems(),
            null, // Only paging forward.
            3,
            LoadResult.Page.COUNT_UNDEFINED,
            LoadResult.Page.COUNT_UNDEFINED
        );
    }
}
