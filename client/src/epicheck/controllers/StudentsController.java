package epicheck.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mb3364.http.HttpResponseHandler;
import epicheck.Main;
import epicheck.apimodels.Student;
import epicheck.utils.ApiRequest;
import epicheck.utils.ApiUtils;
import epicheck.utils.Preferences;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static epicheck.utils.ApiUtils.RequestType.GET;

/**
 * Created by Kevin on 17/10/2016.
 */
public class StudentsController implements Initializable {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Circle circleClip;

    @FXML
    private ImageView profilePicture;

    @FXML
    private JFXTreeTableView studentTableView;

    @FXML
    private JFXTextField searchField;

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
    private ObservableList<Student> searchStudents;

    private JSONArray student_json;

    private ArrayList<Label> markLabel;
    private ArrayList<Label> markValLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        markLabel = new ArrayList();
        markValLabel = new ArrayList();

        markLabel.add(firstNoteLabel);
        markLabel.add(secondNoteLabel);
        markLabel.add(thirdNoteLabel);
        markValLabel.add(firstNoteValLabel);
        markValLabel.add(secondNoteValLabel);
        markValLabel.add(thirdNoteValLabel);

        Platform.runLater(() -> {
            final Circle clip = new Circle(55.0D, 55.0D, 55);
//            profilePicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/profilview/kevin.empociello.jpg"));
            profilePicture.setClip(clip);

        });

        JFXTreeTableColumn<Student, String> login = new JFXTreeTableColumn<>("Login");
        login.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        login.setEditable(true);

        login.setMinWidth(580);

        students = FXCollections.observableArrayList();
        searchStudents = FXCollections.observableArrayList();

        ApiRequest.get().getStudents(new ApiRequest.JSONArrayListener() {
            @Override
            public void onComplete(JSONArray res) {
                Platform.runLater(() -> {
                    student_json = res;
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
                Platform.runLater(() -> new JFXSnackbar(rootPane).show("Erreur pendant le téléchargement de la liste des étudiants", 3000));
            }
        });

        studentTableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            RecursiveTreeItem selectRow = (RecursiveTreeItem) studentTableView.getSelectionModel().getSelectedItem();
            if (selectRow != null) {
                Student student = (Student) selectRow.getValue();
                updateStudentInfo(student);
            }
        });
    }

    public void updateLastStudentMarks(Student student) {
        ApiRequest.get().getStudentMarks(student, new ApiRequest.JSONArrayListener() {

            @Override
            public void onComplete(JSONArray res) {
                Platform.runLater(() -> {
                    try {
                        for (int i = 0; i < 3; i++) {
                            JSONObject tmp = res.getJSONObject(i);
                            markLabel.get(i).setText(tmp.getString("title"));
                            markValLabel.get(i).setText("" + tmp.getInt("final_note"));
                        }
                    } catch (Exception e) {
                        System.out.println("Failed parsing JSON");
                    }
                });
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = " + err);
            }
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
                        student.setTitle(obj.getString("title"));
                        student.setPictureUrl("https://cdn.local.epitech.eu/userprofil/profilview/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".jpg");
                        student.setStudentYear(obj.getString("studentyear"));
                        nameLabel.setText(obj.getString("title"));
                        yearLabel.setText(obj.getString("studentyear") + "ème année");
                        creditsLabel.setText(obj.getString("credits"));
                        gpaLabel.setText(obj.getJSONArray("gpa").getJSONObject(0).getString("gpa"));
                        profilePicture.setImage(new Image("https://cdn.local.epitech.eu/userprofil/profilview/" + studentEmail.substring(0, studentEmail.indexOf('@')) + ".jpg"));
                        if (obj.has("nsstat")) {
                            logLabel.setText(obj.getJSONObject("nsstat").getString("active"));
                        } else {
                            logLabel.setText("0");
                        }
                        updateLastStudentMarks(student);
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

    private void launchWindow(String scene_title, Parent root)
    {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(ActivityController.class.getResource("/resources/css/jfoenix-components.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(scene_title);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(Main.primaryStage);
        stage.show();
        stage.setOnCloseRequest(we -> {
            Main.mainController.paramsController.refresh();
        });
    }

    @FXML
    public void studentActivities() throws IOException {
        FXMLLoader loader = new FXMLLoader(ActivityController.class.getResource("/epicheck/views/student_activities.fxml"));
        Parent root = loader.load();
        StudentActivitiesController t = loader.getController();
        RecursiveTreeItem selectRow = (RecursiveTreeItem) studentTableView.getSelectionModel().getSelectedItem();
        if (selectRow != null) {
            Student student = (Student) selectRow.getValue();
            try {
                t.setStudent((Student) student.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            launchWindow("Activités de l'étudiant", root);
        } else {
            Platform.runLater(() -> new JFXSnackbar(rootPane).show("Veuillez selectionner un étudiant", 3000));
        }
    }

    public void searchFilter(Event event) throws JSONException{
        searchStudents.clear();
        studentTableView.getSelectionModel().clearSelection();


        if (searchField.getText().length() == 0) {
            final TreeItem<epicheck.apimodels.Student> root = new RecursiveTreeItem<>(students, RecursiveTreeObject::getChildren);
            Platform.runLater(() -> studentTableView.setRoot(root));
            return ;
        }
        for (int i = 0; i < student_json.length(); i++) {
            JSONObject obj = student_json.getJSONObject(i);
            if (obj.getString("email").toLowerCase().contains(searchField.getText().toLowerCase())) {
                searchStudents.add(new Student(obj.getString("email"), ""));
            }
        }
        final TreeItem<epicheck.apimodels.Student> root = new RecursiveTreeItem<>(searchStudents, RecursiveTreeObject::getChildren);
        Platform.runLater(() -> studentTableView.setRoot(root));
    }
}
