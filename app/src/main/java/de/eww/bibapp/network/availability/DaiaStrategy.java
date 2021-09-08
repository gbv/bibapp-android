package de.eww.bibapp.network.availability;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.eww.bibapp.R;
import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.DaiaService;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.DaiaItems;
import de.eww.bibapp.util.DaiaHelper;
import de.eww.bibapp.util.UrlHelper;
import io.reactivex.Observable;
import okhttp3.HttpUrl;

public class DaiaStrategy implements AvailabilityStrategy {

    private ModsItem modsItem;
    private Context context;

    public DaiaStrategy(ModsItem modsItem, Context context) {
        this.modsItem = modsItem;
        this.context = context;
    }

    @Override
    public Observable<DaiaItems> getAvailabilityList(String ppn) {
        DaiaService service = ApiClient.getClient(context.getApplicationContext(), HttpUrl.parse("http://dummy.de/")).create(DaiaService.class);

        String url = DaiaHelper.getDaiaUrl(context.getApplicationContext(), ppn, this.modsItem.isLocalSearch, "json");

        return service.getDaia(url)
                .flatMap(daiaItems -> Observable.fromIterable(daiaItems.getItems()))
                .flatMap(DaiaStrategy.this::processSingleDaiaItem)
                .reduceWith(DaiaItems::new, (daiaItems, daiaItem) -> {
                    daiaItems.addItem(daiaItem);
                    return daiaItems;
                }).toObservable();
    }

    private Observable<DaiaItem> processSingleDaiaItem(DaiaItem daiaItem) {
        try {
            HashMap<String, String> daiaInformation = DaiaHelper.getInformation(daiaItem, DaiaStrategy.this.modsItem, DaiaStrategy.this.context);
            daiaItem.setActions(daiaInformation.get("actions"));

            String uriUrl = UrlHelper.getSecureUrl(DaiaHelper.getUriUrl(daiaItem));
            if (!uriUrl.isEmpty()) {
                return DaiaStrategy.this.performSubrequest(uriUrl, daiaItem);
            } else {
                Resources resources = DaiaStrategy.this.context.getResources();
                daiaItem.setDepartment(resources.getString(R.string.detail_daia_no_department));

                return Observable.just(daiaItem);
            }
        } catch (Exception e) {
            return Observable.error(e);
        }
    }


    private Observable<DaiaItem> performSubrequest(String uriUrl, DaiaItem daiaItem) {
        DaiaService service = ApiClient.getClient(this.context, HttpUrl.parse("http://dummy.de/")).create(DaiaService.class);

        String url = uriUrl + "?format=json";

        return service.getDaiaSub(url)
                .map(stringResponse -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(stringResponse);

                        if ( jsonResponse.has(uriUrl) )
                        {
                            JSONObject locationObject = jsonResponse.getJSONObject(uriUrl);

                            if ( locationObject.has("http://dbpedia.org/property/shortName") )
                            {
                                // get list name
                                JSONArray jsonListNameArray = locationObject.getJSONArray("http://dbpedia.org/property/shortName");
                                JSONObject jsonListNameObject = jsonListNameArray.getJSONObject(jsonListNameArray.length() - 1);
                                daiaItem.setDepartment(jsonListNameObject.getString("value"));
                            }
                            else
                            {
                                // get name
                                JSONArray jsonNameArray = locationObject.getJSONArray("http://xmlns.com/foaf/0.1/name");
                                JSONObject jsonNameObject = jsonNameArray.getJSONObject(jsonNameArray.length() - 1);
                                daiaItem.setDepartment(jsonNameObject.getString("value"));
                            }

                            // get location
                            if ( locationObject.has("http://www.w3.org/2003/01/geo/wgs84_pos#location") )
                            {
                                JSONArray jsonLocationArray = locationObject.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#location");
                                JSONObject jsonLocationObject = jsonLocationArray.getJSONObject(jsonLocationArray.length() - 1);
                                String locationKey = jsonLocationObject.getString("value");

                                if ( jsonResponse.has(locationKey) )
                                {
                                    JSONObject jsonLocationContent = jsonResponse.getJSONObject(locationKey);

                                    String entryPosLong = "";
                                    String entryPosLat = "";

                                    if ( jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#long") )
                                    {
                                        JSONArray jsonLongArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#long");
                                        JSONObject jsonLongObject = jsonLongArray.getJSONObject(jsonLongArray.length() - 1);
                                        entryPosLong = jsonLongObject.getString("value");
                                    }

                                    if ( jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#lat") )
                                    {
                                        JSONArray jsonLatArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
                                        JSONObject jsonLatObject = jsonLatArray.getJSONObject(jsonLatArray.length() - 1);
                                        entryPosLat = jsonLatObject.getString("value");
                                    }

                                    if ( !entryPosLong.isEmpty() && !entryPosLat.isEmpty() )
                                    {
                                        daiaItem.setLocation(new LocationItem("", "", "", new ArrayList<>(), "", "", "", entryPosLong, entryPosLat, ""));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        // remove location action for this item
                        String actions = daiaItem.getActions();
                        Pattern pattern = Pattern.compile(";*location");
                        Matcher matcher = pattern.matcher(actions);

                        if (matcher.find()) {
                            actions = matcher.replaceAll("");
                        }

                        daiaItem.setActions(actions);
                    }

                    return daiaItem;
                });
    }
}
