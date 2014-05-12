package de.eww.bibapp.data;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchEntry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2105584000233919546L;
	public final String title;
	public final String subTitle;
	public final String partNumber;
	public final String partName;
	public final String mediaType;
	public final String ppn;
	public final String isbn;
	public final ArrayList<String> authors;
	public final String onlineUrl;
	public final ArrayList<String> indexArray;
	
	public boolean isLocalSearch = true;
	
	public SearchEntry(String title, String subTitle, String partNumber, String partName, String mediaType, String ppn, String isbn, ArrayList<String> authors, String onlineUrl, ArrayList<String> indexArray)
	{
		this.title = title;
		this.subTitle = subTitle;
		this.partNumber = partNumber;
		this.partName = partName;
		this.mediaType = mediaType;
		this.ppn = ppn;
		this.isbn = isbn;
		this.authors = authors;
		this.onlineUrl = onlineUrl;
		this.indexArray = indexArray;
	}
	
	public void setIsLocalSearch(boolean isLocalSearch)
	{
		this.isLocalSearch = isLocalSearch;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj == null )
		{
	        return false;
	    }
		
	    if ( getClass() != obj.getClass() )
	    {
	        return false;
	    }
	    
	    final SearchEntry compare = (SearchEntry) obj;
		return this.ppn.equals(compare.ppn);
	}
}
