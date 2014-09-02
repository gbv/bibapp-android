package de.eww.bibapp.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import de.eww.bibapp.R;
import de.eww.bibapp.UnApiLoaderInterface;

public class UnApiLoaderCallback implements LoaderManager.LoaderCallbacks<String>
{
	UnApiLoaderInterface unApiLoaderInterface = null;
	
	public UnApiLoaderCallback(UnApiLoaderInterface unApiLoaderInterface) {
		this.unApiLoaderInterface = unApiLoaderInterface;
	}

	@Override
	public Loader<String> onCreateLoader(int loaderIndex, Bundle arg1)
	{
		Loader<String> loader = new UnApiLoader(((Fragment) this.unApiLoaderInterface).getActivity().getApplicationContext(), ((Fragment) this.unApiLoaderInterface));
		((UnApiLoader) loader).setPpn(this.unApiLoaderInterface.getSearchItem().ppn);
		((UnApiLoader) loader).setSearchEntry(this.unApiLoaderInterface.getSearchItem());
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data)
	{
		// set extended author information
		View v = ((Fragment) this.unApiLoaderInterface).getView();
		TextView authorExtendedView = (TextView) v.findViewById(R.id.detail_item_author_extended);
		
		authorExtendedView.setText(data);
		
		// unset unapi loading animation
		View progressBar = v.findViewById(R.id.progress_bar_small);
		progressBar.startAnimation(AnimationUtils.loadAnimation(((Fragment) this.unApiLoaderInterface).getActivity(), android.R.anim.fade_out));
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<String> arg0)
	{
		// TODO Auto-generated method stub
		
	}
}
