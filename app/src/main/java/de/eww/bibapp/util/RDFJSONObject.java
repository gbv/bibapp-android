package de.eww.bibapp.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Wraps a JSONObject containing RDF/JSON.
 *
 * See http://www.w3.org/TR/rdf-json/ for definition of RDF/JSON.
 *
 * <pre>{@code
 * JSONObject rdfjson = new JSONObject(json);
 *
 * List<String> names = rdfjson.getAllObjectValues(
 *      "http://example.org/",                      // subject URI
 *      "http://xmlns.com/foaf/0.1/name"            // predicate URI
 * ); // list may be empty
 *
 * String lat = rdfjson.getObjectValue(
 *      "http://example.org/", 
 *      "http://www.w3.org/2003/01/geo/wgs84_pos#lat"
 * ); // may be null

 * String uri_or_bnode = rdfjson.getObjectKey(
 *      "http://example.org/",                      // subject URI
 *      "http://www.w3.org/2006/vcard/ns#url"       // predicate URI
 * ); // may be null
 *
 * String lat = rdfjson.getObjectValue(
 *      uri_or_bnode, 
 *      "http://www.w3.org/2003/01/geo/wgs84_pos#lat"
 * ); // may be null
 *
 * }</pre>
 */
public class RDFJSONObject {
    private JSONObject json;
    
    public RDFJSONObject(JSONObject json) {
        this.json = json;
    }

    /**
     * Get a list of object values with given subject and predicate.
     * 
     * Returns an empty list if no matching objects have been found.
     */
    public ArrayList<String> getAllObjectValues(String subject, String predicate) {
        ArrayList<String> values = new ArrayList<String>();
        try {
            JSONObject s = this.json.getJSONObject(subject);
            JSONArray objects = s.getJSONArray(predicate);
            for (int i = 0; i < objects.length(); i++) {
                values.add( objects.getJSONObject(i).getString("value") );
            }
        } catch(JSONException e) {
        }
        return values;
    }

    /**
     * Get some object value with given subject and predicate (or null).
     */
    public String getObjectValue(String subject, String predicate) {
        try {
            JSONObject s = this.json.getJSONObject(subject);
            JSONObject firstObject = s.getJSONArray(predicate).getJSONObject(0);
            return firstObject.getString("value");
        } catch(JSONException e) {
        }
        return null;
    }

    /**
     * Get some object URI or bnode value with given subject and predicate (or null).
     */
    public String getObjectKey(String subject, String predicate) {
        try {
            JSONObject s = this.json.getJSONObject(subject);
            JSONObject firstObject = s.getJSONArray(predicate).getJSONObject(0);
            String type = firstObject.getString("type");
            if ( type.equals("bnode") || type.equals("uri") ) {
                return firstObject.getString("value");
            }
        } catch(JSONException e) {
        }
        return null;
    }

}


