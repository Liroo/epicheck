package epicheck.utils;

import com.mb3364.http.AsyncHttpClient;
import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;

/**
 * Created by jean on 16/10/16.
 */
public class ApiUtils {
    public interface ApiVars {
        String apiUrl = "http://10.10.251.153:3000/"; // http://10.10.253.62:3000
        String intraUrl = "https://intra.epitech.eu/";
    }
    public enum RequestType {
        GET,
        POST,
        DELETE
    }
    private static ApiUtils self = null;
    private AsyncHttpClient client;


    private ApiUtils() {
        client = new AsyncHttpClient();
        client.setHeader("Accept", "application/*");
        client.setUserAgent("epicheck-java");
    }

    public static ApiUtils get() {
        if (self == null)
            self = new ApiUtils();
        return (self);
    }

    public void exec(RequestType type, String url, HttpResponseHandler handler) {
        switch (type) {
            case GET:
                client.get(url, handler);
                break;
            case POST:
                client.post(url, handler);
                break;
            case DELETE:
                client.delete(url, handler);
                break;
            default:
                break;
        }
    }

    public void exec(RequestType type, String url, RequestParams params, HttpResponseHandler handler) {
        switch (type) {
            case GET:
                client.get(url, params, handler);
                break;
            case POST:
                client.post(url, params, handler);
                break;
            case DELETE:
                client.delete(url, params, handler);
        }
    }
}
