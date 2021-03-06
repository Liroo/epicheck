package epicheck.utils;

import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import epicheck.apimodels.Student;
import epicheck.utils.ApiUtils.ApiVars;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static epicheck.utils.ApiUtils.RequestType.DELETE;
import static epicheck.utils.ApiUtils.RequestType.GET;
import static epicheck.utils.ApiUtils.RequestType.POST;

/**
 * Created by jean on 16/10/16.
 */
public class ApiRequest implements ApiVars {
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
     * Intra Student marks
     */

    public void getStudentMarks(Student student, JSONArrayListener call) {
        String URL = intraUrl + Preferences.get().getAutoLogin() + "/user/" + student.getEmail().get() + "/notes/?format=json";
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONObject obj = new JSONObject(new String(bytes));
                    JSONArray ret = obj.getJSONArray("notes");
                    JSONArray newJsonArray = new JSONArray();
                    for (int j = ret.length()-1; j>=0; j--) {
                        newJsonArray.put(ret.get(j));
                    }
                    call.onComplete(newJsonArray);
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
     * Intra Activities
     */

    public void getActivitiesFromIntra(String from, String to, JSONArrayListener call) {
        String URL = intraUrl + Preferences.get().getAutoLogin() + "/planning/load?format=json&start=" + from + "&end=" + to;
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    JSONArray ret = new JSONArray(new String(bytes));
                    JSONArray sel = new JSONArray();
                    for (int j = 0; j < ret.length(); j++) {
                        JSONObject obj = ret.getJSONObject(j);
                        if (obj.has("acti_title")) {
                            sel.put(obj);
                        }
                    }
                    call.onComplete(sel);
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
        String URL = apiUrl + "activities";

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

    private String dateFormatIntra(String date) {
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            DateFormat extern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date_ret = format.parse(date);
            return (extern.format(date_ret));
        } catch (ParseException e) {
            return ("");
        }
    }



    public void addActivity(String actiTitle, String dateFrom, String dateTo, String moduleTitle, String scholarYear, String codeModule, String codeInstance,
                            String codeActi, String codeEvent, JSONArray students, JSONObjectListener call) {
        String URL = apiUrl + "activities/add";
        RequestParams params = new RequestParams();
        params.put("actiTitle", actiTitle);
        params.put("dateFrom", dateFormatIntra(dateFrom));
        params.put("dateTo", dateFormatIntra(dateTo));
        params.put("moduleTitle", moduleTitle);
        params.put("scholarYear", scholarYear);
        params.put("codeModule", codeModule);
        params.put("codeInstance", codeInstance);
        params.put("codeActi", codeActi);
        params.put("codeEvent", codeEvent);
        for (int i = 0; i < students.length(); i++) {
            try {
                params.put("students[" + i + "][login]", students.getJSONObject(i).getString("login"));
                params.put("students[" + i + "][date]", students.getJSONObject(i).getString("date_modif"));
                params.put("students[" + i + "][present]", students.getJSONObject(i).getString("present"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public void getActivity(String codeActi, String codeEvent, JSONObjectListener call) {
        String URL = apiUrl + "activities/get";
        RequestParams params = new RequestParams();
        params.put("codeActi", codeActi);
        params.put("codeEvent", codeEvent);
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
        String URL = apiUrl + "activities/" + id;
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
        String URL = apiUrl + "check";
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
        String URL = apiUrl + "check";
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

    public void deleteCheck(String studentId, String activityId, JSONObjectListener call) {
        String URL = apiUrl + "check/" + studentId + "/" + activityId;
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
        String URL = apiUrl + "students";
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    call.onComplete(new JSONArray(new String(bytes)));
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
        String URL = apiUrl + "students/add";
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
        String URL = apiUrl + "students/" + email;
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
        String URL = apiUrl + "students/get";
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


    public void getStudentByEmail(String email, JSONObjectListener call) {
        String URL = apiUrl + "students/get/" + email;
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                try {
                    call.onComplete(new JSONObject(new String(bytes)));
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
