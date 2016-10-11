package epicheck;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Main extends Application {

    private static Acr122Device acr122;
    private static boolean connected = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

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

}
