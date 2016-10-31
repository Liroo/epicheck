package epicheck.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import epicheck.models.Params;
import epicheck.utils.ApiRequest;
import epicheck.utils.MailUtils;
import epicheck.utils.Preferences;
import epicheck.utils.nfc.TagTask;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by jean on 18/10/16.
 */
public class SessionController extends AbstractSession implements Initializable {
    private Activity activity;
    private JSONObject activity_json;

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTreeTableView table;

    @FXML
    private ImageView img_stud;

    @FXML
    private Label lbl_email;

    @FXML
    private Label lbl_date;

    @FXML
    private JFXButton btn_connect;

    @FXML
    private JFXCheckBox chkbox_email;

    private ObservableList<Student> students;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            final Circle clip = new Circle(147.5D, 175, 147.5D);
            img_stud.setVisible(true);
            img_stud.setClip(clip);
            img_stud.setImage(new Image(getClass().getResource("/resources/images/default_student.jpg").toExternalForm()));
            img_stud.setSmooth(true);
            lbl_email.setVisible(false);
            lbl_date.setVisible(false);
            if (Params.isConnected()) {
                new JFXSnackbar(root).show("Lecteur connecté", 2000);
                btn_connect.setText("Déconnecter le lecteur");
            } else {
                new JFXSnackbar(root).show("Lecteur déconnecté", 2000);
                btn_connect.setText("Connecter le lecteur");
            }
        });

        TagTask.get().setListener(new TagTask.TagListener() {
            @Override
            public void scanCard(JSONObject student) {
                try {

                    String studentEmail = student.getString("email");
                    Platform.runLater(() -> {
                        lbl_email.setText(studentEmail);
                        Date now = new Date();
                        DateFormat extern = new SimpleDateFormat("HH:mm");
                        lbl_date.setText("Validation : " + extern.format(now));
                        img_stud.setImage(new Image("https://cdn.local.epitech.eu/userprofil/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".bmp"));
                        img_stud.setVisible(true);
                        lbl_email.setVisible(true);
                        lbl_date.setVisible(true);
                    });

                    ApiRequest.get().addCheck(activity_json.getString("_id"), studentEmail, new ApiRequest.JSONObjectListener() {
                        @Override
                        public void onComplete(JSONObject res) {
                            for(int i = 0; i < students.size(); i++) {
                                if (students.get(i).getEmail().get().equals(studentEmail))
                                    students.get(i).setDate();
                            }

                            activity.forcePresenceUser(studentEmail, "present", new ApiRequest.JSONObjectListener() {
                                @Override
                                public void onComplete(JSONObject res) {
                                    Platform.runLater(() -> new JFXSnackbar(root).show(studentEmail + " has been set present on the intranet", 2000));
                                }

                                @Override
                                public void onFailure(String err) {
                                    Platform.runLater(() -> new JFXSnackbar(root).show("An error occured while setting " + studentEmail + " present", 2000));
                                }
                            });

                            Platform.runLater(() -> {
                                TreeItem<Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                                table.getRoot().getChildren().clear();
                                table.getRoot().getChildren().addAll(root);
                                table.setRoot(root);
                            });
                        }

                        @Override
                        public void onFailure(String err) {
                            try {
                                JSONObject res = new JSONObject(err);
                                String errmsg = res.getString("message");
                                Platform.runLater(() -> new JFXSnackbar(root).show(errmsg, 3000));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void scanError(String error) {
                System.out.println("error = [" + error + "]");
                Platform.runLater(() -> new JFXSnackbar(root).show("Please, scan card again", 3000));
            }
        });
    }

    public void deleteEntry() throws JSONException {
        JSONArray studs = activity_json.getJSONArray("students");
        if (table.getSelectionModel().isEmpty())
            return ;
        RecursiveTreeItem select = (RecursiveTreeItem) table.getSelectionModel().getSelectedItem();
        Student stud_selected = (Student) select.getValue();

        for (int i = 0; i < studs.length(); i++) {
            JSONObject student = studs.getJSONObject(i);
            if (student.getString("email").equals(stud_selected.getEmail().get()))
            {
                ApiRequest.get().deleteCheck(student.getString("_id"), activity_json.getString("_id"), new ApiRequest.JSONObjectListener() {
                    @Override
                    public void onComplete(JSONObject res) {
                        Platform.runLater(() -> {
                            for(int i1 = 0; i1 < students.size(); i1++) {
                                if (students.get(i1).getEmail().get().equals(stud_selected.getEmail().get()))
                                    students.get(i1).setDate("null");
                            }

                            activity.forcePresenceUser(stud_selected.getEmail().get(), "N/A", new ApiRequest.JSONObjectListener() {
                                @Override
                                public void onComplete(JSONObject res) {
                                    Platform.runLater(() -> new JFXSnackbar(root).show(stud_selected.getEmail().get() + " has been set N/A on the intranet", 2000));
                                }

                                @Override
                                public void onFailure(String err) {
                                    Platform.runLater(() -> new JFXSnackbar(root).show("An error occured while setting " + stud_selected.getEmail().get() + " N/A", 2000));
                                }
                            });


                            Platform.runLater(() -> {
                                TreeItem<Student> root1 = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                                table.getRoot().getChildren().clear();
                                table.getRoot().getChildren().addAll(root1);
                                table.setRoot(root1);
                            });
                        });
                    }

                    @Override
                    public void onFailure(String err) {

                    }
                });
            }
        }
    }

    public void setParams(Activity activity, JSONObject activity_json) {
        this.activity = activity;
        this.activity_json = activity_json;

        JFXTreeTableColumn<Student, String> email = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<Student, String> check = new JFXTreeTableColumn<>("Validation");

        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        check.setCellValueFactory(param -> param.getValue().getValue().getDate());

        email.setEditable(true);
        check.setEditable(true);

        email.setMaxWidth(260);
        check.setMinWidth(200);

        students = FXCollections.observableArrayList();

        try {
            JSONArray studs = activity_json.getJSONArray("students");

            for(int i = 0; i < studs.length(); i++) {
                JSONObject stud = studs.getJSONObject(i);
                students.add(new Student(stud.getString("email"), stud.getJSONObject("presence").getString("date")));
            }

            Platform.runLater(() -> {
                TreeItem<Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                table.getColumns().setAll(email, check);
                table.setRoot(root);
                table.setShowRoot(false);
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws Exception {
        if (epicheck.models.Params.isConnected())
        {
            epicheck.models.Params.disconnect();
            new JFXSnackbar(root).show("Lecteur déconnecté", "CONNECTER", 5000, mouseEvent -> {
                try {
                    connect();
                } catch (Exception e) {}
            });
            btn_connect.setText("Connecter le lecteur");
        } else {
            if (!epicheck.models.Params.connect()) {
                new JFXSnackbar(root).show("Lecteur non trouvé", "CONNECTER", 5000, mouseEvent -> {
                    try {
                        connect();
                    } catch (Exception e) {}
                });
                btn_connect.setText("Connecter le lecteur");
            }
            else {
                new JFXSnackbar(root).show("Lecteur connecté", "DÉCONNECTER", 5000, mouseEvent -> {
                    try {
                        connect();
                    } catch (Exception e) {}
                });
                btn_connect.setText("Déconnecter le lecteur");
            }
        }
    }
    
    @FXML
    private void endSession() {

        if (chkbox_email.isSelected()) {
            Preferences.get().getEmailList();
            ArrayList<String> emails = new ArrayList<>();
            Collections.addAll(emails, Preferences.get().getEmailList().split("\\n"));

            ArrayList<String> abs_students = new ArrayList<>();

            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getDate().get().length() == 0)
                    abs_students.add(students.get(i).getEmail().get());
            }

            String content_master = "Bonjour,\n\nVoici la liste des étudiants absents à l'activité : " + activity.getActiTitle().get() + ".\n\n";

            for (int i = 0; i < abs_students.size(); i++)
                content_master += "- " + abs_students.get(i) + "\n";
            if (abs_students.size() == 0)
                content_master += "Aucun étudiant absent.\n";
            content_master += "\n\n##########\nCeci est un message automatique, merci de ne pas répondre.";

            // TODO: 10/29/16 end session, check if mails is checked ans send mails or not. close the window afterthat
            MailUtils.send(new ApiRequest.StringListener() {
                @Override
                public void onComplete(String res) {
                    String student_content = "Bonjour,\n\nVous n'avez pas validé votre présence à l'activité " + activity.getActiTitle().get() + ".\n\nRapprochez vous de la pédagogie pour justifier cette absence.\n";
                    student_content += "\n\n##########\nCeci est un message automatique, merci de ne pas répondre.";
                    for (int i = 0; i < abs_students.size(); i++) {
                        final int cur_pos = i;
                        MailUtils.send(new ApiRequest.StringListener() {
                            @Override
                            public void onComplete(String res) {}

                            @Override
                            public void onFailure(String err) {
                            }
                        }, "[EPICHECK]Absence activité", student_content, abs_students.get(i));
                    }
                }

                @Override
                public void onFailure(String err) {
                    Platform.runLater(() -> new JFXSnackbar(root).show("Echec lors de l'envoi du mail d'absence.", 2000));
                }
            }, "[EPICHECK]Liste des étudiants absents", content_master, emails);
        }

        Stage stage = (Stage) chkbox_email.getScene().getWindow();
        stage.close();
    }

}
