package de.eww.bibapp.network.locations;

import android.content.Context;
import android.webkit.URLUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.eww.bibapp.network.model.LocationItem;
import de.eww.bibapp.network.ApiClient;
import de.eww.bibapp.network.UriService;
import de.eww.bibapp.util.UrlHelper;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.HttpUrl;

public class LocationsManager {

    public Observable<List<LocationItem>> getLocations(
            String url,
            Context context) {
        return performLocationRequest(url, context, true).toList().toObservable();
    }

    public Observable<LocationItem> getSingleLocation(String url, Context context) {
        UriService service = ApiClient.getClient(context, HttpUrl.parse("http://dummy.de/")).create(UriService.class);
        String requestUrl = UrlHelper.getSecureUrl(url + "?format=json");

        return service.getLocation(requestUrl)
                .flatMap(jsonResponse -> {
                    if (jsonResponse.has(url)) {
                        JSONObject jsonObject = (JSONObject) jsonResponse.get(url);
                        return Observable.just(createLocationFromJSON(jsonObject, jsonResponse));
                    }

                    return Observable.empty();
                });
    }

    private Observable<LocationItem> performLocationRequest(String url, Context context, boolean isMainRequest) {
        UriService service = ApiClient.getClient(context, HttpUrl.parse("http://dummy.de/")).create(UriService.class);

        return service.getLocations(url)
                .flatMap(jsonResponse -> {
                    try {
                        if (isMainRequest) {
                            JSONObject mainObject = this.findMainObject(jsonResponse, context);

                            Observable<LocationItem> mainObservable = Observable.just(this.createLocationFromJSON(mainObject, jsonResponse));

                            // iterate the elements of the "http://www.w3.org/ns/org#hasSite" key, holding all child locations
                            JSONArray jsonChildArray = mainObject.getJSONArray("http://www.w3.org/ns/org#hasSite");

                            if (jsonChildArray.length() > 0) {
                                return Observable.merge(
                                        mainObservable,
                                        Observable.range(0, jsonChildArray.length())
                                                .map(jsonChildArray::get)
                                                .flatMap(jsonChildContent -> {
                                                    // get the uri of the child
                                                    String childUri = ((JSONObject) jsonChildContent).getString("value");
                                                    childUri = UrlHelper.getSecureUrl(childUri) + "?format=json";
                                                    return performLocationRequest(childUri, context, false);
                                                })
                                );
                            } else {
                                return mainObservable;
                            }

                        } else {
                            String lookupKey = UrlHelper.getInsecureUrl(url.substring(0, url.length() - 12));
                            if (jsonResponse.has(lookupKey)) {
                                JSONObject childObject = (JSONObject) jsonResponse.get(lookupKey);
                                return Observable.just(createLocationFromJSON(childObject, jsonResponse));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return Observable.empty();
                });
    }

    private JSONObject findMainObject(JSONObject jsonResponse, Context context) {
        JSONObject mainEntry;

        try {
            // search the main entry, this should be the one with the key "http://www.w3.org/ns/org#hasSite"
            Iterator<?> keyIterator = jsonResponse.keys();
            while (keyIterator.hasNext()) {
                String key = (String) keyIterator.next();
                JSONObject jsonContent = (JSONObject) jsonResponse.get(key);

                if (jsonContent.has("http://www.w3.org/ns/org#hasSite")) {
                    return jsonContent;
                }
            }

            // if we did not found a main entry, try to find one with the uri url as key
            String lookupKey = UrlHelper.getLocationUrl(context, "json").substring(0, UrlHelper.getLocationUrl(context, "json").length() - 12);
            if (jsonResponse.has(lookupKey)) {
                mainEntry = (JSONObject) jsonResponse.get(lookupKey);
                return mainEntry;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private LocationItem createLocationFromJSON(JSONObject jsonObject, JSONObject completeResponse) throws JSONException {
        // prepare LocationsEntry data
        String entryName = "";
        String entryListName = "";
        String entryAddress = "";
        ArrayList<String> listOpeningHours = new ArrayList<>();
        String entryEmail = "";
        String entryUrl = "";
        String entryPhone = "";
        String entryPosLong = "";
        String entryPosLat = "";
        String entryDescription = "";

        // check if this is a locations entry - we assume this, if there is "http://xmlns.com/foaf/0.1/name" value
        if (jsonObject.has("http://xmlns.com/foaf/0.1/name")) {
            // get name
            JSONArray jsonNameArray = jsonObject.getJSONArray("http://xmlns.com/foaf/0.1/name");
            JSONObject jsonNameObject = jsonNameArray.getJSONObject(jsonNameArray.length() - 1);
            entryName = jsonNameObject.getString("value");

            // get list name
            if (jsonObject.has("http://dbpedia.org/property/shortName")) {
                JSONArray jsonListNameArray = jsonObject.getJSONArray("http://dbpedia.org/property/shortName");
                JSONObject jsonListNameObject = jsonListNameArray.getJSONObject(jsonListNameArray.length() - 1);
                entryListName = jsonListNameObject.getString("value");
            } else {
                entryListName = entryName;
            }

            // get address
            if (jsonObject.has("http://purl.org/ontology/gbv/address")) {
                JSONArray jsonAddressArray = jsonObject.getJSONArray("http://purl.org/ontology/gbv/address");
                JSONObject jsonAddressObject = jsonAddressArray.getJSONObject(jsonAddressArray.length() - 1);
                entryAddress = jsonAddressObject.getString("value");
            } else {
                if (completeResponse.has("_:b2")) {
                    JSONObject b2Object = completeResponse.getJSONObject("_:b2");
                    entryAddress = this.extractLocationFromB2(b2Object);
                }
            }

            // get opening hours
            if (jsonObject.has("http://purl.org/ontology/gbv/openinghours")) {
                JSONArray jsonOpeningHoursArray = jsonObject.getJSONArray("http://purl.org/ontology/gbv/openinghours");

                for (int i = 0; i < jsonOpeningHoursArray.length(); i++) {
                    JSONObject jsonOpeningHoursObject = jsonOpeningHoursArray.getJSONObject(i);
                    listOpeningHours.add(jsonOpeningHoursObject.getString("value"));
                }
            }

            // get email
            if (jsonObject.has("http://www.w3.org/2006/vcard/ns#email")) {
                JSONArray jsonEmailArray = jsonObject.getJSONArray("http://www.w3.org/2006/vcard/ns#email");
                JSONObject jsonEmailObject = jsonEmailArray.getJSONObject(jsonEmailArray.length() - 1);
                entryEmail = jsonEmailObject.getString("value");
            }

            // get url
            if (jsonObject.has("http://www.w3.org/2006/vcard/ns#url")) {
                JSONArray jsonUrlArray = jsonObject.getJSONArray("http://www.w3.org/2006/vcard/ns#url");
                JSONObject jsonUrlObject = jsonUrlArray.getJSONObject(jsonUrlArray.length() - 1);
                entryUrl = jsonUrlObject.getString("value");
            }

            // get phone
            if (jsonObject.has("http://xmlns.com/foaf/0.1/phone")) {
                JSONArray jsonPhoneArray = jsonObject.getJSONArray("http://xmlns.com/foaf/0.1/phone");
                JSONObject jsonPhoneObject = jsonPhoneArray.getJSONObject(jsonPhoneArray.length() - 1);
                entryPhone = jsonPhoneObject.getString("value");
            }

            // get location
            if (jsonObject.has("http://www.w3.org/2003/01/geo/wgs84_pos#location")) {
                JSONArray jsonLocationArray = jsonObject.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#location");

                if (jsonLocationArray.length() > 0) {
                    JSONObject jsonLocationFirstObject = jsonLocationArray.getJSONObject(0);

                    // check if the first item is of type bnode
                    if (jsonLocationFirstObject.getString("type").equals("bnode")) {
                        // search the referenced nodes
                        String referenceNodeName = jsonLocationFirstObject.getString("value");
                        if (completeResponse.has(referenceNodeName)) {
                            JSONObject jsonLocationContent = completeResponse.getJSONObject(referenceNodeName);

                            if (jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#long")) {
                                JSONArray jsonLongArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#long");
                                JSONObject jsonLongObject = jsonLongArray.getJSONObject(jsonLongArray.length() - 1);
                                entryPosLong = jsonLongObject.getString("value");
                            }

                            if (jsonLocationContent.has("http://www.w3.org/2003/01/geo/wgs84_pos#lat")) {
                                JSONArray jsonLatArray = jsonLocationContent.getJSONArray("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
                                JSONObject jsonLatObject = jsonLatArray.getJSONObject(jsonLatArray.length() - 1);
                                entryPosLat = jsonLatObject.getString("value");
                            }
                        }
                    }
                }
            }

            // get description
            if (jsonObject.has("http://purl.org/dc/elements/1.1/description")) {
                JSONArray jsonDescriptionArray = jsonObject.getJSONArray("http://purl.org/dc/elements/1.1/description");
                JSONObject jsonDescriptionObject = jsonDescriptionArray.getJSONObject(jsonDescriptionArray.length() - 1);
                entryDescription = jsonDescriptionObject.getString("value");
            }
        }

        // add entry
        return new LocationItem(
                entryName,
                entryListName,
                entryAddress,
                listOpeningHours,
                entryEmail,
                entryUrl,
                entryPhone,
                entryPosLong,
                entryPosLat,
                entryDescription
        );
    }

    private String extractLocationFromB2(JSONObject b2Object) throws JSONException {

        String address = "";
        if (b2Object.has("http://www.w3.org/2006/vcard/ns#street-address")) {
            JSONArray addressArray = b2Object.getJSONArray("http://www.w3.org/2006/vcard/ns#street-address");
            if (addressArray.length() > 0) {
                address = addressArray.getJSONObject(0).getString("value");
            }
        }

        String locality = "";
        if (b2Object.has("http://www.w3.org/2006/vcard/ns#locality")) {
            JSONArray localityArray = b2Object.getJSONArray("http://www.w3.org/2006/vcard/ns#locality");
            if (localityArray.length() > 0) {
                locality = localityArray.getJSONObject(0).getString("value");
            }
        }

        String postal = "";
        if (b2Object.has("http://www.w3.org/2006/vcard/ns#postal-code")) {
            JSONArray postalArray = b2Object.getJSONArray("http://www.w3.org/2006/vcard/ns#postal-code");
            if (postalArray.length() > 0) {
                postal = postalArray.getJSONObject(0).getString("value");
            }
        }

        String completeAddress = address;

        if (!address.isEmpty()) {
            completeAddress += ", ";
        }

        completeAddress += postal + " " + locality;
        return completeAddress;
    }
}
