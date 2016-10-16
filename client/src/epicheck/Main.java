package epicheck;

import com.mb3364.http.AsyncHttpClient;
import com.mb3364.http.HttpClient;
import com.mb3364.http.HttpResponseHandler;
import com.mb3364.http.RequestParams;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    private static Acr122Device acr122;
    private static boolean connected = false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));

        primaryStage.setTitle("Epicheck");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

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

}