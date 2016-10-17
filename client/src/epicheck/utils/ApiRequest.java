package epicheck.utils;

import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import static epicheck.utils.ApiUtils.RequestType.DELETE;
import static epicheck.utils.ApiUtils.RequestType.GET;
import static epicheck.utils.ApiUtils.RequestType.POST;

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

    public interface StringListener {
        void onComplete(String res);
        void onFailure(String err);
    }

    private ApiRequest() {}

    public static ApiRequest get() {
        if (self == null)
            self = new ApiRequest();
        return (self);
    }

    /**
     * Intra Activities
     */

    public void getActivitiesFromIntra(String from, String to, JSONArrayListener call) {
        // format de from et to : yyyy-mm-dd
        String URL = "https://intra.epitech.eu/" + Preferences.get().getAutoLogin() + "/planning/load?format=json&start=" + from + "&end=" + to;
        System.out.println("URL = " + URL);
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
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

    /**
     *  Activities
     */

    public void getActivities(JSONArrayListener call) {
        String URL = "http://localhost:3000/activities";
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    JSONArray ret = obj.getJSONArray("activities");
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

    public void addActivity(String actiTitle, String dateFrom, String dateTo, String moduleTitle, String scholarYear, String codeModule, String codeInstance,
                            String codeActi, String codeEvent, JSONArray students, JSONObjectListener call) {
        String URL = "http://localhost:3000/activities/add";
        RequestParams params = new RequestParams();
        params.put("actiTitle", actiTitle);
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);
        params.put("moduleTitle", moduleTitle);
        params.put("scholarYear", scholarYear);
        params.put("codeModule", codeModule);
        params.put("codeInstance", codeInstance);
        params.put("codeActi", codeActi);
        params.put("codeEvent", codeEvent);
        params.put("students", String.valueOf(students));
        ApiUtils.get().exec(POST, URL, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    public void deleteActivity(String id, JSONObjectListener call) {
        String URL = "http://localhost:3000/activities/" + id;
        ApiUtils.get().exec(DELETE, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    /**
     * Validation
     */

    public void getChecks(JSONArrayListener call) {
        String URL = "http://localhost:3000/check";
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    JSONArray ret = obj.getJSONArray("presences");
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

    public void addCheck(String activityId, String email, JSONObjectListener call) {
        String URL = "http://localhost:3000/check";
        RequestParams params = new RequestParams();
        params.put("id", activityId);
        params.put("email", email);
        ApiUtils.get().exec(POST, URL, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    public void deleteCheck(String id, JSONObjectListener call) {
        String URL = "http://localhost:3000/check/" + id;
        ApiUtils.get().exec(DELETE, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    /**
     * Students
     */

    public void getStudents(JSONArrayListener call) {
        String URL = "http://localhost:3000/students";
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
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

    public void addStudent(String email, JSONArray activities, JSONObjectListener call) {
        String URL = "http://localhost:3000/students/add";
        RequestParams params = new RequestParams();
        params.put("activities", String.valueOf(activities));
        params.put("email", email);
        ApiUtils.get().exec(POST, URL, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    public void deleteStudent(String email, JSONObjectListener call) {
        String URL = "http://localhost:3000/students/" + email;
        ApiUtils.get().exec(DELETE, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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

    public void getStudent(String tag, JSONObjectListener call) {
        String URL = "http://localhost:3000/students/get";
        RequestParams params = new RequestParams();
        params.put("id", tag);

        ApiUtils.get().exec(POST, URL, params, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject ret = new JSONObject(new String(bytes));
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
