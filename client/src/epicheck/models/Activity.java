package epicheck.models;

import epicheck.utils.ApiRequest;
import org.json.JSONArray;

/**
 * Created by Kevin on 16/10/2016.
 */
public class Activity {

    public static JSONArray getAllActivities() {
        ApiRequest.get().getActivitiesFromIntra("2016-10-17", "2016-10-22", new ApiRequest.JSONArrayListener() {

            @Override
            public void onComplete(JSONArray res) {
                System.out.println(res);
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = [" + err + "]");
            }
        });
        return new JSONArray();
    }
}
