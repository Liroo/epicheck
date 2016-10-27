package epicheck.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import epicheck.utils.ApiRequest;
import epicheck.utils.MailUtils;
import epicheck.utils.Preferences;
import epicheck.utils.nfc.TagTask;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 17/10/2016.
 */
public class ParamsController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private JFXTextField autolinkField;

    @FXML
    private JFXButton connectBtn;

    @FXML
    private JFXButton applyBtn;

    @FXML
    private Label noUserLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private Label loginTitleLabel;

    @FXML
    private JFXTextArea txtarea_email_list;

    @FXML
    private JFXButton btn_test_mail;

    @FXML
    private ImageView userPicture;


    public void refresh() {
        Platform.runLater(() -> {
            if (epicheck.models.Params.isConnected())
            {
                connectBtn.setText("Lecteur connecté");
            } else {
                connectBtn.setText("Lecteur déconnecté");
            }

            autolinkField.setText(Preferences.get().getAutoLogin());
            txtarea_email_list.setText(Preferences.get().getEmailList());
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
        txtarea_email_list.setText(Preferences.get().getEmailList());
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
    private void testEmail() {
        if (txtarea_email_list.isDisabled()) {
            System.out.println("txtarea disabled");
            return;
        }
        Platform.runLater(() -> {
            btn_test_mail.setText("Sending mail to list...");
            txtarea_email_list.setDisable(true);
        });
        ArrayList<String> emails = new ArrayList<>();
        Collections.addAll(emails, txtarea_email_list.getText().split("\\n"));
        Platform.runLater(() -> MailUtils.send(new ApiRequest.StringListener() {
            @Override
            public void onComplete(String res) {
                Platform.runLater(() -> {
                    new JFXSnackbar(rootPane).show("Email envoyé", 1500);
                    btn_test_mail.setText("Envoyer un e-mail de test");
                    txtarea_email_list.setDisable(false);
                });
            }

            @Override
            public void onFailure(String err) {
                Platform.runLater(() -> {
                    new JFXSnackbar(rootPane).show("Une erreur s'est produite", 1500);
                    btn_test_mail.setText("Envoyer un e-mail de test");
                    txtarea_email_list.setDisable(false);
                });
            }
        }, "Epicheck: email de test", "Ceci est un e-mail automatique de test.\nMerci de ne pas répondre.", emails));
    }

    @FXML
    private void applyParams() {
        Preferences.get().setAutoLogin(autolinkField.getText().toString());
        Preferences.get().setEmailList(txtarea_email_list.getText().toString());
        Platform.runLater(() -> new JFXSnackbar(rootPane).show("Parameters saved", 1500));
    }
}
