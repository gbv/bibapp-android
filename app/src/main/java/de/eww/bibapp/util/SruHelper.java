package de.eww.bibapp.util;

import android.content.Context;

import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.search.SearchManager;

public class SruHelper {

    public static final String CATALOG_BBG = "pica.bbg";
    public static final String CATALOG_MAK = "pica.mak";

    // The name of the global catalog used for search operations
    public static final String CATALOG_GVK = "gvk";

    /**
     * This determs the URL for search requests
     */
    public static String getSearchUrl(String search, int start, int numRecords, boolean isLocalSearch, Context context)
    {
        return SruHelper.getSearchUrl(search, start, numRecords, isLocalSearch, context, SruHelper.CATALOG_BBG);
    }

    /**
     * This determs the URL for search requests
     */
    public static String getSearchUrl(String search, int start, int numRecords, boolean isLocalSearch, Context context, String catalog)
    {
        String url = "https://sru.k10plus.de/";

        if (isLocalSearch) {
            String[] sruIsils = context.getResources().getStringArray(R.array.bibapp_sru_isils);
            int localCatalogIndex = PrefUtils.getLocalCatalogIndex(context);
            url += sruIsils[localCatalogIndex];
        } else {
            url += SruHelper.CATALOG_GVK;
        }

        url += "?version=1.1&operation=searchRetrieve&query=pica.all=" + search + "+or+pica.tmb=" + search +
                "+not+(" + catalog + "=ac*+or+" + catalog + "=bc*+or+" + catalog + "=ec*+or+" + catalog +
                "=gc*+or+" + catalog + "=kc*+or+" + catalog + "=mc*+or+" + catalog + "=oc*+or+" + catalog + "=sc*)" +
                "&startRecord=" + start + "&maximumRecords=" + numRecords + "&recordSchema=mods";

        return url;
    }

    public static List<ModsItem> injectSearchModeIntoMods(List<ModsItem> list, SearchManager.SEARCH_MODE searchMode)
    {
        for (ModsItem item: list) {
            item.setIsLocalSearch(searchMode.equals(SearchManager.SEARCH_MODE.LOCAL));
        }

        return list;
    }
}
