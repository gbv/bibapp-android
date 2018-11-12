package de.eww.bibapp.network.availability;

import de.eww.bibapp.network.model.DaiaItems;
import io.reactivex.Observable;

public interface AvailabilityStrategy {
    Observable<DaiaItems> getAvailabilityList(String ppn);
}
