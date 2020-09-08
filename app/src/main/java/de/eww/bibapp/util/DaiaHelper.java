package de.eww.bibapp.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.eww.bibapp.R;
import de.eww.bibapp.model.ModsItem;
import de.eww.bibapp.network.model.DaiaItem;
import okhttp3.HttpUrl;

public class DaiaHelper {

    public static String getDaiaUrl(Context context, String ppn, boolean isLocal, String format)
    {
        if (isLocal) {
            String[] localDaiaUrls = context.getResources().getStringArray(R.array.daia_local_urls);
            int localCatalogIndex = PrefUtils.getLocalCatalogIndex(context);


            return String.format(localDaiaUrls[localCatalogIndex], ppn, format);

        } else {
            return String.format(context.getResources().getString(R.string.daia_gvk_url), ppn, format);
        }
    }

    public static String getUriUrl(DaiaItem daiaItem) throws Exception {
        String uriUrl = "";

        // department
        JSONObject departmentObject = daiaItem.getDepartmentObject();
        if (departmentObject != null) {
            if (departmentObject.has("id")) {
                uriUrl = departmentObject.getString("id");
            }
        }

        return uriUrl;
    }

    public static HashMap<String, String> getInformation(DaiaItem daiaItem, ModsItem modsItem, Context context) throws Exception {
        String limitation = "";

        HashMap<String, JSONObject> availableItems = new HashMap<>();
        HashMap<String, JSONObject> unavailableItems = new HashMap<>();

        // available
        JSONArray availableArray = daiaItem.getAvailableItems();
        for (int i=0; i < availableArray.length(); i++) {
            JSONObject availableObject = availableArray.getJSONObject(i);

            String service = availableObject.getString("service");
            availableItems.put(service, availableObject);

            // read limitation only from the "presentation" attribute
            if (service.equals("presentation")) {
                if (availableObject.has("limitation")) {
                    JSONArray limitationArray = availableObject.getJSONArray("limitation");

                    for (int j=0; j < limitationArray.length(); j++) {
                        JSONObject limitationObject = limitationArray.getJSONObject(j);

                        if (limitationObject.has("content")) {
                            String content = limitationObject.getString("content");

                            if (!content.isEmpty()) {
                                limitation = content;
                            }
                        }
                    }
                }
            }
        }

        JSONArray unavailableArray = daiaItem.getUnavailableItems();

        // unavailable
        for (int i=0; i < unavailableArray.length(); i++) {
            JSONObject unavailableObject = unavailableArray.getJSONObject(i);

            String service = unavailableObject.getString("service");
            unavailableItems.put(service, unavailableObject);

            // read limitation only from the "presentation" attribute
            if (service.equals("presentation")) {
                if (unavailableObject.has("limitation")) {
                    JSONArray limitationArray = unavailableObject.getJSONArray("limitation");

                    for (int j=0; j < limitationArray.length(); j++) {
                        JSONObject limitationObject = limitationArray.getJSONObject(j);

                        if (limitationObject.has("content")) {
                            String content = limitationObject.getString("content");

                            if (!content.isEmpty()) {
                                limitation = content;
                            }
                        }
                    }
                }
            }
        }

        String status = "";
        String statusColor = "#000000";
        String statusInfo = "";

        if (availableItems.containsKey("loan")) {
            status += "ausleihbar";
            statusColor = "#007F00";

            if ((availableItems.containsKey("presentation") || availableItems.containsKey("presentation")) && !limitation.isEmpty()) {
                status += "; " + limitation;
            }

            if (availableItems.containsKey("presentation")) {
                // tag available with service="loan" and href=""
                if (availableItems.get("loan").has("href")) {
                    statusInfo += "Bitte bestellen";
                } else {
                    statusInfo += "Bitte am Standort entnehmen";
                }
            }
        } else {
            if (unavailableItems.containsKey("loan") && unavailableItems.get("loan").has("href")) {
                if (unavailableItems.get("loan").getString("href").contains("loan/RES")) {
                    status += "ausleihbar";
                    statusColor = "#FF7F00";
                } else {
                    status += "nicht ausleihbar";
                    statusColor = "#FF0000";
                }
            } else {
                // if this is not an online resource
                if (modsItem.onlineUrl.isEmpty()) {
                    status += "nicht ausleihbar";
                    statusColor = "#FF0000";

                    if (availableItems.containsKey("presentation")) {
                        if (availableItems.get("presentation").has("href")) {
                            String href = availableItems.get("presentation").get("href").toString();
                            HttpUrl hrefUrl = HttpUrl.parse(href);
                            Set<String> queryParameter = hrefUrl.queryParameterNames();
                            if (queryParameter.contains("action")) {
                                List<String> hrefValues = hrefUrl.queryParameterValues("action");
                                if (hrefValues.size() > 0) {
                                    if (hrefValues.get(0).equals("order")) {
                                        status += "; Vor Ort benutzbar, bitte bestellen";
                                    }
                                }
                            }
                        }
                    }
                } else {
                    status += "Online-Ressource im Browser öffnen";
                }
            }

            if ((availableItems.containsKey("presentation") || unavailableItems.containsKey("presentation")) && !limitation.isEmpty()) {
                status += "; " + limitation;
            }

            if (unavailableItems.containsKey("presentation")) {
                if ( unavailableItems.get("loan").has("href") ){
                    if (unavailableItems.get("loan").getString("href").contains("loan/RES")) {
                        if (!unavailableItems.get("loan").has("expected") || unavailableItems.get("loan").getString("expected").equals("unknown")) {
                            statusInfo += "ausgeliehen, Vormerken möglich";
                        } else {
                            String dateString = unavailableItems.get("loan").getString("expected");
                            SimpleDateFormat simpleDateFormat;

                            if (dateString.substring(2, 3).equals("-") && dateString.substring(5, 6).equals("-")) {
                                simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.GERMANY);
                            } else {
                                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY);
                            }

                            try {
                                Date date = simpleDateFormat.parse(dateString);

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
                                statusInfo += "ausgeliehen bis " + dateFormat.format(date) + ", Vormerken möglich";
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    statusInfo += "...";
                }
            }
        }

        // Actions
        String actions = "";
        if (availableItems.containsKey("loan")) {
            if (availableItems.containsKey("presentation")) {
                if (availableItems.get("loan").has("href")) {
                    actions = "order";

                    String href = availableItems.get("loan").get("href").toString();
                    HttpUrl hrefUrl = HttpUrl.parse(href);
                    Set<String> queryParameter = hrefUrl.queryParameterNames();
                    if (queryParameter.contains("bar")) {
                        List<String> hrefValues = hrefUrl.queryParameterValues("bar");
                        if (hrefValues.size() > 0) {
                            if (hrefValues.get(0).isEmpty()) {
                                actions = "no_barcode_reset";
                            }
                        }
                    }
                }
            }
        } else {
            if (unavailableItems.containsKey("presentation")) {
                if (unavailableItems.get("loan").has("href")) {
                    actions = "request";
                }
            } else {
                if (availableItems.containsKey("presentation")) {
                    if (availableItems.get("presentation").has("href")) {
                        String href = availableItems.get("presentation").get("href").toString();
                        HttpUrl hrefUrl = HttpUrl.parse(href);
                        Set<String> queryParameter = hrefUrl.queryParameterNames();
                        if (queryParameter.contains("action")) {
                            List<String> hrefValues = hrefUrl.queryParameterValues("action");
                            if (hrefValues.size() > 0) {
                                if (hrefValues.get(0).equals("order")) {
                                    actions = "order";
                                }
                            }
                        }
                    }
                }
            }
        }

        if (    (availableItems.containsKey("loan") && availableItems.get("loan").has("href") && !availableItems.get("loan").getString("href").isEmpty()) ||
                (unavailableItems.containsKey("loan") && unavailableItems.get("loan").has("href") && !unavailableItems.get("loan").getString("href").isEmpty())) {
            actions += ";location";
        } else {
            // fix for crash when tryining to access a location entry that does not exists
            // the default actions depends on the existence of a uri entry
            String uriUrl = DaiaHelper.getUriUrl(daiaItem);
            if (!uriUrl.isEmpty() && actions.isEmpty()) {
                actions = "location";
            }
        }

        HashMap<String, String> information = new HashMap<>();
        information.put("status", status);
        information.put("statusColor", statusColor);
        information.put("statusInfo", statusInfo);
        information.put("actions", actions);

        return information;
    }
}
