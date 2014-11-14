package de.eww.bibapp.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import de.eww.bibapp.model.ModsItem;

public class UnApiLoaderCallback implements LoaderManager.LoaderCallbacks<String> {

	UnApiLoaderInterface unApiLoaderInterface = null;
    ModsItem mModsItem;

    public interface UnApiLoaderInterface {
        public void onUnApiRequestDone(String authorExtended);
    }

	public UnApiLoaderCallback(UnApiLoaderInterface unApiLoaderInterface, ModsItem modsItem) {
		this.unApiLoaderInterface = unApiLoaderInterface;
        mModsItem = modsItem;
	}

	@Override
	public Loader<String> onCreateLoader(int loaderIndex, Bundle arg1) {
		Loader<String> loader = new UnApiLoader(((Fragment) this.unApiLoaderInterface).getActivity().getApplicationContext(), ((Fragment) this.unApiLoaderInterface));
		((UnApiLoader) loader).setPpn(mModsItem.ppn);
		((UnApiLoader) loader).setSearchEntry(mModsItem);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<String> loader, String data) {
        unApiLoaderInterface.onUnApiRequestDone(data);
	}

	@Override
	public void onLoaderReset(Loader<String> arg0) {
		// empty
	}
}
