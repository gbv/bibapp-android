package de.eww.bibapp;

import de.eww.bibapp.adapter.AvailableAdapterOld;
import de.eww.bibapp.model.ModsItem;

public interface DaiaLoaderInterface
{
	public ModsItem getSearchItem();
	public AvailableAdapterOld getAdapter();
}