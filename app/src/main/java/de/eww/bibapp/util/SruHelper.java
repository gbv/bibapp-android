package de.eww.bibapp.util;

import de.eww.bibapp.constants.Constants;

public class SruHelper {

    public static final String CATALOG_BBG = "pica.bbg";
    public static final String CATALOG_MAK = "pica.mak";

    /**
     * This determs the URL for search requests
     */
    public static String getSearchUrl(String search, int start, int numRecords, boolean isLocalSearch, int localCatalogIndex)
    {
        return SruHelper.getSearchUrl(search, start, numRecords, isLocalSearch, localCatalogIndex, SruHelper.CATALOG_BBG);
    }

    /**
     * This determs the URL for search requests
     */
    public static String getSearchUrl(String search, int start, int numRecords, boolean isLocalSearch, int localCatalogIndex, String catalog)
    {
        String url = "http://sru.k10plus.de/";

        if (isLocalSearch) {
            url += Constants.LOCAL_CATALOGS[localCatalogIndex][0];
        } else {
            url += Constants.GVK_CATALOG;
        }

        url += "?version=1.1&operation=searchRetrieve&query=pica.all=" + search + "+or+pica.tmb=" + search +
                "+not+(" + catalog + "=ac*+or+" + catalog + "=bc*+or+" + catalog + "=ec*+or+" + catalog +
                "=gc*+or+" + catalog + "=kc*+or+" + catalog + "=mc*+or+" + catalog + "=oc*+or+" + catalog + "=sc*)" +
                "&startRecord=" + start + "&maximumRecords=" + numRecords + "&recordSchema=mods";

        return url;
    }
}
