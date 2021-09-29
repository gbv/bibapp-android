package de.eww.bibapp.network.availability;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.DaiaItems;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AvailabilityManager {

    public interface DaiaLoaderInterface {
        void onDaiaRequestDone(DaiaItems daiaItems);
    }

    public void getAvailabilityList(
            ModsItem modsItem,
            CompositeDisposable disposable,
            DaiaLoaderInterface callback,
            Context context) {

        AvailabilityStrategy strategy = this.resolveStrategy(modsItem, context);
        disposable.add(strategy.getAvailabilityList(modsItem.ppn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(daiaItems -> {
                List<DaiaItem> daiaList = daiaItems.getItems();

                // Group by department, if we are processing a gvk search
                if (!modsItem.isLocalSearch) {
                    daiaList = AvailabilityManager.this.groupByDepartment(daiaList, context);
                }

                DaiaItems response = new DaiaItems();
                response.setItems(daiaList);
                callback.onDaiaRequestDone(response);
            }, error -> {
                error.printStackTrace();
            }));
    }

    private AvailabilityStrategy resolveStrategy(ModsItem modsItem, Context context) {
        Resources resources = context.getResources();
        String[] blockOrderTypes = resources.getStringArray(R.array.block_order_types);

        if (blockOrderTypes.length > 0) {
            return new DaiaSubStrategy(modsItem, context);
        }

        return new DaiaStrategy(modsItem, context);
    }

    /**
     * Iterates over a list of given daia items and groups them by department. If any item does not
     * have a department value (or the value of "Ohne Zuordnung"), a default one will be created and
     * all related items will be grouped under the default one.
     *
     * @param ungroupedList The list of ungrouped daia items loaded online
     *
     * @return The daia item list grouped by department
     */
    private List<DaiaItem> groupByDepartment(List<DaiaItem> ungroupedList, Context context) {
        HashMap<String, DaiaItem> hashMap = new HashMap<>();

        Resources resources = context.getResources();
        String daiaDefaultDepartment = resources.getString(R.string.daia_default_department);

        Iterator<DaiaItem> it = ungroupedList.iterator();
        while (it.hasNext()) {
            DaiaItem daiaItem = it.next();

            // Update department "Ohne Zuordnung"
            if (!daiaItem.hasDepartment() || daiaItem.getDepartment().equals("Ohne Zuordnung")) {
                daiaItem.setDepartment(daiaDefaultDepartment);
            }

            // Grouping
            if (hashMap.containsKey(daiaItem.getDepartment())) {
                DaiaItem daiaHashItem = hashMap.get(daiaItem.getDepartment());

                if (!daiaItem.getLabel().isEmpty()) {
                    daiaHashItem.setLabel(daiaHashItem.getLabel() + ", " + daiaItem.getLabel());
                }

                hashMap.put(daiaItem.getDepartment(), daiaHashItem);
            } else {
                hashMap.put(daiaItem.getDepartment(), daiaItem);
            }
        }

        ArrayList<DaiaItem> daiaResponseList = new ArrayList<>(hashMap.values());

        // Sort alphabetically
        Collections.sort(daiaResponseList, (lhs, rhs) -> lhs.getDepartment().compareTo(rhs.getDepartment()));

        // Make sure, that the default department fallback is the last one in the list
        it = daiaResponseList.iterator();
        while (it.hasNext()) {
            DaiaItem daiaItem = it.next();

            if (daiaItem.getDepartment().equals(daiaDefaultDepartment)) {
                daiaResponseList.remove(daiaItem);
                daiaResponseList.add(daiaItem);
                break;
            }
        }

        return daiaResponseList;
    }
}
