package epicheck;

import com.mb3364.http.AsyncHttpClient;
import com.mb3364.http.HttpClient;
import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import epicheck.utils.ApiRequest;
import epicheck.utils.ApiRequest.JSONArrayListener;
import epicheck.utils.Preferences;
import epicheck.utils.nfc.Acr122Device;
import epicheck.utils.nfc.TagTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    private static Acr122Device acr122;
    private static boolean connected = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("views/home.fxml"));

        primaryStage.setTitle("Epicheck");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        TrustAllHttpsDomain();
        Preferences.get().setAutoLogin("auth-7a83b1dd2a2de287c8b66e89bdb68a9aaf48a773");
        ApiRequest.get().getActivitiesFromIntra("2016-10-17", "2016-10-22", new JSONArrayListener() {

            @Override
            public void onComplete(JSONArray res) {
                System.out.println(res);
            }

            @Override
            public void onFailure(String err) {

            }
        });

        ApiRequest.get().getStudents(new JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                System.out.println(res);
            }

            @Override
            public void onFailure(String err) {

            }
        });

        // Set logger
        org.apache.log4j.BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        // Set tag listener
        TagTask.get().setListener(new TagTask.TagListener() {
            @Override
            public void scanCard(String tag) {
                System.out.println("Card ID = " + tag);
                RequestParams params = new RequestParams();
                params.put("id", tag + "9000");

                HttpClient client = new AsyncHttpClient();
                client.post("http://localhost:3000/students/get", params, new HttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                        System.out.println("response = " + new String(bytes));
                    }

                    @Override
                    public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                        System.out.println("response error = " + new String(bytes));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("failure");
                    }
                });
            }

            @Override
            public void scanError(String error) {
                System.out.println("We handle an error : " + error);
            }
        });
    }

    public static boolean connect() throws Exception
    {
        try {
            acr122 = new Acr122Device();
        } catch (RuntimeException re) {
            connected = false;
            return false;
        }
        acr122.open();
        connected = true;
        return true;
    }

    public static boolean isConnected() {
        return (connected);
    }

    public static void disconnect() throws IOException {
        acr122.close();
        connected = false;
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void TrustAllHttpsDomain() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}