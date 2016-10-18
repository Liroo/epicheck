package epicheck.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import epicheck.utils.ApiRequest;
import epicheck.utils.Preferences;
import epicheck.utils.nfc.TagTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 17/10/2016.
 */
public class ParamsController implements Initializable {

    @FXML
    private static JFXTextField autolinkField;

    @FXML
    private static JFXButton connectBtn;

    @FXML
    private JFXButton applyBtn;

    @FXML
    private static Label noUserLabel;

    @FXML
    private static Label loginLabel;

    @FXML
    private static Label loginTitleLabel;

    @FXML
    private static ImageView userPicture;


    public static void refresh() {
        Platform.runLater(() -> {
            if (epicheck.models.Params.isConnected())
            {
                connectBtn.setText("Lecteur connecté");
            } else {
                connectBtn.setText("Lecteur déconnecté");
            }

            autolinkField.setText(Preferences.get().getAutoLogin());
            userPicture.setVisible(false);
            loginLabel.setVisible(false);
            loginTitleLabel.setVisible(false);
        });

        // Set tag listener
        TagTask.get().setListener(new TagTask.TagListener() {
            @Override
            public void scanCard(JSONObject student) {
                Platform.runLater(() -> {
                    userPicture.setVisible(true);
                    loginLabel.setVisible(true);
                    loginTitleLabel.setVisible(true);
                    noUserLabel.setVisible(false);
                });
                try {
                    String studentEmail = student.getString("email");

                    Platform.runLater(() -> {
                        loginLabel.setText(studentEmail);
                        userPicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".bmp"));
                    });

                } catch (Exception e) {

                }
            }

            @Override
            public void scanError(String error) {
                System.out.println("We handle an error : " + error);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autolinkField.setText(Preferences.get().getAutoLogin());
        userPicture.setVisible(false);
        loginLabel.setVisible(false);
        loginTitleLabel.setVisible(false);

        // Set tag listener
        TagTask.get().setListener(new TagTask.TagListener() {
            @Override
            public void scanCard(JSONObject student) {
                Platform.runLater(() -> {
                    userPicture.setVisible(true);
                    loginLabel.setVisible(true);
                    loginTitleLabel.setVisible(true);
                    noUserLabel.setVisible(false);
                });
                try {
                    String studentEmail = student.getString("email");

                    Platform.runLater(() -> {
                        loginLabel.setText(studentEmail);
                        userPicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".bmp"));
                    });

                } catch (Exception e) {

                }
            }

            @Override
            public void scanError(String error) {
                System.out.println("We handle an error : " + error);
            }
        });
    }

    @FXML
    private void connect() throws Exception {
        if (epicheck.models.Params.isConnected())
        {
            epicheck.models.Params.disconnect();
            connectBtn.setText("Lecteur déconnecté");
        } else {
            if (!epicheck.models.Params.connect())
                connectBtn.setText("Lecteur déconnecté");
            else {
                connectBtn.setText("Lecteur connecté");
            }
        }
    }

    @FXML
    private void applyParams() {
        Preferences.get().setAutoLogin(autolinkField.getText().toString());
    }

}
