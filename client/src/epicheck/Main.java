package epicheck;

import epicheck.controllers.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class Main extends Application {

    public static Stage primaryStage;
    public static MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Set logger
        org.apache.log4j.BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.DEBUG);
        System.setProperty( "file.encoding", "UTF-8" );


        this.primaryStage = primaryStage;
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Bold.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Light.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Heavy.ttf").toExternalForm(), 15);
        javafx.scene.text.Font.loadFont(Main.class.getResource("/resources/fonts/SFUIText-Regular.ttf").toExternalForm(), 15);

        TrustAllHttpsDomain();

        Platform.setImplicitExit(false);
        FXMLLoader load = new FXMLLoader(getClass().getResource("/epicheck/views/home.fxml"));
        Parent root = load.load();
        mainController = load.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

        primaryStage.setTitle("Epicheck");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void TrustAllHttpsDomain() throws NoSuchAlgorithmException, KeyManagementException {

       TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
       };

       try {
           SSLContext sc = SSLContext.getInstance("SSL");
           sc.init(null, trustAllCerts, new java.security.SecureRandom());
           HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
       } catch (GeneralSecurityException e) {
       }
    }

}
