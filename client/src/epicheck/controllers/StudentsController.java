package epicheck.controllers;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mb3364.http.HttpResponseHandler;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import epicheck.utils.ApiRequest;
import epicheck.utils.ApiUtils;
import epicheck.utils.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.DocFlavor;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static epicheck.utils.ApiUtils.RequestType.GET;

/**
 * Created by Kevin on 17/10/2016.
 */
public class StudentsController implements Initializable {

    @FXML
    private Circle circleClip;

    @FXML
    private ImageView profilePicture;

    @FXML
    private JFXTreeTableView studentTableView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Label logLabel;

    @FXML
    private Label creditsLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label firstNoteLabel;

    @FXML
    private Label firstNoteValLabel;

    @FXML
    private Label secondNoteLabel;

    @FXML
    private Label secondNoteValLabel;

    @FXML
    private Label thirdNoteLabel;

    @FXML
    private Label thirdNoteValLabel;

    private ObservableList<Student> students;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            final Circle clip = new Circle(55.0D, 55.0D, 55);
            profilePicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/profilview/kevin.empociello.jpg"));
            profilePicture.setClip(clip);

        });

        JFXTreeTableColumn<Student, String> login = new JFXTreeTableColumn<>("Login");
        login.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        login.setEditable(true);

        login.setMinWidth(580);

        students = FXCollections.observableArrayList();

        ApiRequest.get().getStudents(new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                Platform.runLater(() -> {
                    try {
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject obj = res.getJSONObject(i);
                            students.add(new Student(obj.getString("email"), ""));
                        }
                    } catch (Exception e) {
                        System.out.println("Failed parsing JSON");
                        //Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur, veuillez recharger le calendrier", 3000));
                    }
                    TreeItem<Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
                    studentTableView.getColumns().setAll(login);
                    studentTableView.setRoot(root);
                    studentTableView.setShowRoot(false);
                });
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = [" + err + "]");
            }
        });

        studentTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            RecursiveTreeItem selectRow = (RecursiveTreeItem) studentTableView.getSelectionModel().getSelectedItem();
            Student student = (Student) selectRow.getValue();
            updateStudentInfo(student);
        });
    }

    public void updateStudentInfo(Student student) {
        String URL = "https://intra.epitech.eu/" + Preferences.get().getAutoLogin() + "/user/" + student.getEmail().get() + "?format=json";
        System.out.println("URL = " + URL);
        ApiUtils.get().exec(GET, URL, new HttpResponseHandler() {
            @Override
            public void onSuccess(int i, Map<String, List<String>> map, byte[] bytes) {
                Platform.runLater(() -> {
                    try {
                        JSONObject obj = new JSONObject(new String(bytes));
                        String studentEmail = student.getEmail().get();
                        nameLabel.setText(obj.getString("title"));
                        yearLabel.setText(obj.getString("studentyear") + "ème année");
                        creditsLabel.setText(obj.getString("credits"));
                        gpaLabel.setText(obj.getJSONArray("gpa").getJSONObject(0).getString("gpa"));
                        logLabel.setText(obj.getJSONObject("nsstat").getString("active"));
                        profilePicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/profilview/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".jpg"));
                  } catch (JSONException e) {
                        e.printStackTrace();
                  }
                });
            }

            @Override
            public void onFailure(int i, Map<String, List<String>> map, byte[] bytes) {
                String err = (new String(bytes));
                System.out.println(err);
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }
}
