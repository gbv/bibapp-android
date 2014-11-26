package de.eww.bibapp.tasks;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.DaiaItem;
import de.eww.bibapp.model.ModsItem;

/**
* @author Christoph Schönfeld - effective WEBWORK GmbH
*
* This file is part of the Android BibApp Project
* =========================================================
* Callback for daia communication
*/
public class DaiaLoaderCallback implements
	LoaderManager.LoaderCallbacks<List<DaiaItem>> {

	private DaiaLoaderInterface daiaLoaderInterface = null;

    public interface DaiaLoaderInterface {
        public ModsItem getModsItem();
        public void onDaiaRequestDone(List<DaiaItem> daiaItems);
    }

	public DaiaLoaderCallback(DaiaLoaderInterface daiaLoaderInterface) {
		this.daiaLoaderInterface = daiaLoaderInterface;
	}

	@Override
	public Loader<List<DaiaItem>> onCreateLoader(int loaderIndex, Bundle arg1) {
		Loader<List<DaiaItem>> loader = new DaiaLoader(((Fragment) this.daiaLoaderInterface).getActivity(), (Fragment) this.daiaLoaderInterface);
		((DaiaLoader) loader).setPpn(this.daiaLoaderInterface.getModsItem().ppn);
		((DaiaLoader) loader).setFromLocalSearch(this.daiaLoaderInterface.getModsItem().isLocalSearch);
		((DaiaLoader) loader).setItem(this.daiaLoaderInterface.getModsItem());

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<List<DaiaItem>> loader, List<DaiaItem> data) {
        List<DaiaItem> daiaList;

        // Group by department
        daiaList = groupByDepartment(data);

        this.daiaLoaderInterface.onDaiaRequestDone(daiaList);
	}

	@Override
	public void onLoaderReset(Loader<List<DaiaItem>> arg0) {
		// empty
	}

    // GVK: Standortangaben, die statt Signaturen Volltext-URLs enthalten, werden offenbar bei "Ohne Zuordnung" zusammengefasst und ohne diese Signaturen-URLs angezeigt

    /**
     * Iterates over a list of given daia items and groups them by department. If any item does not
     * have a department value (or the value of "Ohne Zuordnung"), a default one will be created and all related items will be grouped under
     * the default one.
     *
     * @param ungroupedList The list of ungrouped daia items loaded online
     *
     * @return The daia item list grouped by department
     */
    private List<DaiaItem> groupByDepartment(List<DaiaItem> ungroupedList) {
        HashMap<String, DaiaItem> hashMap = new HashMap<String, DaiaItem>();

        Resources resources = ((Fragment) this.daiaLoaderInterface).getResources();
        String daiaDefaultDepartment = resources.getString(R.string.daia_default_department);

        Iterator<DaiaItem> it = ungroupedList.iterator();
        while (it.hasNext()) {
            DaiaItem daiaItem = it.next();

            if (!daiaItem.hasDepartment() || daiaItem.getDepartment().equals("Ohne Zuordnung")) {
                daiaItem.setDepartment(daiaDefaultDepartment);
            }

            if (hashMap.containsKey(daiaItem.getDepartment())) {
                DaiaItem daiaHashItem = hashMap.get(daiaItem.getDepartment());

                if (daiaItem.hasLabel()) {
                    daiaHashItem.label += ", " + daiaItem.label;
                }

                hashMap.put(daiaItem.getDepartment(), daiaHashItem);
            } else {
                hashMap.put(daiaItem.getDepartment(), daiaItem);
            }
        }

        return new ArrayList<DaiaItem>(hashMap.values());
    }
}
