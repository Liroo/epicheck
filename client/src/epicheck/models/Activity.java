package epicheck.models;

import epicheck.utils.ApiRequest;
import epicheck.utils.ApiRequest.JSONArrayListener;
import org.json.JSONArray;

/**
 * Created by Kevin on 16/10/2016.
 */
public class Activity {

    public static void getAllActivities(JSONArrayListener call) {
        ApiRequest.get().getActivitiesFromIntra("2016-10-17", "2016-10-22", call);
    }
}
