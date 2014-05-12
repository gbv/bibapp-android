package de.eww.bibapp;

import de.eww.bibapp.adapters.AvailableAdapter;
import de.eww.bibapp.data.SearchEntry;

public interface DaiaLoaderInterface
{
	public SearchEntry getSearchItem();
	public AvailableAdapter getAdapter();
}