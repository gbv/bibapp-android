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
import de.eww.bibapp.network.model.ModsItem;
import de.eww.bibapp.network.model.DaiaItem;
import de.eww.bibapp.network.model.daia.DaiaAvailable;
import de.eww.bibapp.network.model.daia.DaiaEntity;
import de.eww.bibapp.network.model.daia.DaiaUnavailable;
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
        DaiaEntity department = daiaItem.getDepartmentEntity();
        if (department != null) {
            if (department.getId() != null) {
                uriUrl = department.getId();
            }
        }

        return uriUrl;
    }

    public static HashMap<String, String> getInformation(DaiaItem daiaItem, ModsItem modsItem, Context context) throws Exception {
        String presentationLimitation = "";

        HashMap<String, DaiaAvailable> availableItems = new HashMap<>();
        HashMap<String, DaiaUnavailable> unavailableItems = new HashMap<>();

        // available
        for (DaiaAvailable available: daiaItem.getAvailables()) {
            String service = available.getService();
            availableItems.put(service, available);

            // read limitation only from the "presentation" attribute
            if (service.equals("presentation")) {
                for (DaiaEntity limitation: available.getLimitations()) {
                    if (limitation.getContent() != null && !limitation.getContent().isEmpty()) {
                        presentationLimitation = limitation.getContent();
                    }
                }
            }
        }
        // unavailable
        for (DaiaUnavailable unavailable: daiaItem.getUnavailables()) {
            String service = unavailable.getService();
            unavailableItems.put(service, unavailable);

            // read limitation only from the "presentation" attribute
            if (service.equals("presentation")) {
                for (DaiaEntity limitation: unavailable.getLimitations()) {
                    if (limitation.getContent() != null && !limitation.getContent().isEmpty()) {
                        presentationLimitation = limitation.getContent();
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

            if ((availableItems.containsKey("presentation") || availableItems.containsKey("presentation")) && !presentationLimitation.isEmpty()) {
                status += "; " + presentationLimitation;
            }

            if (availableItems.containsKey("presentation")) {
                // tag available with service="loan" and href=""
                if (availableItems.get("loan").getHref() != null) {
                    statusInfo += "Bitte bestellen";
                } else {
                    statusInfo += "Bitte am Standort entnehmen";
                }
            }
        } else {
            if (unavailableItems.containsKey("loan") && unavailableItems.get("loan").getHref() != null) {
                status += "ausleihbar";
                statusColor = "#FF7F00";
            } else {
                // if this is not an online resource
                if (modsItem.onlineUrl.isEmpty()) {
                    status += "nicht ausleihbar";
                    statusColor = "#FF0000";

                    if (availableItems.containsKey("presentation")) {
                        if (availableItems.get("presentation").getHref() != null) {
                            String href = availableItems.get("presentation").getHref();
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

            if ((availableItems.containsKey("presentation") || unavailableItems.containsKey("presentation")) && !presentationLimitation.isEmpty()) {
                status += "; " + presentationLimitation;
            }

            if (unavailableItems.containsKey("presentation")) {
                if (unavailableItems.get("loan").getHref() != null){
                    if (unavailableItems.get("loan").getExpected() == null || unavailableItems.get("loan").getExpected().equals("unknown")) {
                        statusInfo += "ausgeliehen, Vormerken möglich";
                    } else {
                        String dateString = unavailableItems.get("loan").getExpected();
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
                } else {
                    statusInfo += "...";
                }
            }
        }

        // Actions
        String actions = "";
        if (availableItems.containsKey("loan")) {
            if (availableItems.containsKey("presentation")) {
                if (availableItems.get("loan").getHref() != null) {
                    actions = "order";

                    String href = availableItems.get("loan").getHref();
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
                if (unavailableItems.get("loan").getHref() != null) {
                    actions = "request";
                }
            } else {
                if (availableItems.containsKey("presentation")) {
                    if (availableItems.get("presentation").getHref() != null) {
                        String href = availableItems.get("presentation").getHref();
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

        if (    (availableItems.containsKey("loan") && availableItems.get("loan").getHref() != null && !availableItems.get("loan").getHref().isEmpty()) ||
                (unavailableItems.containsKey("loan") && unavailableItems.get("loan").getHref() != null && !unavailableItems.get("loan").getHref().isEmpty())) {
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
