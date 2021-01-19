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
        "https://paia.gbv.de/DE-564"
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
        { "opac-de-564", "OPAC NDH", "NDH" }
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
        "DE-564"
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
        "DE-564"
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

    /**
     * An URL to a news feed
     */
    public static final String NEWS_URL = "";

    /**
     * This is the number of hits display by a single request - without scroll reloads
     */
    public static final int SEARCH_HITS_PER_REQUEST = 20;

    /**
     * The interlending URL
     */
    public static String getInterlendingUrl(String ppn)
    {
        return "http://gso.gbv.de/DB=2.1/PPNSET?PPN=" + ppn;
    }

    /**
     * DBS Counting (called on every search request)
     */
    public static String DBS_COUNTING_URL = "";

    /**
     * If true, either the storage or the department location of an exemplar are displayed, otherwise both if available
     */
    public static boolean EXEMPLAR_SHORT_DISPLAY = false;

    /**
     * You probably do not need to change anything below this line
     * =====================================================================
     */

    public static double EARTH_FLATTENING = 0.00335281066474748071984552861852;
    public static double EQUATORIAL_RADIUS = 6378.137;

    public static final int READ_TIMEOUT = 15000; /* ms */
    public static final int CONNECTION_TIMEOUT = 15000; /* ms */
}