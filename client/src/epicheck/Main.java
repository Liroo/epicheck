package epicheck;

import epicheck.utils.ApiRequest;
import epicheck.utils.ApiRequest.JSONArrayListener;
import epicheck.utils.Preferences;
import epicheck.utils.nfc.Acr122Device;
import epicheck.utils.nfc.TagTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.awt.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {

    private static Acr122Device acr122;
    private static boolean connected = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Platform.setImplicitExit(false);
        Parent root = FXMLLoader.load(getClass().getResource("views/home.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

        primaryStage.setTitle("Epicheck");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        TrustAllHttpsDomain();
        ApiRequest.get().getActivitiesFromIntra("2016-10-17", "2016-10-22", new JSONArrayListener() {

            @Override
            public void onComplete(JSONArray res) {
                System.out.println(res);
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = [" + err + "]");
            }
        });

        ApiRequest.get().getStudents(new JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                System.out.println(res);
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = [" + err + "]");
            }
        });

        // Set logger
        org.apache.log4j.BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
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
       HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
    }

}
