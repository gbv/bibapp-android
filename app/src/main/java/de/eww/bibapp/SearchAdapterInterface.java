package de.eww.bibapp;

import android.support.v4.app.LoaderManager;

import java.util.ArrayList;

import de.eww.bibapp.adapters.SearchAdapter;
import de.eww.bibapp.data.SearchEntry;

public interface SearchAdapterInterface
{
	public SearchAdapter getSearchAdapter();
	public LoaderManager getLoaderManager();
	public int getHits();
	public ArrayList<SearchEntry> getResults();
}