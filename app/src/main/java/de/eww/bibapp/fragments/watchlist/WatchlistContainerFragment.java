package de.eww.bibapp.fragments.watchlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.eww.bibapp.R;
import de.eww.bibapp.fragments.AbstractContainerFragment;

/**
 * @author Christoph Schönfeld - effective WEBWORK GmbH
 * 
 * This file is part of the Android BibApp Project
 * =========================================================
 * Watchlist Fragment class, providing a container for the users watchlist
 */
public class WatchlistContainerFragment extends AbstractContainerFragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_watchlist_main, container, false);
		
		if ( this.fragments.isEmpty() )
		{
			this.switchContent(R.id.watchlist_container, WatchlistFragment.class.getName(), "watchlist_container", false);
		}
		
		return v;
	}
}