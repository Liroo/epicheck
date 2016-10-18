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
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.awt.*;
import java.awt.Font;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Bold.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Light.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Heavy.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Regular.ttf").toExternalForm(), 15);


        Platform.setImplicitExit(false);
        Parent root = FXMLLoader.load(getClass().getResource("views/home.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

        primaryStage.setTitle("Epicheck");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        TrustAllHttpsDomain();

        // Set logger
        //org.apache.log4j.BasicConfigurator.configure();
        //Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void TrustAllHttpsDomain() throws NoSuchAlgorithmException, KeyManagementException {
       HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);
    }

}
