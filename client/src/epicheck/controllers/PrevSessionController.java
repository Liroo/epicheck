package epicheck.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import epicheck.apimodels.Activity;
import epicheck.apimodels.Student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by jean on 10/26/16.
 */
public class PrevSessionController extends AbstractSession implements Initializable {
    private JSONObject activity_json;

    @FXML
    AnchorPane root;

    @FXML
    JFXButton btn_export;

    @FXML
    private JFXTreeTableView table;


    @FXML
    private void export() {
        // TODO: 10/26/16 : need to export the student list in csv
        Platform.runLater(() -> new JFXSnackbar(root).show("Not implemented yet", 2000));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setParams(Activity acti, JSONObject res) {
        this.activity_json = res;

        JFXTreeTableColumn<Student, String> email = new JFXTreeTableColumn<>("Email");
        JFXTreeTableColumn<Student, String> check = new JFXTreeTableColumn<>("Validation");

        email.setCellValueFactory(param -> param.getValue().getValue().getEmail());
        check.setCellValueFactory(param -> param.getValue().getValue().getDate());

        email.setEditable(true);
        check.setEditable(true);

        email.setMaxWidth(260);
        check.setMinWidth(200);

        ObservableList<Student> students = FXCollections.observableArrayList();

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
}
