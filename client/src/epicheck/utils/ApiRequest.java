package epicheck.utils;

import com.mb3364.http.HttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by jean on 16/10/16.
 */
public class ApiRequest {
    private static ApiRequest self = null;

    public interface JSONArrayListener {
        void onComplete(JSONArray res);
        void onFailure(String err);
    }

    public interface JSONObjectListener {
        void onComplete(JSONObject res);
        void onFailure(String err);
    }

    private ApiRequest() {}

    public static ApiRequest get() {
        if (self == null)
            self = new ApiRequest();
        return (self);
    }

    public void getActivitiesFromIntra(String from, String to, JSONArrayListener call) {
        // format de from et to : yyyy-mm-dd
        String URL = "https://intra.epitech.eu/" + Preferences.get().getAutoLogin() + "/planning/load?format=json&start=" + from + "&end=" + to;
        System.out.println("URL = " + URL);
        ApiUtils.get().exec(ApiUtils.RequestType.GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONArray ret = new JSONArray(new String(bytes));
                    call.onComplete(ret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                call.onFailure(new String(bytes));
            }

            @Override
            public void onFailure(Throwable throwable) {
                call.onFailure("Connection interrupted");
            }
        });
    }

    public void getStudents(JSONArrayListener call) {
        String URL = "http://localhost:3000/students";
        ApiUtils.get().exec(ApiUtils.RequestType.GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    JSONArray ret = obj.getJSONArray("students");
                    call.onComplete(ret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                call.onFailure(new String(bytes));
            }

            @Override
            public void onFailure(Throwable throwable) {
                call.onFailure("Connection interrupted");
            }
        });
    }

}
