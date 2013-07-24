package de.eww.bibapp.constants;

public final class Constants
{
	/**
	 * This is the main configuration file, please adjust these settings according to your library.
	 * 
	 * Detailed information for each entry are given below.
	 * All fields are filled with example data of the Universitätsbibliothek Hildesheim
	 */
	
	/**
	 * The library code used in paia and location requests
	 */
	public static final String BIB_CODE = "DE-Hil2";
	
	/**
	 * The paia URL appended with the bib code
	 */
	public static final String PAIA_URL = "https://paia.gbv.de/isil/" + BIB_CODE;
	
	/**
	 * The name of the catalogs used for local search operations
	 */
	public static final String[][] LOCAL_CATALOGS = {
		{ "opac-de-hil2", "Lokal 1" },
		{ "opac-de-hil2", "Lokal 2" }
	};
	
	public static final int LOCAL_CATALOG_DEFAULT = 0;
	
	/**
	 * The name of the global catalog used for search operations
	 */
	public static final String GVK_CATALOG = "gvk";
	
	/**
	 * Address to location information in JSON format
	 */
	public static final String LOCATION_URL = "http://uri.gbv.de/organization/isil/" + BIB_CODE + "?format=json";
	
	/**
	 * An URL to a news feed
	 */
	public static final String NEWS_URL = "https://www.uni-hildesheim.de/index.php?id=8920&type=100";
	
	/**
	 * This is the number of hits display by a single request - without scroll reloads
	 */
	public static final int SEARCH_HITS_PER_REQUEST = 20;
	
	/**
	 * This determs the URL for search requests
	 */
	public static String getSearchUrl(String search, int start, int numRecords, boolean isLocalSearch, int localCatalogIndex)
	{
		String url = null;
		
		url = "http://sru.gbv.de/";
		
		if ( isLocalSearch == true )
		{
			url += Constants.LOCAL_CATALOGS[localCatalogIndex][0];
		}
		else
		{
			url += Constants.GVK_CATALOG;
		}
		
		url += "?version=1.1&operation=searchRetrieve&query=pica.all=" + search + "+or+pica.tmb=" + search + "+not+(pica.mak=ac*+or+pica.mak=bc*+or+pica.mak=ec*+or+pica.mak=gc*+or+pica.mak=kc*+or+pica.mak=mc*+or+pica.mak=oc*+or+pica.mak=sc*)";
		url += "&startRecord=" + start + "&maximumRecords=" + numRecords + "&recordSchema=mods";
		
		return url;
	}
	
	/**
	 * The interlending URL
	 */
	public static String getInterlendingUrl(String ppn)
	{
		return "http://gso.gbv.de/DB=2.1/PPNSET?PPN=" + ppn;
	}
	
	/**
	 * The UnAPI URL for extended information
	 */
	public static String getUnApiUrl(String ppn)
	{
		return "http://unapi.gbv.de/?id=gvk:ppn:" + ppn + "&format=isbd";
	}
	
	/**
	 * Daia URL for availability requests
	 */
	public static String getDaiaUrl(String ppn, boolean isLocal)
	{
		if ( isLocal == true )
		{
			return "http://daia.gbv.de/isil/DE-Hil2?id=ppn:" + ppn + "&format=xml";
		}
		else
		{
			return "http://daia.gbv.de/?id=gvk:ppn:" + ppn + "&format=xml";
		}
	}
	
	/**
	 * URL for getting images
	 */
	public static String getImageUrl(String isbn)
	{
		return "http://ws.gbv.de/covers/?id=" + isbn + "&format=img";
	}
	
	/**
	 * DBS Counting (called on every search request)
	 */
	public static String DBS_COUNTING_URL = "http://dbspixel.hbz-nrw.de/count?id=AN087&page=3";
	
	/**
	 * You probably do not need to change anything below this line
	 * =====================================================================
	 */
	
	public static double EARTH_FLATTENING = 0.00335281066474748071984552861852;
	public static double EQUATORIAL_RADIUS = 6378.137;
	
	public static final int READ_TIMEOUT = 15000; /* ms */
	public static final int CONNECTION_TIMEOUT = 15000; /* ms */
}