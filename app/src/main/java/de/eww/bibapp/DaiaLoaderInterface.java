package de.eww.bibapp;

import de.eww.bibapp.adapters.AvailableAdapter;
import de.eww.bibapp.model.ModsItem;

public interface DaiaLoaderInterface
{
	public ModsItem getSearchItem();
	public AvailableAdapter getAdapter();
}