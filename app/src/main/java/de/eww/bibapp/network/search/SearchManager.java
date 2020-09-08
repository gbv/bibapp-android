package de.eww.bibapp.network.search;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.SruService;
import de.eww.bibapp.network.model.SruResult;
import de.eww.bibapp.util.PrefUtils;
import de.eww.bibapp.util.SruHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;

public class SearchManager {

    public enum SEARCH_MODE {
        LOCAL,
        GVK
    }

    public interface SearchLoaderInterface {
        void onSearchRequestDone(SruResult sruResult);
        void onSearchRequestFailed();
    }

    private int offset = 1;
    private String searchQuery = "";
    private SEARCH_MODE searchMode;

    public void getSearchResults(
            CompositeDisposable disposable,
            SearchLoaderInterface callback,
            Context context)
    {
        disposable.add(this.doRequest(context, SruHelper.CATALOG_BBG)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(sruResult -> {
                callback.onSearchRequestDone(sruResult);
            }, error -> {
                error.printStackTrace();
                callback.onSearchRequestFailed();
            }));
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        searchQuery = searchQuery.replaceAll("ü", "ue");
        searchQuery = searchQuery.replaceAll("ö", "oe");
        searchQuery = searchQuery.replaceAll("ä", "ae");
        searchQuery = searchQuery.replaceAll("Ü", "ue");
        searchQuery = searchQuery.replaceAll("Ö", "oe");
        searchQuery = searchQuery.replaceAll("Ä", "ae");
        searchQuery = searchQuery.replaceAll("\\?", "*");
        searchQuery = searchQuery.replaceAll("ß", "ss");
        try {
            this.searchQuery = URLEncoder.encode(searchQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public SEARCH_MODE getSearchMode()
    {
        return searchMode;
    }

    public void setSearchMode(SEARCH_MODE searchMode)
    {
        this.searchMode = searchMode;
    }

    private Observable<SruResult> doRequest(Context context, String catalog)
    {
        SruService service = ApiClient.getClient(context.getApplicationContext(), HttpUrl.parse("http://dummy.de/")).create(SruService.class);

        String url = SruHelper.getSearchUrl(
            this.searchQuery,
            this.offset,
            Constants.SEARCH_HITS_PER_REQUEST,
            this.searchMode == SEARCH_MODE.LOCAL,
            context,
            catalog
        );

        return service.getSearchResult(url)
            .flatMap(sruResult -> {
                int numResults = (Integer) sruResult.getResult().get("numberOfRecords");

                if (numResults == 0 && !catalog.equals(SruHelper.CATALOG_MAK)) {
                    return this.doRequest(context, SruHelper.CATALOG_MAK);
                } else {
                    HashMap<String, Object> result = sruResult.getResult();
                    List<ModsItem> modsItems = (List<ModsItem>) result.get("list");
                    modsItems = SruHelper.injectSearchModeIntoMods(modsItems, this.searchMode);
                    result.put("list", modsItems);
                    sruResult.setResult(result);

                    return Observable.just(sruResult);
                }
            });
    }
}
