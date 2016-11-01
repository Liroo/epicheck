package epicheck.controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import epicheck.utils.ApiRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Kevin on 30/10/2016.
 */
public class StudentActivitiesController implements Initializable {

    @FXML
    private JFXTreeTableView tableView;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Label titleLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private JFXTextField searchField;

    private ObservableList<Activity> activities;
    private ObservableList<Activity> searchActivities;

    private JSONArray activities_json;
    private Student student;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Welcome in student's activities");

        JFXTreeTableColumn<Activity, String> name = new JFXTreeTableColumn<>("Activité");
        JFXTreeTableColumn<Activity, String> module = new JFXTreeTableColumn<>("Module");
        JFXTreeTableColumn<epicheck.apimodels.Activity, String> presence = new JFXTreeTableColumn<>("Présence");

        name.setMinWidth(360);
        presence.setMinWidth(200);
        name.setMaxWidth(360);
        presence.setMaxWidth(200);
        module.setMinWidth(270);
        module.setMaxWidth(270);


        name.setCellValueFactory(param -> param.getValue().getValue().getActiTitle());
        module.setCellValueFactory(param -> param.getValue().getValue().getModuleTitle());
        presence.setCellValueFactory(param -> param.getValue().getValue().getDatePresence());

        activities = FXCollections.observableArrayList();
        searchActivities = FXCollections.observableArrayList();

        final TreeItem<Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
        tableView.getColumns().setAll(name, module, presence);
        tableView.setShowRoot(false);
    }

    public void updateController() {
        final Circle clip = new Circle(35.0D, 35.0D, 35);
        profilePicture.setImage(new Image(student.getPictureUrl().get()));
        profilePicture.setClip(clip);
        titleLabel.setText(student.getTitle().get());
        yearLabel.setText(student.getStudentYear().get() + "ème année");
        ApiRequest.get().getStudentByEmail(student.getEmail().get(), new ApiRequest.JSONObjectListener() {
            @Override
            public void onComplete(JSONObject res) {
                Platform.runLater(() -> {
                try {
                    activities_json = res.getJSONArray("activities");
                    for (int i = 0 ; i < activities_json.length() ; i++) {
                        JSONObject obj = activities_json.getJSONObject(i);
                        Activity newAct = new Activity(obj.getString("actiTitle"), obj.getString("moduleTitle"), obj.getString("dateFrom"), obj.getString("dateTo"),
                                obj.getString("scholarYear"), obj.getString("codeModule"), obj.getString("codeInstance"), obj.getString("codeActi"), obj.getString("codeEvent"));
                        JSONObject presenceObj = obj.getJSONObject("presence");
                        newAct.setDatePresence(presenceObj.getString("date"));
                        activities.add(newAct);
                    }
                    TreeItem<Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
                    tableView.setRoot(root);
                    tableView.setShowRoot(false);
                    tableView.getSelectionModel().clearSelection();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                });
                System.out.println("res = " + res);
            }

            @Override
            public void onFailure(String err) {
                System.out.println("err = " + err);
            }
        });
    }

    public void searchFilter(Event event) throws JSONException {
        searchActivities.clear();
        tableView.getSelectionModel().clearSelection();

        if (searchField.getText().length() == 0) {
            final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(activities, RecursiveTreeObject::getChildren);
            Platform.runLater(() -> tableView.setRoot(root));
            return ;
        }
        for (int i = 0; i < activities_json.length(); i++) {
            JSONObject obj = activities_json.getJSONObject(i);
            if (obj.getString("moduleTitle").toLowerCase().contains(searchField.getText().toLowerCase()) || obj.getString("actiTitle").toLowerCase().contains(searchField.getText().toLowerCase())) {
                Activity newAct = new epicheck.apimodels.Activity(obj.getString("actiTitle"), obj.getString("moduleTitle"), obj.getString("dateFrom"), obj.getString("dateTo"),
                        obj.getString("scholarYear"), obj.getString("codeModule"), obj.getString("codeInstance"), obj.getString("codeActi"), obj.getString("codeEvent"));
                JSONObject presenceObj = obj.getJSONObject("presence");
                newAct.setDatePresence(presenceObj.getString("date"));
                searchActivities.add(newAct);
            }
        }
        final TreeItem<epicheck.apimodels.Activity> root = new RecursiveTreeItem<>(searchActivities, RecursiveTreeObject::getChildren);
        Platform.runLater(() -> {
            tableView.setRoot(root);
            tableView.setShowRoot(false);
        });
    }

    public void setStudent(Student st) {
        student = st;
        updateController();
    }
}
