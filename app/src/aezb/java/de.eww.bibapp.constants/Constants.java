package de.eww.bibapp.constants;

public final class Constants
{
    /**
     * This is the main configuration file, please adjust these settings according to your library.
     *
     * Detailed information for each entry are given below.
     * All fields are filled with example data of the Universitätsbibliothek Lüneburg
     */

    /**
     * The PAIA URLs
     *
     * @see LOCAL_CATALOGS and keep order in sync
     */
    public static final String[] PAIA_URLS = {
        "https://aezb.api.effective-webwork.de/"
    };

    /**
     * Returns the current PAIA URL
     */
    public static final String getPaiaUrl(int localCatalogIndex)
    {
        return Constants.PAIA_URLS[localCatalogIndex];
    }

    /**
     * The name of the catalogs used for local search operations
     *
     * @see BIB_CODES and keep order in sync
     */
    public static final String[][] LOCAL_CATALOGS = {
        { "opac-de-18-64", "OPAC ÄZB", "ÄZB" }
    };

    public static final int LOCAL_CATALOG_DEFAULT = 0;

    /**
     * The name of the global catalog used for search operations
     */
    public static final String GVK_CATALOG = "gvk";

    /**
     * The library codes used in location requests
     *
     * @see LOCAL_CATALOGS and keep order in sync
     */
    public static final String[] BIB_CODES = {
        "DE-18-64"
    };

    /**
     * Returns the address to location information in JSON format
     */
    public static String getLocationUrl(int localCatalogIndex)
    {
        return "http://uri.gbv.de/organization/isil/" + Constants.BIB_CODES[localCatalogIndex] + "?format=json";
    }

    /**
     * The library codes used in daia requests
     *
     * @see LOCAL_CATALOGS and keep order in sync
     */
    public static final String[] DAIA_BIB_CODES = {
        "DE-18"
    };

    /**
     * Daia URL for availability requests
     */
    public static String getDaiaUrl(String ppn, boolean isLocal, int localCatalogIndex)
    {
        if ( isLocal == true )
        {
            return "http://daia.gbv.de/isil/" + Constants.DAIA_BIB_CODES[localCatalogIndex] + "?id=ppn:" + ppn + "&format=json";
        }
        else
        {
            return "http://daia.gbv.de/?id=gvk:ppn:" + ppn + "&format=json";
        }
    }


}