package de.eww.bibapp.network.availability;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.eww.bibapp.R;
import de.eww.bibapp.constants.Constants;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.FamService;
import de.eww.bibapp.network.UnAPIService;
import de.eww.bibapp.network.model.DaiaItems;
import de.eww.bibapp.network.model.fam.FamSet;
import de.eww.bibapp.network.model.pica.PicaDatafield;
import de.eww.bibapp.network.model.pica.PicaRecord;
import de.eww.bibapp.util.PrefUtils;
import io.reactivex.Observable;
import okhttp3.HttpUrl;

public class DaiaSubStrategy implements AvailabilityStrategy {

    private ModsItem modsItem;
    private Context context;

    public DaiaSubStrategy(ModsItem modsItem, Context context) {
        this.modsItem = modsItem;
        this.context = context;
    }

    @Override
    public Observable<DaiaItems> getAvailabilityList(String ppn) {
        // request UNAPI for pica in xml format
        UnAPIService service = ApiClient.getClient(context, HttpUrl.parse("http://dummy.de/")).create(UnAPIService.class);
        String url = Constants.getUnApiUrl(ppn, "picaxml");

        return service.getPica(url)
                .flatMap(picaRecord -> DaiaSubStrategy.this.processPicaRecord(picaRecord, ppn));
    }

    private Observable<DaiaItems> processPicaRecord(PicaRecord picaRecord, String ppn) {
        Resources resources = context.getResources();
        String[] blockOrderTypes = resources.getStringArray(R.array.block_order_types);

        boolean mayContainBlockableOrders = false;

        // look for any block order type in any datafield
        List<String> blockOrderTypeList = Arrays.asList(blockOrderTypes);
        List<PicaDatafield> datafields = picaRecord.getDatafields();
        for (PicaDatafield datafield: datafields) {
            if (blockOrderTypeList.contains(datafield.getTag())) {
                // iterate all subfields an check its value (is there a "b" at the second position)
                List<String> subfieldValues = new ArrayList<>(datafield.getSubfields().values());
                for (String subfieldValue: subfieldValues) {
                    if (subfieldValue.length() >= 2 && subfieldValue.charAt(1) == 'b') {
                        // this now indicates that the specific type of that entry may contain blockable orders
                        mayContainBlockableOrders = true;
                        break;
                    }
                }
            }
        }

        boolean useDaiaSubRequests = resources.getBoolean(R.bool.use_daia_sub_requests);
        if (mayContainBlockableOrders && useDaiaSubRequests) {
            return this.performFamRequest(this.modsItem, 0);
        } else {
            return this.performFallbackRequest(ppn);
        }
    }

    private Observable<DaiaItems> performFamRequest(ModsItem modsItem, int start) {
        FamService service = ApiClient.getClient(this.context, HttpUrl.parse("http://dummy.de/")).create(FamService.class);

        // find the fam url to use
        int localCatalogIndex = PrefUtils.getLocalCatalogIndex(this.context);
        Resources resources = this.context.getResources();
        String[] famUrls = resources.getStringArray(R.array.fam_urls);
        String famUrl = String.format(famUrls[localCatalogIndex], start, modsItem.ppn);

        // send a Fam-Request starting at index 0 with the mods ppn
        return service.getFam(famUrl)
                .flatMap(famResult -> {
                    // If the XML reponse does contain any tags in path RESULT/SET/SHORTTITLE, those will be
                    // the following items
                    FamSet famSet = famResult.getSet();

                    if (famSet != null) {
                        Observable<DaiaItems> observable = Observable.fromArray(famSet.getItems())
                                .flatMap(Observable::fromIterable)
                                // check if the fam ppn does not match the mods item
                                .filter(famItem -> !modsItem.ppn.equals(famItem.getPPN()))
                                .flatMap(famItem -> {
                                    // perform daia request with the fam ppn
                                    DaiaStrategy daiaStrategy = new DaiaStrategy(modsItem, DaiaSubStrategy.this.context);
                                    return daiaStrategy.getAvailabilityList(famItem.getPPN());
                                })
                                .reduceWith(DaiaItems::new, (daiaItems, daiaItemsNew) -> {
                                    daiaItems.addItems(daiaItemsNew.getItems());
                                    return daiaItems;
                                }).toObservable();

                        int hits = famSet.getHits();
                        if ((hits - start) > 10) {
                            // get the next chunk
                            return Observable.merge(
                                    observable,
                                    DaiaSubStrategy.this.performFamRequest(modsItem, start + 10)
                            );
                        } else {
                            return observable;
                        }
                    } else {
                        // fallback
                        return DaiaSubStrategy.this.performFallbackRequest(modsItem.ppn);
                    }
                });
    }

    public Observable<DaiaItems> performFallbackRequest(String ppn) {
        DaiaStrategy daiaStrategy = new DaiaStrategy(modsItem, DaiaSubStrategy.this.context);
        return daiaStrategy.getAvailabilityList(ppn);
    }
}
